package com.axiom.app.presentation.setup

import android.app.Application
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.components.AxiomPillButton
import com.axiom.app.ui.components.AxiomWordmarkHeader
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
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

    val layoutDir = if (selectedLang == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
        AwakenTheme(themeMode = selectedTheme) {
            val colors = LocalAxiomColors.current
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.voidBlack)
            ) {
                // Upper Atmospheric Light Arc from 02_language_selection.png
                Image(
                    painter = painterResource(R.drawable.bg_language_header),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Middle: Titles and Language Cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header text
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = localizedString(R.string.lang_choose_title, selectedLang),
                            fontFamily = Outfit,
                            fontWeight = FontWeight.Light,
                            fontSize = 30.sp,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = localizedString(R.string.lang_choose_subtitle, selectedLang),
                            fontFamily = Outfit,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // English Card (with Big Ben artwork)
                    LanguageCard(
                        title = "English",
                        artDrawableId = R.drawable.art_big_ben,
                        artDescription = "Big Ben",
                        isSelected = selectedLang == "en",
                        onClick = { selectedLang = "en" }
                    )

                    // Persian Card (with Azadi Tower artwork)
                    LanguageCard(
                        title = "فارسی",
                        artDrawableId = R.drawable.art_azadi_tower,
                        artDescription = "Azadi Tower",
                        isSelected = selectedLang == "fa",
                        onClick = { selectedLang = "fa" }
                    )
                }

                // Bottom: Continue CTA
                AxiomPillButton(
                    text = localizedString(R.string.btn_continue, selectedLang),
                    onClick = {
                        viewModel.completeSetup(
                            lang = selectedLang,
                            theme = selectedTheme,
                            onDone = onSetupComplete
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}
}

@Composable
private fun LanguageCard(
    title: String,
    artDrawableId: Int,
    artDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)
    val borderColor = if (isSelected) Color(0xFF2EE6A8) else Color(0x334E655C)
    val borderWidth = if (isSelected) 1.5.dp else 1.dp
    val bgBrush = if (isSelected) {
        androidx.compose.ui.graphics.Brush.verticalGradient(
            listOf(
                Color(0xEE092019),
                Color(0xFA04120E)
            )
        )
    } else {
        androidx.compose.ui.graphics.Brush.verticalGradient(
            listOf(
                Color(0xCC0A1411),
                Color(0xEE060C0A)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp)
            .clip(cardShape)
            .background(bgBrush)
            .border(BorderStroke(borderWidth, borderColor), cardShape)
            .clickable(
                role = androidx.compose.ui.semantics.Role.RadioButton,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Architectural artwork
            Image(
                painter = painterResource(artDrawableId),
                contentDescription = artDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(width = 72.dp, height = 68.dp)
            )

            // Center: Language Name
            Text(
                text = title,
                fontFamily = Outfit,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                color = if (isSelected) Color(0xFFF0FDF8) else Color(0xFFD1DDD7),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Right: Glowing Radio Indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .border(
                        width = if (isSelected) 2.dp else 1.5.dp,
                        color = if (isSelected) Color(0xFF2EE6A8) else Color(0x667A8C84),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0xFF2EE6A8))
                    )
                }
            }
        }
    }
}
