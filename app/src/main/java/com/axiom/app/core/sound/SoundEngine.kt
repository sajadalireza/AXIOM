package com.axiom.app.core.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.axiom.app.R
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════
// SOUND CATALOGUE
// All 7 sounds sourced from freesound.org (CC0 license).
// Raw files live in res/raw/. See AXIOM design tokens for
// descriptions and suggested search terms.
// ═══════════════════════════════════════════════════════════════

enum class AwakenSound {
    /** Soft digital chime (~0.3s) — XP gained */
    XP_PING,

    /** Low resonant pulse (~0.6s) — mission completed */
    MISSION_COMPLETE,

    /** Ascending synth surge (~1.2s) — hunter level up */
    LEVEL_UP,

    /** Deep bass hit + shimmer (~1.8s) — rank tier change */
    RANK_UP,

    /** Dark chord materializing (~2.0s) — shadow acquired */
    SHADOW_MANIFEST,

    /** Impact + echo decay (~1.5s) — dungeon boss defeated */
    BOSS_DEFEATED,

    /** Short terminal beep (~0.2s) — system toast / alerts */
    SYSTEM_ALERT
}

// ═══════════════════════════════════════════════════════════════
// SOUND ENGINE
//
// Call SoundEngine.init(context) from AwakenApplication.onCreate().
// Call SoundEngine.play(AwakenSound.XP_PING) from anywhere.
// Every play() is wrapped in try-catch — audio is non-critical;
// failures are swallowed silently so the app never crashes on
// missing or corrupt audio files.
// ═══════════════════════════════════════════════════════════════

object SoundEngine {

    private const val MAX_STREAMS = 3
    private const val PRIORITY    = 1
    private const val LOOP_NONE   = 0
    private const val RATE_NORMAL = 1f
    private const val VOLUME_MAX  = 1f

    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<AwakenSound, Int>()
    private var initialized = false

    private lateinit var prefs: android.content.SharedPreferences
    var isMuted: Boolean = false
        private set
    var volume: Float = 1.0f
        private set

    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (::prefs.isInitialized) {
            prefs.edit().putBoolean("is_muted", muted).apply()
        }
    }

    fun setVolume(vol: Float) {
        volume = vol.coerceIn(0f, 1f)
        if (::prefs.isInitialized) {
            prefs.edit().putFloat("volume", volume).apply()
        }
    }

    // ─── Mapping: AwakenSound → res/raw/ resource ID ────────────

    private val rawResMap: Map<AwakenSound, Int> = mapOf(
        AwakenSound.XP_PING         to R.raw.xp_ping,
        AwakenSound.MISSION_COMPLETE to R.raw.mission_complete,
        AwakenSound.LEVEL_UP        to R.raw.level_up,
        AwakenSound.RANK_UP         to R.raw.rank_up,
        AwakenSound.SHADOW_MANIFEST to R.raw.shadow_manifest,
        AwakenSound.BOSS_DEFEATED   to R.raw.boss_defeated,
        AwakenSound.SYSTEM_ALERT    to R.raw.system_alert
    )

    // ─── Lifecycle ───────────────────────────────────────────────

    /**
     * Initializes the SoundPool and pre-loads all audio assets.
     * Must be called once from [com.axiom.app.AwakenApplication.onCreate].
     * Subsequent calls after initialization are no-ops.
     */
    fun init(context: Context) {
        if (initialized) return

        try {
            prefs = context.getSharedPreferences("axiom_sound", Context.MODE_PRIVATE)
            isMuted = prefs.getBoolean("is_muted", false)
            volume = prefs.getFloat("volume", 1.0f)

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val pool = SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(attributes)
                .build()

            rawResMap.forEach { (sound, resId) ->
                try {
                    soundIds[sound] = pool.load(context, resId, PRIORITY)
                } catch (e: Exception) {
                    // Individual sound load failure — continue loading others
                }
            }

            soundPool = pool
            initialized = true
        } catch (e: Exception) {
            // SoundPool creation failed (e.g. hardware limitation) — fail silently
        }
    }

    private val audioScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default)

    /**
     * Plays [sound] at full volume with no looping.
     * Fails silently if the engine is not initialized or the sound
     * asset failed to load — the app must never crash on audio.
     */
    fun play(sound: AwakenSound) {
        if (isMuted) return
        audioScope.launch {
            try {
                val pool    = soundPool ?: return@launch
                val soundId = soundIds[sound] ?: return@launch
                val currentVol = volume
                pool.play(soundId, currentVol, currentVol, PRIORITY, LOOP_NONE, RATE_NORMAL)
            } catch (e: Exception) {
                // Silent fail — audio is non-critical
            }
        }
    }

    /**
     * Overload to allow playing sound directly by its R.raw resource ID.
     */
    fun play(resId: Int) {
        val sound = rawResMap.entries.find { it.value == resId }?.key
        if (sound != null) {
            play(sound)
        }
    }

    /**
     * Releases all SoundPool resources.
     * Call from Application.onTerminate() or when audio is no longer needed.
     */
    fun release() {
        try {
            soundPool?.release()
        } catch (e: Exception) {
            // Silent fail
        } finally {
            soundPool = null
            soundIds.clear()
            initialized = false
        }
    }
}
