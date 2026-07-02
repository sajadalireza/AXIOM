package com.axiom.app.presentation.setup

import android.app.Application
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.R
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

// ──────────────────────────────────────────────
// ViewModel
// ──────────────────────────────────────────────
@HiltViewModel
class SetupViewModel @Inject constructor(
    app: Application,
    private val preferences: AxiomPreferences
) : AndroidViewModel(app) {

    /** Save language + theme + mark setup done, then notify caller to recreate. */
    fun completeSetup(
        lang: String,
        theme: ThemeMode,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            // 1. Persist to SharedPreferences so attachBaseContext picks it up immediately
            getApplication<Application>()
                .getSharedPreferences("axiom_lang", Context.MODE_PRIVATE)
                .edit()
                .putString("lang", lang)
                .apply()
            // 2. Persist to DataStore
            preferences.setLanguage(lang)
            preferences.setThemeMode(theme)
            preferences.setSetupComplete()
            onDone()
        }
    }
}

// ──────────────────────────────────────────────
// Screen
// ──────────────────────────────────────────────
@Composable
fun localizedString(id: Int, localeCode: String): String {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember(id, localeCode) {
        try {
            val locale = java.util.Locale(localeCode)
            val config = android.content.res.Configuration(context.resources.configuration)
            config.setLocale(locale)
            val localizedContext = context.createConfigurationContext(config)
            localizedContext.resources.getString(id)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                context.getString(id)
            } catch (ex: Exception) {
                ""
            }
        }
    }
}

@Composable
fun LanguageThemeSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    var selectedLang by remember { mutableStateOf("en") }
    var selectedTheme by remember { mutableStateOf(ThemeMode.DARK) }

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(120); visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500), label = "fade"
    )

    // Wrap the screen content inside an instant local AwakenTheme to preview the selected theme!
    AwakenTheme(themeMode = selectedTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalAxiomColors.current.voidBlack)
                .alpha(alpha),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {

                // ── Logo ──────────────────────────────
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AXIOM",
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp,
                        color = TextPrimary,
                        letterSpacing = 6.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = localizedString(R.string.setup_welcome, selectedLang),
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = SystemGreen,
                        letterSpacing = 3.sp
                    )
                }

                // ── Language ─────────────────────────
                SetupSection(label = localizedString(R.string.setup_language_label, selectedLang)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ChoiceChip(
                            text = "English",
                            selected = selectedLang == "en",
                            modifier = Modifier.weight(1f)
                        ) { selectedLang = "en" }
                        ChoiceChip(
                            text = "فارسی",
                            selected = selectedLang == "fa",
                            modifier = Modifier.weight(1f)
                        ) { selectedLang = "fa" }
                    }
                }

                // ── Theme ─────────────────────────────
                SetupSection(label = localizedString(R.string.setup_theme_label, selectedLang)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            ThemeMode.SYSTEM to localizedString(R.string.setup_theme_system, selectedLang),
                            ThemeMode.LIGHT  to localizedString(R.string.setup_theme_light, selectedLang),
                            ThemeMode.DARK   to localizedString(R.string.setup_theme_dark, selectedLang)
                        ).forEach { (mode, label) ->
                            ChoiceChip(
                                text = label,
                                selected = selectedTheme == mode,
                                modifier = Modifier.weight(1f)
                              ) { selectedTheme = mode }
                        }
                    }
                }

                // ── Begin button ──────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SystemGreen)
                        .clickable {
                            viewModel.completeSetup(
                                lang = selectedLang,
                                theme = selectedTheme,
                                onDone = onSetupComplete
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = localizedString(R.string.setup_continue, selectedLang),
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = VoidBlack,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

// ── Helpers ──────────────────────────────────

@Composable
private fun SetupSection(
    label: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            fontFamily = JetBrainsMono,
            fontSize = 10.sp,
            color = TextDim,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@Composable
private fun ChoiceChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (selected) SystemGreen.copy(alpha = 0.15f) else Color.Transparent
    val border = if (selected) SystemGreen else BorderFaint

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = JetBrainsMono,
            fontSize = 12.sp,
            color = if (selected) SystemGreen else TextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}
