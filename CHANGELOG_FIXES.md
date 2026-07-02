# WARRIOR — Bug Fixes & UI Cleanup

## 🐛 Bug Fixes

### CRITICAL — AI features were completely broken
- **`core/ai/SystemVoiceEngine.kt`** (lines 62, 461): model id `gemini-3.5-flash` → `gemini-1.5-flash`.
  That model id does not exist, so every AI call (daily briefing, mission completion,
  rank-up speech, XION chat) failed with a 400/404.
- **`presentation/analytics/WeeklyAnalyticsViewModel.kt`** (line 88): same `gemini-3.5-flash` → `gemini-1.5-flash`.

### HIGH — Mission status filtering was fragile
- **`ui/MissionsViewModel.kt`** (lines 81–83): status comparison was case-sensitive and
  only matched `"ACTIVE"/"Active"` and `"COMPLETED"/"Completed"`. Any other casing or
  stray whitespace silently dropped missions into "pending".
  Now normalized with `.uppercase().trim()`.

## 🧹 UI / Code Cleanup

### Dead code removed
- **`presentation/systemvoice/SystemVoiceScreen.kt`**: removed a 255-line "Coming Soon"
  block that was permanently disabled behind `val IS_AI_SCREEN_CLOSED = false`.
  File went from 890 → 635 lines. The lock logic now lives in a single
  `if (hunterLevel < 4 && !isDevBypass) { … } else { … }` branch.

### Logic moved out of the UI layer
- **`presentation/shadow/ShadowArmyScreen.kt`**: moved `SortOption` enum and the
  12-branch `getShadowStory()` content function into a new file
  **`presentation/shadow/ShadowArmyHelpers.kt`** (same package, no import changes needed).
  The screen file now only handles rendering.

### Design-system font consistency
- **`presentation/financial/FinancialCheckpointScreen.kt`** (7 spots) and
  **`presentation/review/WeeklyReviewScreen.kt`** (1 spot): replaced raw
  `FontFamily.Monospace` with the theme token `JetBrainsMono`. Now if the mono font is
  ever swapped for a real typeface, these screens pick it up automatically.

## 📦 Housekeeping
- Removed macOS junk (`__MACOSX/`, `.DS_Store`).

## ⚠️ Notes / not changed (intentionally)
- **Placeholder fonts**: the 10 `.ttf` files in `app/src/main/res/font/` are 524-byte
  placeholders (not referenced in code — `Type.kt` uses system font families). The Gradle
  `downloadFonts` task regenerates real ones. Safe to delete if you need to drop the
  project under a file-count limit.
- **Large composables** (`MissionDetailScreen` ~832 lines, `LeaguesScreen` ~648 lines)
  were left structurally intact. Splitting them is worthwhile but should be done with a
  live build/preview to verify — this pass only applied changes verified by static reading.

> All edits were verified for brace/paren balance, but the project was not compiled in
> this environment. Build once on your side before shipping.
