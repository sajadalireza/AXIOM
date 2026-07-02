package com.axiom.app.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import com.axiom.app.R
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AwakeningCompleteViewModel @Inject constructor(
    private val preferences: AxiomPreferences,
    private val hunterRepository: HunterRepository
) : ViewModel() {
    val hunterFlow = hunterRepository.getHunterProfile()
    suspend fun markShown() = preferences.setAwakeningShown()
}

private data class ConvergingParticle(
    val angle: Double,
    val initialDistance: Float, // distance from center (as a fraction of screen radius)
    val speed: Float,           // speed of converging
    val radiusDp: Float,
    val color: Color
)

@Composable
fun ConvergingParticleField(modifier: Modifier = Modifier) {
    val count = 100
    val particles = remember {
        val random = java.util.Random(42)
        List(count) {
            val angle = random.nextDouble() * 2 * Math.PI
            val distance = 0.5f + random.nextFloat() * 0.5f // Start from outer half
            val speed = 0.15f + random.nextFloat() * 0.2f
            val isGold = random.nextBoolean()
            ConvergingParticle(
                angle = angle,
                initialDistance = distance,
                speed = speed,
                radiusDp = 1.5f + random.nextFloat() * 3f,
                color = if (isGold) LegendaryGold else EpicPurple
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "converge_particles")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "converge_time"
    )

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = Math.sqrt((centerX * centerX + centerY * centerY).toDouble()).toFloat()

        particles.forEach { p ->
            // Converge to center: distance goes from initialDistance down to 0, then resets
            val currentDistanceFraction = (p.initialDistance - progress * p.speed) % 1.0f
            val normalizedDistance = if (currentDistanceFraction < 0f) currentDistanceFraction + 1.0f else currentDistanceFraction
            
            val r = normalizedDistance * maxRadius
            val x = centerX + (r * Math.cos(p.angle)).toFloat()
            val y = centerY + (r * Math.sin(p.angle)).toFloat()

            // Fade out as they get extremely close to center, and fade in as they spawn at the edge
            val alpha = if (normalizedDistance < 0.1f) {
                normalizedDistance / 0.1f
            } else if (normalizedDistance > 0.8f) {
                (1f - normalizedDistance) / 0.2f
            } else {
                1f
            }

            drawCircle(
                color = p.color.copy(alpha = alpha * 0.5f),
                radius = p.radiusDp.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun AwakeningCompleteScreen(
    onBegin: () -> Unit,
    viewModel: AwakeningCompleteViewModel = hiltViewModel()
) {
    val hunter by viewModel.hunterFlow.collectAsStateWithLifecycle(null)

    var nameProgress by remember { mutableStateOf(0) }
    var thesisAlpha by remember { mutableStateOf(0f) }
    var buttonAlpha by remember { mutableStateOf(0f) }

    val mainTitle = "THE AWAKENING IS COMPLETE"

    LaunchedEffect(hunter) {
        val name = hunter?.name ?: ""
        if (name.isNotEmpty()) {
            // Typewriter effect for name
            for (i in 1..name.length) {
                nameProgress = i
                delay(80)
            }
            delay(300)
            // Fade in the thesis statement
            animate(0f, 1f, animationSpec = tween(800)) { v, _ ->
                thesisAlpha = v
            }
            delay(400)
            // Fade in the action button
            animate(0f, 1f, animationSpec = tween(600)) { v, _ ->
                buttonAlpha = v
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(EpicPurple.copy(alpha = 0.45f), VoidBlack),
                    radius = 1400f
                )
            )
            .testTag("awakening_complete_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Epic particle burst field
        ConvergingParticleField(modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(28.dp)
                .fillMaxWidth()
        ) {
            // Display main header
            Text(
                text = mainTitle,
                style = DisplayXL,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("awakening_title")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // User name typewriter reveal in LegendaryGold
            val hunterName = hunter?.name ?: ""
            if (hunterName.isNotEmpty()) {
                Text(
                    text = hunterName.take(nameProgress).uppercase(),
                    fontFamily = Outfit,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LegendaryGold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    modifier = Modifier.testTag("awakening_hunter_name")
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Personal thesis statement in Fraunces Italic
            val personalThesis = hunter?.personalThesis ?: ""
            if (personalThesis.isNotEmpty() && thesisAlpha > 0f) {
                Text(
                    text = "\"$personalThesis\"",
                    fontFamily = Fraunces,
                    fontStyle = FontStyle.Italic,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(thesisAlpha)
                        .padding(horizontal = 16.dp)
                        .testTag("awakening_thesis")
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Enter the system CTA button
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "begin_scale"
            )
            val scope = rememberCoroutineScope()

            if (buttonAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .alpha(buttonAlpha)
                        .scale(scale)
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(LegendaryGold, LegendaryGold.copy(alpha = 0.85f))
                            )
                        )
                        .border(1.dp, LegendaryGold, RoundedCornerShape(8.dp))
                        .clickable(interactionSource, null) {
                            scope.launch {
                                try {
                                    viewModel.markShown()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                onBegin()
                            }
                        }
                        .testTag("enter_system_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ENTER THE SYSTEM",
                        fontFamily = JetBrainsMono,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VoidBlack,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
