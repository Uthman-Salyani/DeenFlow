# DeenFlow — Project Progress Documentation

**Package name:** `com.uthman.deenflow`
**Platform:** Android (Kotlin, Jetpack Compose)
**Type:** Offline Islamic companion app — Quran, Hadith, Tasbih, Hijri Calendar

---

## 1. Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Navigation | Compose Navigation |
| Database | Room (SQLite), pre-populated from bundled JSON assets |
| JSON Parsing | kotlinx.serialization |
| State | ViewModel + StateFlow |
| Widget | Native `AppWidgetProvider` + `RemoteViews` |
| Local Settings/Bridge | SharedPreferences |
| Alarms/Notifications | `AlarmManager` (exact alarms) + `BroadcastReceiver`, NOT WorkManager (see notes) |
| Build | Gradle (Kotlin DSL), KSP (not kapt) |
| Kotlin version | 2.2.10 |

---

## 2. Package Structure

```
com.uthman.deenflow/
├── data/
│   ├── calendar/          → HijriDate, CalendarRepository, HijriCalendarCalculator, HijriMonths
│   ├── local/
│   │   ├── entity/        → SurahEntity, AyahEntity, HadithBookEntity, HadithEntity
│   │   ├── dao/            → SurahDao, AyahDao, HadithBookDao, HadithDao
│   │   ├── seed/           → DatabaseSeeder
│   │   └── AppDatabase.kt
│   └── repository/        → QuranRepository, HadithRepository
├── navigation/             → Screen.kt, DeenFlowNavGraph.kt
├── notifications/          → NotificationHelper, AlarmScheduler, SunsetAlarmReceiver,
│                             BootReceiver, DeenFlowWidgetProvider
├── ui/
│   ├── home/               → HomeScreen (placeholder only, not built)
│   ├── quran/               → QuranScreen, QuranViewModel, AyahReaderScreen, AyahReaderViewModel
│   ├── hadith/               → HadithScreen, HadithViewModel, HadithReaderScreen, HadithReaderViewModel
│   ├── tasbih/                → TasbihScreen, TasbihViewModel
│   ├── calendar/               → CalendarScreen, CalendarViewModel, CalendarInputCards
│   └── theme/                  → (default Compose template theme, not yet customized)
└── MainActivity.kt
```

---

## 3. Progress Log

### Step 1 — Project Setup ✅
- New Compose project, minSdk 26, Kotlin DSL build files.
- Verified running on both emulator and physical device (Vivo phone).

### Step 2 — Navigation Shell ✅
- `Screen.kt`: sealed class defining 5 routes/labels/icons (Home, Quran, Hadith, Tasbih, Calendar) — single source of truth for the bottom nav.
- `DeenFlowNavGraph.kt`: `Scaffold` + `NavigationBar` + `NavHost`, 5 placeholder screens wired up.
- Dependencies added: `navigation-compose`, `material-icons-extended`.

### Step 3 — Room Database ✅
- 4 entities: `SurahEntity`, `AyahEntity` (Uthmani + Indo-Pak text, translation, juz, bookmark flag), `HadithBookEntity`, `HadithEntity` (Arabic, narrator, translation, bookmark flag).
- 4 DAOs with Flow-returning queries for reactive UI updates.
- `AppDatabase` — singleton via companion object (`getInstance(context)`), Room + KSP set up (KSP version must match Kotlin version exactly: `2.2.10-2.0.2`).
- Known gotcha: `android.disallowKotlinSourceSets=false` needed in `gradle.properties` for KSP to work with newer AGP.

### Step 4 — Data Sourcing ✅
Sourced from **QUL (qul.tarteel.ai)** — Tarteel's Quranic Universal Library, a developer resource platform:
- Uthmani script (ayah-by-ayah)
- Indo-Pak script (ayah-by-ayah)
- Saheeh International translation ("simple" format — no footnotes)
- Juz boundary metadata (`verse_mapping` ranges per juz)
- Surah metadata (`chapters` resource: Arabic name, transliteration, verse count, revelation place) — English surah names hand-supplied (standard, well-known list) since QUL's export didn't include them.

Hadith sourced from **`AhmedBaset/hadith-json`** on GitHub — Sahih Bukhari (97 books, 7277 hadiths) + Sahih Muslim (57 books incl. "Book 0: Introduction", 7459 hadiths).

