package com.axiom.app.presentation.bodymap

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sqrt

@Composable
fun BodySilhouetteCanvas(
    muscles: List<MuscleGroup>,
    selectedMuscleId: String?,
    onSelectMuscle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isFrontView by remember { mutableStateOf(true) }

    // Screen-load fade/slide-in for colors
    var isLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isLoaded = true
    }

    val colorAlphaMultiplier by animateFloatAsState(
        targetValue = if (isLoaded) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "fill_fade_in"
    )

    // Flip animation (vertical swipe/pan triggers scale X transition from 1 -> 0 -> 1)
    val flipAngle by animateFloatAsState(
        targetValue = if (isFrontView) 0f else 180f,
        animationSpec = tween(400, easing = LinearOutSlowInEasing),
        label = "flip_animation"
    )
    val scaleX = cos(Math.toRadians(flipAngle.toDouble())).toFloat()

    // Bounce scale for tapped muscle
    val bounceScale = remember { Animatable(1f) }

    // Define center coordinates in 200x400 space for touch hotspots
    val touchPoints = remember(isFrontView) {
        if (isFrontView) {
            listOf(
                "chest" to Offset(100f, 90f),
                "shoulders" to Offset(60f, 85f),
                "shoulders" to Offset(140f, 85f),
                "biceps" to Offset(45f, 115f),
                "biceps" to Offset(155f, 115f),
                "forearms" to Offset(40f, 160f),
                "forearms" to Offset(160f, 160f),
                "core" to Offset(100f, 140f),
                "legs" to Offset(80f, 220f),
                "legs" to Offset(120f, 220f),
                "legs" to Offset(70f, 310f),
                "legs" to Offset(130f, 310f)
            )
        } else {
            listOf(
                "back" to Offset(100f, 100f),
                "triceps" to Offset(45f, 115f),
                "triceps" to Offset(155f, 115f),
                "legs" to Offset(80f, 220f), // glutes / hamstrings
                "legs" to Offset(120f, 220f),
                "legs" to Offset(70f, 310f), // back of calves
                "legs" to Offset(130f, 310f)
            )
        }
    }

    // Color coder helper
    fun getRecoveryColor(muscleId: String): Color {
        val muscle = muscles.find { it.id == muscleId }
        val lastTrained = muscle?.lastTrainedTimestamp
        if (lastTrained == null) return CommonGray.copy(alpha = 0.15f * colorAlphaMultiplier)
        val diffHours = (System.currentTimeMillis() - lastTrained) / (1000f * 60f * 60f)
        return when {
            diffHours > 48f -> SystemGreen.copy(alpha = 0.30f * colorAlphaMultiplier)
            diffHours >= 24f -> LegendaryGold.copy(alpha = 0.30f * colorAlphaMultiplier)
            else -> PenaltyRed.copy(alpha = 0.30f * colorAlphaMultiplier)
        }
    }

    // Drag tracking for vertical swipe
    var totalDragY by remember { mutableStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isFrontView, touchPoints) {
                    detectTapGestures { tapLoc ->
                        // Determine scale and offset to map tap coordinates back to 200x400 space
                        val scale = min(size.width / 200f, size.height / 400f)
                        val offsetX = (size.width - 200f * scale) / 2f
                        val offsetY = (size.height - 400f * scale) / 2f

                        val localX = (tapLoc.x - offsetX) / scale
                        val localY = (tapLoc.y - offsetY) / scale
                        val localTap = Offset(localX, localY)

                        var minDistance = Float.MAX_VALUE
                        var closestMuscleId: String? = null

                        touchPoints.forEach { (muscleId, hotspot) ->
                            val dx = localTap.x - hotspot.x
                            val dy = localTap.y - hotspot.y
                            val dist = sqrt(dx * dx + dy * dy)
                            if (dist < minDistance && dist < 35f) { // 35 units radius in 200x400 space
                                minDistance = dist
                                closestMuscleId = muscleId
                            }
                        }

                        closestMuscleId?.let { id ->
                            onSelectMuscle(id)
                            coroutineScope.launch {
                                bounceScale.snapTo(1f)
                                bounceScale.animateTo(1.15f, animationSpec = tween(100, easing = FastOutSlowInEasing))
                                bounceScale.animateTo(1f, animationSpec = tween(100, easing = FastOutSlowInEasing))
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { totalDragY = 0f },
                        onDrag = { _, dragAmount ->
                            totalDragY += dragAmount.y
                        },
                        onDragEnd = {
                            if (totalDragY > 150f) {
                                // Swipe Down -> Toggle Front/Back
                                isFrontView = !isFrontView
                            } else if (totalDragY < -150f) {
                                // Swipe Up -> Toggle Front/Back
                                isFrontView = !isFrontView
                            }
                        }
                    )
                }
        ) {
            // Background Grid Lines
            drawRect(
                color = BorderFaint.copy(alpha = 0.15f),
                size = Size(size.width, size.height),
                style = Stroke(width = 1f)
            )
            for (i in 0..6) {
                val offset = size.width * (i.toFloat() / 6f)
                drawLine(
                    color = BorderFaint.copy(alpha = 0.12f),
                    start = Offset(offset, 0f),
                    end = Offset(offset, size.height)
                )
                val hOffset = size.height * (i.toFloat() / 6f)
                drawLine(
                    color = BorderFaint.copy(alpha = 0.12f),
                    start = Offset(0f, hOffset),
                    end = Offset(size.width, hOffset)
                )
            }

            // Calculate bounding scaling factor to draw perfectly centered in 200x400
            val scale = min(size.width / 200f, size.height / 400f)
            val offsetX = (size.width - 200f * scale) / 2f
            val offsetY = (size.height - 400f * scale) / 2f

            withTransform({
                translate(left = offsetX, top = offsetY)
                scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
                scale(scaleX = scaleX, scaleY = 1f, pivot = Offset(100f, 200f))
            }) {
                // Draw Body Silhouette outlines & filled muscle groups
                val activeMuscles = if (isFrontView) {
                    listOf("chest", "shoulders", "biceps", "forearms", "core", "legs")
                } else {
                    listOf("back", "triceps", "legs")
                }

                activeMuscles.forEach { muscleId ->
                    val path = when (muscleId) {
                        "chest" -> BodyMapPaths.chest
                        "shoulders" -> BodyMapPaths.shoulders
                        "biceps" -> BodyMapPaths.biceps
                        "triceps" -> BodyMapPaths.triceps
                        "forearms" -> BodyMapPaths.forearms
                        "back" -> BodyMapPaths.back
                        "core" -> BodyMapPaths.core
                        "legs" -> BodyMapPaths.legs
                        else -> null
                    }

                    if (path != null) {
                        val color = getRecoveryColor(muscleId)
                        val isSelected = selectedMuscleId == muscleId
                        val muscleCenter = when (muscleId) {
                            "chest" -> Offset(100f, 90f)
                            "shoulders" -> Offset(100f, 82f)
                            "biceps" -> Offset(100f, 112f)
                            "triceps" -> Offset(100f, 112f)
                            "forearms" -> Offset(100f, 155f)
                            "back" -> Offset(100f, 95f)
                            "core" -> Offset(100f, 140f)
                            "legs" -> Offset(100f, 260f)
                            else -> Offset(100f, 200f)
                        }

                        // Apply individual bounce animation if selected
                        withTransform({
                            if (isSelected) {
                                scale(bounceScale.value, bounceScale.value, pivot = muscleCenter)
                            }
                        }) {
                            // Fill muscle group area
                            drawPath(path = path, color = color)
                            // Stroke outline
                            drawPath(
                                path = path,
                                color = if (isSelected) LegendaryGold else BorderFaint.copy(alpha = 0.5f),
                                style = Stroke(width = if (isSelected) 2f else 1f)
                            )
                        }
                    }
                }

                // Draw Head/Neck for completion (unclickable but stylized)
                val headPath = androidx.compose.ui.graphics.Path().apply {
                    addOval(androidx.compose.ui.geometry.Rect(88f, 20f, 112f, 50f))
                }
                drawPath(path = headPath, color = CommonGray.copy(alpha = 0.15f))
                drawPath(path = headPath, color = BorderFaint.copy(alpha = 0.4f), style = Stroke(width = 1f))
            }
        }
    }
}
