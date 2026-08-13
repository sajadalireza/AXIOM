package com.axiom.app

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.axiom.app.data.SeedDataHelper
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.repository.SkillRepository
import com.axiom.app.domain.usecase.CheckStreakOnOpenUseCase
import com.axiom.app.domain.usecase.GrantDailyLoginBonusUseCase
import com.axiom.app.ui.AxiomViewModel
import com.axiom.app.ui.VitalsViewModel
import com.axiom.app.ui.MainScreen
import androidx.activity.viewModels
import com.axiom.app.ui.theme.AwakenTheme
import com.axiom.app.ui.theme.ThemeMode
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        fun saveLanguage(context: Context, lang: String) {
            context.getSharedPreferences("axiom_lang", Context.MODE_PRIVATE)
                .edit().putString("lang", lang).apply()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        try {
            val lang = try {
                newBase.getSharedPreferences("axiom_lang", Context.MODE_PRIVATE)
                    .getString("lang", "en") ?: "en"
            } catch (t: Throwable) {
                "en"
            }
            val locale = java.util.Locale(lang)
            java.util.Locale.setDefault(locale)
            val config = Configuration(newBase.resources.configuration)
            config.setLocale(locale)
            super.attachBaseContext(newBase.createConfigurationContext(config))
        } catch (e: Throwable) {
            e.printStackTrace()
            super.attachBaseContext(newBase)
        }
    }

    @Inject
    lateinit var preferences: com.axiom.app.data.local.AxiomPreferences

    @Inject
    lateinit var checkStreakOnOpenUseCase: CheckStreakOnOpenUseCase

    @Inject
    lateinit var grantDailyLoginBonusUseCase: GrantDailyLoginBonusUseCase

    private val axiomViewModel: AxiomViewModel by viewModels()
    private val vitalsViewModel: VitalsViewModel by viewModels()

    @Inject
    lateinit var seedDataHelper: SeedDataHelper

    @Inject
    lateinit var hunterRepository: HunterRepository

    @Inject
    lateinit var skillRepository: SkillRepository

    @Inject
    lateinit var startupReadiness: com.axiom.app.core.startup.StartupReadiness

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Run application initialization diagnostics check
        com.axiom.app.core.AppInitDiagnostics.runStartupCheckSequence(
            context = this,
            preferences = preferences,
            hunterRepository = hunterRepository,
            skillRepository = skillRepository
        )

        // Request notification permission if on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        lifecycleScope.launch {
            try {
                // Check streak on open
                checkStreakOnOpenUseCase()

                // Trigger Vitals App Open check (including Burnout and daily prompts)
                vitalsViewModel.checkAppOpenVitals()

                // Check and award daily login bonus
                val gotBonus = grantDailyLoginBonusUseCase()
                if (gotBonus) {
                    axiomViewModel.notifyDailyLoginBonus()
                }

                // WP-202: seed neutral reference catalogs on launch (no personal data,
                // no faked completion). Self-guards; safe on every launch.
                seedDataHelper.seedReferenceCatalogsIfNeeded()

                // Seed fallback if profile exists but no skills
                val profile = hunterRepository.getDirectHunterProfile()
                if (profile != null) {
                    val skills = skillRepository.getAllSkills().first()
                    if (skills.isEmpty()) {
                        seedDataHelper.seedSkillsIfNeeded()
                        seedDataHelper.seedMuscleGroupsIfNeeded()
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                // WP-201: signal authoritative startup readiness exactly once,
                // even on failure, so launch routing never blocks indefinitely
                // and never reads pre-bootstrap state.
                startupReadiness.markReady()
            }
        }

        setContent {
            val themeMode by preferences.themeModeFlow.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            AwakenTheme(themeMode = themeMode) {
                MainScreen()
            }
        }
    }
}