Merged locally via Python scripts (`merge.py`, `merge_surahs.py`, `merge_hadith.py`) into 4 clean JSON files matching entity structure exactly:
- `ayahs.json` — 6,236 rows
- `surahs.json` — 114 rows
- `hadith_books.json` — 154 rows (synthetic IDs: `collectionCode * 1000 + chapterId`)
- `hadiths.json` — 14,736 rows

### Step 5 — Data Seeding ✅
- 4 JSON files bundled in `app/src/main/assets/`.
- `DatabaseSeeder.seedIfNeeded()` — reads assets via kotlinx.serialization (`@Serializable` on all entities), inserts into Room, guarded by a `SharedPreferences` flag (`is_database_seeded`) so it only runs once ever.
- Called from `MainActivity.onCreate()` via `lifecycleScope.launch`.
- Verified via Database Inspector + manual `SELECT COUNT(*)` queries.

### Step 6 — Quran Module ✅
- `QuranRepository` → `QuranViewModel` (surah list) → `QuranScreen` (`LazyColumn`, tap → navigate).
- `AyahReaderViewModel` + `AyahReaderScreen` — parameterized nav route `ayah_reader/{surahNumber}`.
- Uthmani ayahs display a traditional end-of-ayah marker: `۝` + Arabic-Indic numeral (custom `toArabicIndicNumeral()` helper).
- **Known deferred issue:** surah list scroll is laggy (ayah reader scrolls fine) — not yet root-caused, planned for a later performance pass.

### Step 7 — Hadith Module ✅
- `HadithRepository` → `HadithViewModel` → `HadithScreen` with `FilterChip` tab switcher (Saheeh Bukhari / Saheeh Muslim).
- `HadithReaderViewModel` + `HadithReaderScreen` — parameterized nav route `hadith_reader/{bookId}`.
- `HadithEntity` includes a separate `narrator` field (kept apart from `translationEn` specifically so a future "Hadith of the Week" home-screen feature can show clean hadith text without the narrator line).

### Step 8 — Tasbih Module ✅
Rebuilt from a simple counter into a full tasbih.org-style tool:
- Persistent count (SharedPreferences-backed, survives app restart — only Reset zeroes it).
- Custom increment step selector (+1/+2/+3/+5/+10/+33/+100 dropdown).
- Decrement ("undo one") button.
- Manual edit-count dialog.
- Goal-setting with **vibration** (`VibrationEffect`) + a fading "Goal reached! 🎉" banner, fired exactly once when the count crosses the goal.
- Minimalist/fullscreen toggle button (hides all chrome except count + circle).

### Step 9 — Calendar Module ✅
**Core design decision:** date persistence uses an **anchor system** (last confirmed Hijri date + the real-world timestamp it was confirmed at), with the current date always *recalculated* from that anchor + elapsed time whenever needed — never relying on a continuously-running background tick. This means the correct date shows up even after the phone was off or the app wasn't opened for a long time.

- `HijriDate` (data class), `CalendarRepository` (SharedPreferences storage for anchor + sunset time), `HijriCalendarCalculator` (pure function: walks forward sunset-boundary by sunset-boundary from the anchor, caps at day 29 and sets `needsConfirmation = true` rather than auto-advancing past it; day 30 auto-rolls to day 1 of next month on the next boundary, since no lunar month exceeds 30 days). Logic validated with a Python simulation before porting to Kotlin.
- **Important bug fixed:** every `refresh()` now re-checkpoints the anchor to the just-computed date + "now" — otherwise editing settings (like sunset time) after days had elapsed would retroactively reinterpret the whole elapsed period under the new settings, causing the displayed date to jump backward unexpectedly.
- `CalendarScreen` — redesigned to match a provided mockup: dark-green/gold "Today's Date is" card (currently default-colored, full theme deferred), "Set Hijri Date" card (DD/MM/YYYY plain number fields), "Daily Update Time (Sunset)" card with a **custom AM/PM dropdown** (12-hour input, converted to 24-hour internally).
- 29th-day confirmation UI: "Confirm 30th" / "Confirm New Month" buttons appear inline on the date card when `needsConfirmation` is true.

