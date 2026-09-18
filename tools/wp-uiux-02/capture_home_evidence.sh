#!/usr/bin/env bash
#
# WP-UIUX-02 hardened Home evidence capture.
#
# Fixes the evidence defect where captures 55-63 were taken while Splash/Onboarding
# was still on screen (invalid "Home" evidence). Every capture taken by this script:
#
#   1. installs the freshly built debug APK (so evidence always matches the fixed build),
#   2. forces a cold start into a deterministic state (UI language via axiom_lang,
#      font scale via `settings put system font_scale`, theme via `cmd uimode night`),
#   3. waits until the UI tree PROVES Home is rendered (dock + hero label in the
#      expected language) before the shutter,
#   4. auto-rejects any frame whose UI tree still contains Splash/Onboarding markers
#      ("Begin" / "شروع"),
#   5. logs the full state (apex of same commands) next to each image, including the
#      observed axiom_lang value and the DataStore language/theme values.
#
# Usage:
#   tools/wp-uiux-02/capture_home_evidence.sh            # run the full WP matrix
#   tools/wp-uiux-02/capture_home_evidence.sh --restore  # restore emulator defaults only
#
set -euo pipefail

SERIAL="${SERIAL:-emulator-5554}"
PKG="com.axiom.app"
ACTIVITY="${PKG}/.MainActivity"
APK="${APK:-app/build/outputs/apk/debug/app-debug.apk}"
OUT="${OUT:-docs/reviews/screenshots/wp-uiux-02}"
LOGDIR="${LOGDIR:-docs/reviews/evidence-logs/wp-uiux-02}"
SWIPE_X="${SWIPE_X:-540}"
SWIPE_TOP="${SWIPE_TOP:-1700}"
SWIPE_BOTTOM="${SWIPE_BOTTOM:-700}"

ADB=(adb -s "$SERIAL")
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

mkdir -p "$OUT" "$LOGDIR"
RUNLOG="$LOGDIR/capture_run_$(date '+%Y%m%d_%H%M%S').log"
: > "$RUNLOG"

log() { printf '%s %s\n' "$(date '+%H:%M:%S')" "$*" | tee -a "$RUNLOG"; }

# ── State helpers ────────────────────────────────────────────────────────────

state_dump() {
    {
        echo "--- state @ $(date '+%F %T') ---"
        echo "font_scale: $("${ADB[@]}" shell settings get system font_scale | tr -d '\r')"
        echo "night_mode: $("${ADB[@]}" shell cmd uimode night | tr -d '\r')"
        echo -n "axiom_lang: "
        "${ADB[@]}" shell run-as "$PKG" cat shared_prefs/axiom_lang.xml 2>/dev/null | tr -d '\r' || echo "(missing)"
        echo
        echo -n "datastore:  "
        "${ADB[@]}" exec-out run-as "$PKG" cat files/datastore/axiom_prefs.preferences_pb > "$TMP/ds.pb" 2>/dev/null &&
            python3 tools/wp-uiux-02/read_datastore_prefs.py "$TMP/ds.pb" | grep -E "^(language|theme_mode)=" | tr '\n' ' ' || echo "(unreadable)"
        echo
    } >> "$RUNLOG"
}

set_language() {
    local lang="$1"
    printf '%s' "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?><map><string name=\"lang\">${lang}</string></map>" > "$TMP/axiom_lang.xml"
    "${ADB[@]}" push "$TMP/axiom_lang.xml" /data/local/tmp/wp_axiom_lang.xml >/dev/null
    "${ADB[@]}" shell chmod 644 /data/local/tmp/wp_axiom_lang.xml
    "${ADB[@]}" shell run-as "$PKG" cp /data/local/tmp/wp_axiom_lang.xml shared_prefs/axiom_lang.xml
    "${ADB[@]}" shell rm /data/local/tmp/wp_axiom_lang.xml
    log "state: UI language -> $lang"
}

set_font_scale() {
    "${ADB[@]}" shell settings put system font_scale "$1"
    log "state: font_scale -> $1"
}

set_night() {
    "${ADB[@]}" shell cmd uimode night "$1" >/dev/null
    log "state: night mode -> $1"
}

launch_app() {
    "${ADB[@]}" shell am force-stop "$PKG"
    sleep 1
    "${ADB[@]}" shell am start -n "$ACTIVITY" >/dev/null
}

# ── UI-tree evidence helpers ─────────────────────────────────────────────────

dump_ui() {
    local tries=0
    while [ $tries -lt 3 ]; do
        if "${ADB[@]}" shell uiautomator dump /sdcard/wp_ui.xml >/dev/null 2>&1 &&
            "${ADB[@]}" shell cat /sdcard/wp_ui.xml > "$TMP/ui.xml" 2>/dev/null &&
            grep -q "<hierarchy" "$TMP/ui.xml"; then
            return 0
        fi
        tries=$((tries + 1))
        sleep 2
    done
    return 1
}

expected_markers() {
    case "$1" in
    en)
        HOME_DOCK="HOME"; HOME_HERO="NEXT MEANINGFUL MISSION"
        HOME_SECTIONS="TODAY|XION|PROGRESS"
        ;;
    fa)
        HOME_DOCK="خانه"; HOME_HERO="مأموریت معنادار بعدی"
        HOME_SECTIONS="امروز|زیون|پیشرفت"
        ;;
    *) log "FATAL: unknown language '$1'"; exit 2 ;;
    esac
}

# Splash / Onboarding reject markers: the primary CTA label rendered only by the
# launch splash and the language/theme setup onboarding.
has_reject_marker() {
    grep -qE 'text="(Begin|شروع)"' "$TMP/ui.xml"
}