**Notifications (AlarmManager, not WorkManager):**
- `NotificationHelper` (channel + notification builder), `AlarmScheduler` (`setExactAndAllowWhileIdle`, self-rescheduling daily), `SunsetAlarmReceiver` (recalculates date, checkpoints anchor, notifies only if day 29 reached, reschedules next day), `BootReceiver` (reschedules alarm after phone reboot, since exact alarms don't survive reboot).
- Runtime `POST_NOTIFICATIONS` permission request wired into `CalendarScreen` (Android 13+).
- **Confirmed working via direct-call test** (calling the receiver's logic directly bypasses broadcast delivery) — the underlying logic, calculation, and notification-building are all correct.
- **⏸️ ON HOLD:** the real background alarm does not reliably fire on Uthman's Vivo phone (OriginOS). Suspected OEM background-restriction issue (Vivo/BBK phones are known for aggressively killing background alarms), though Uthman notes his previous, older calendar app *did* notify reliably on the same phone — so this isn't fully settled. Two untried next steps if revisited: (1) check DeenFlow's specific entry in Vivo's Battery/Autostart permission settings, (2) switch from `setExactAndAllowWhileIdle()` to `setAlarmClock()`, which Android exempts from Doze/battery restrictions almost entirely (minor tradeoff: shows a small alarm-clock icon in the status bar).

### Step 10 — Home Screen Widget ✅
- `DeenFlowWidgetProvider` (`RemoteViews`-based — Compose cannot be used in widgets) reads the same `CalendarRepository` anchor data as the in-app screen, shows live Hijri date.
- Widget refreshes on date/settings changes and via the alarm receiver (`updateAllWidgets()`).
- **Design evolution:** started with plain text → styled to match in-app date card (gold/cream, moon icon via layered shape-drawables) → replaced moon icon with the real app launcher icon + "DeenFlow" text (per user preference, animated moon-phase idea dropped entirely) → background upgraded from procedural shape-drawables to a real mosque-silhouette **SVG** (imported as Android Vector Drawable, tinted gold `#D4AF37`, ~14% opacity, layered behind content via `FrameLayout`), scaled up via `scaleX`/`scaleY` to bleed past the widget's edges and eliminate a visible built-in canvas-margin gap in the source SVG.
- App's actual launcher icon (`ic_launcher`/`ic_launcher_round`) regenerated from the real logo artwork via Android Studio's Image Asset wizard — this updates the home screen icon, app drawer icon, AND the widget's icon reference all from one source.
- **Bug fixed:** Gregorian date text was 2-line (`"MMM dd\nyyyy"`), causing overflow past the widget's rounded corners if resized shorter than expected. Changed to single-line (`"MMM dd, yyyy"`) + added `maxLines="1"` / `ellipsize="end"` as a safety net.
- Widget's static visual design considered **fully finished**.

### Step 11 — Quran Page Polish 🔶 IN PROGRESS
Based on a provided mockup, planned features:
1. Header (back button, logo, "Quran" title, subtitle, decorative divider) — not yet built
2. "Continue Reading" card — tracks reading position **automatically every time an ayah is opened** in the reader (decided, not yet built — needs new tracking logic)
3. **Juz/Surah tab switcher** (Page tab dropped — no page-number data exists in the schema; this was a deliberate earlier decision, see Known Deferred Items below)
4. Hexagon-outlined number badges on list rows (currently plain numbers) — not yet built
5. Future search feature covering Juz/Surah/Ayah — not yet built

**Progress so far on this step:**
- `AyahDao.getAyahsForJuz(juzNumber)` and `getJuzStartAyahs()` queries added.
- `QuranRepository` extended with matching methods.
- `QuranScreen` rebuilt with a Juz/Surah `FilterChip` tab switcher — Surah tab unchanged/working; Juz tab shows all 30 juz with a subtitle ("Starts at [Surah Name], Ayah [N]"), tapping opens that starting surah's reader from ayah 1 (not yet scrolled to the exact starting ayah — known limitation, reasonable follow-up polish item).

**🔶 CURRENT DECISION POINT (mid-implementation):** Uthman requested upgrading the reading experience to include a genuine **Mushaf-style paginated view** — swipeable pages resembling a real printed Quran, with continuous flowing Arabic text (not per-ayah blocks), first-ayah-of-a-new-Juz highlighted, and **sajda (prostration) markers** shown beside relevant ayahs. A separate "Translation" tab/mode would keep the current ayah-by-ayah-with-translation format.

Two implementation paths were discussed:
- **Simple version:** our own arbitrary page boundaries, flowing text, swipeable — achievable with data already on hand.
- **True Mushaf-accurate version:** exact same 604 pages / same line breaks as a real printed Mushaf — requires genuinely new, specialized data.

**Decision: Uthman chose the true Mushaf-accurate version**, specifically because sajda markers and similar print-accurate annotations matter to him.

**Research completed (not yet implemented):** QUL (the same source used for Steps 4/9/10) provides exactly this kind of data:
- **Mushaf Layout resource** — a `pages` table (SQLite/JSON export) with one row per *line* of the Mushaf (not per ayah), containing `mushaf_id`, `page_number`, `line_number`, `line_type` (`ayah` / `surah_name` / `basmallah` etc.), `is_centered`, `surah_number` (for header lines), `first_word_id`/`last_word_id` (for ayah lines — references into a word-level Quran script export). The Madinah Mushaf (KFGQPC V1, 1405H print) has 604 pages, ~15 lines/page, ~9,046 total line records. Pages 1-2 have only 8 lines due to decorated surah headers.
- **Glyph-based fonts (QPC V1/V2)** — specialized fonts where each glyph represents an entire *word* (not per-letter), page-specific: **604 separate font files**, one per Mushaf page, needed to render the exact same line-wrapping as print. Font integration for Android/mobile is explicitly **not** covered by QUL's web-focused docs — will need platform-specific handling, not yet researched.
- Sajda ayah data — not yet located/confirmed as a specific QUL resource; still needs to be found.

**Not yet started:** sourcing the actual layout data files, sourcing/integrating the 604 page-specific fonts on Android specifically (open question — web font-face techniques from QUL's docs don't directly apply), designing new Room tables for page/line layout data, sourcing sajda ayah data, and building the actual swipeable Compose UI (likely `HorizontalPager`).

This is understood to be a **large, standalone feature** — comparable in scope to the original Step 4 (Quran data sourcing) or bigger, given the added font-integration complexity specific to Android.

---

## 4. Known Deferred / Parked Items

| Item | Status | Notes |
|---|---|---|
| Surah list scroll lag | Deferred | Ayah reader scrolls fine; not yet root-caused. Planned for a dedicated performance pass. |
| Footnote/explanation popup (tap a marker → see explanation) | Deferred | Wanted for both Quran and Hadith translation text. Needs a footnoted translation data source — deliberately avoided during Step 4 data sourcing (chose the "simple" no-footnote translation format specifically to sidestep parsing complexity at the time). |
| Page numbers in Quran data | Dropped entirely | Bookmarks use juz + verse number instead. Real print page numbers are Mushaf-edition-specific and no clean per-ayah source was found; superseded anyway once the Step 11 true-Mushaf-layout approach is built, since that will include real page data as part of the layout resource. |
| Notification reliability on Vivo phone | On hold | See Step 9 notes above — real root cause not fully confirmed (OEM restriction vs. something else), two untried fixes identified. |
| Home screen | Not started | Placeholder only. |
| Settings/user page | Not started | Not designed yet. |
| Full app-wide theme pass (white/gold color scheme) | Not started | Deliberately deferred until all screens exist, so it can be applied consistently in one pass rather than piecemeal. Calendar screen and widget already hand-styled ahead of this pass as exceptions (mockup-driven). |
| Hadith page polish | Not started | Mentioned as wanted, specifics not yet discussed. |

---

## 5. Data Sources & Credits

- **QUL — Quranic Universal Library** (`qul.tarteel.ai`, run by Tarteel): Uthmani script, Indo-Pak script, Saheeh International translation, Juz metadata, Surah (chapters) metadata, Mushaf layout data, glyph-based fonts (for planned Step 11 work).
- **`AhmedBaset/hadith-json`** (GitHub): Sahih Bukhari and Sahih Muslim text, Arabic + English + narrator.
- Mosque-silhouette SVG: user-supplied file (`Mosque-Silhouette.svg`), source/license not documented here — confirm before any public release.
- English surah names: standard, widely-published list (not from a specific downloaded file).

---

## 6. Environment Notes

- Fedora 44, Android Studio + SDK already installed prior to project start.
- `adb` not on PATH by default on Fedora — full path used: `~/Android/Sdk/platform-tools/adb`.
- KSP version must exactly match Kotlin version (`2.2.10` → `2.2.10-2.0.2`), unlike the Kotlin serialization plugin (`org.jetbrains.kotlin.plugin.serialization`), which shares the main Kotlin version directly with no separate suffix.
- `android.disallowKotlinSourceSets=false` required in `gradle.properties` — known interaction between KSP and newer AGP versions.