is_home_visible() {
    grep -q "text=\"$HOME_DOCK\"" "$TMP/ui.xml" &&
        grep -q "text=\"$HOME_HERO\"" "$TMP/ui.xml" &&
        ! has_reject_marker
}

# Waits until the UI tree proves Home is rendered. Fails hard (no capture) otherwise.
wait_for_home() {
    local lang="$1" tries=0
    expected_markers "$lang"
    while [ $tries -lt 45 ]; do
        if dump_ui; then
            if has_reject_marker; then
                log "  waiting: Splash/Onboarding marker present (frame rejected)"
            elif is_home_visible; then
                log "  home asserted: dock='$HOME_DOCK' hero='$HOME_HERO' (no Begin/شروع marker)"
                return 0
            else
                log "  waiting: Home markers not yet present"
            fi
        else
            log "  waiting: UI dump unavailable (Animating)"
        fi
        tries=$((tries + 1))
        sleep 1
    done
    log "FATAL: Home never asserted for lang=$lang; last visible texts:"
    grep -o 'text="[^"]*"' "$TMP/ui.xml" | sort -u >> "$RUNLOG" 2>/dev/null || true
    exit 1
}

write_png() {
    local name="$1" assertion="$2"
    "${ADB[@]}" exec-out screencap -p > "$OUT/$name.png"
    python3 - "$OUT/$name.png" <<'PY'
import sys
with open(sys.argv[1], "rb") as f:
    head = f.read(8)
if head != b"\x89PNG\r\n\x1a\n":
    sys.exit(f"not a PNG: {sys.argv[1]}")
PY
    cp "$TMP/ui.xml" "$LOGDIR/$name.uidump.xml"
    {
        echo "$name.png @ $(date '+%F %T')"
        echo "assert: $assertion"
        echo "sha256: $(shasum -a 256 "$OUT/$name.png" | awk '{print $1}')"
        state_dump
    } >> "$RUNLOG"
    log "captured: $OUT/$name.png"
}

# Top-of-Home capture: the full Home assertion (dock + hero label) must hold.
capture() {
    local name="$1" lang="$2"
    expected_markers "$lang"
    if ! dump_ui || ! is_home_visible; then
        log "FATAL: refusing to capture '$name' — Home assertion failed"
        exit 1
    fi
    write_png "$name" "top: dock='$HOME_DOCK' hero='$HOME_HERO' reject=False"
}

# Scrolled (continued) Home capture: the hero has scrolled off, so assert the
# persistent dock plus a Home section marker, and re-check the reject markers.
capture_scrolled() {
    local name="$1" lang="$2"
    expected_markers "$lang"
    if ! dump_ui || has_reject_marker ||
        ! grep -q "text=\"$HOME_DOCK\"" "$TMP/ui.xml" ||
        ! grep -qE "text=\"($HOME_SECTIONS)\"" "$TMP/ui.xml"; then
        log "FATAL: refusing to capture '$name' — continued-Home assertion failed"
        exit 1
    fi
    write_png "$name" "scrolled: dock='$HOME_DOCK' section=($HOME_SECTIONS) reject=False (Home asserted at top before scroll)"
}

scroll_down() {
    local n="${1:-1}" i=0
    while [ $i -lt "$n" ]; do
        "${ADB[@]}" shell input swipe "$SWIPE_X" "$SWIPE_TOP" "$SWIPE_X" "$SWIPE_BOTTOM" 300
        sleep 1
        i=$((i + 1))
    done
}

# ── Install the build under test ─────────────────────────────────────────────
if [ "${1:-}" != "--restore" ]; then
    log "APK under test: $APK"
    log "APK sha256: $(shasum -a 256 "$APK" | awk '{print $1}')"
    "${ADB[@]}" install -r "$APK" >/dev/null
    log "installed $APK"
fi

# ── Full WP-UIUX-02 evidence matrix ──────────────────────────────────────────
# NOTE: DataStore language is intentionally NOT written by this script. The
# emulator currently carries the reported desync state (DataStore language=fa
# while the UI locale is driven by axiom_lang); the EN captures below are
# therefore taken under exactly the desynchronized state that produced the
# Persian-advisory defect on the reviewed head.
if [ "${1:-}" != "--restore" ]; then
    log "=== Dark EN 100% (desync state: UI=en, datastore language=fa) ==="
    set_language en; set_font_scale 1.0; set_night yes
    launch_app; wait_for_home en
    capture 64_fixed_dark_en_100_top en
    scroll_down 1; sleep 1
    capture_scrolled 65_fixed_dark_en_100_lower en

    log "=== Dark FA RTL 100% ==="
    set_language fa; set_font_scale 1.0; set_night yes
    launch_app; wait_for_home fa
    capture 66_fixed_dark_fa_rtl_100_top fa
    scroll_down 1; sleep 1
    capture_scrolled 67_fixed_dark_fa_rtl_100_lower fa

    log "=== Dark EN 200% ==="
    set_language en; set_font_scale 2.0; set_night yes
    launch_app; wait_for_home en
    capture 68_fixed_dark_en_200_top en
    scroll_down 2; sleep 1
    capture_scrolled 69_fixed_dark_en_200_lower en
    scroll_down 2; sleep 1
    capture_scrolled 70_fixed_dark_en_200_deep en

    log "=== Light EN 100% ==="
    set_language en; set_font_scale 1.0; set_night no
    launch_app; wait_for_home en
    capture 71_fixed_light_en_100_top en
    scroll_down 1; sleep 1
    capture_scrolled 72_fixed_light_en_100_lower en
fi

# ── Restore emulator defaults (original pre-capture state) ───────────────────
log "=== restore emulator defaults (UI=en, font 1.0, night=yes) ==="
set_language en; set_font_scale 1.0; set_night yes
"${ADB[@]}" shell am force-stop "$PKG"

log "done. run log: $RUNLOG"
