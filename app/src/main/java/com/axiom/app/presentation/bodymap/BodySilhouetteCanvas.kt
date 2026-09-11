package com.axiom.app.presentation.bodymap

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.ui.theme.AxiomBorder
import com.axiom.app.ui.theme.AxiomColorScheme
import com.axiom.app.ui.theme.AxiomMotion
import com.axiom.app.ui.theme.AxiomRadius
import com.axiom.app.ui.theme.AxiomSpacing
import com.axiom.app.ui.theme.FiraCode
import com.axiom.app.ui.theme.Inter
import com.axiom.app.ui.theme.LocalAxiomColors
import com.axiom.app.ui.theme.LocalAxiomReducedMotion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayDeque
import kotlin.math.abs
import kotlin.math.min

private const val CW = BodyMapPaths.CANVAS_WIDTH

private enum class AtlasRegionTone { Standard, CoreRib, CoreSide, LegPrimary, LegSecondary, LegShin }
private enum class AtlasRegionPart { Muscle, Thigh, Knee, Calf }

private data class AtlasRegion(
    val id: String,
    val center: Offset,
    val path: Path,
    val tone: AtlasRegionTone = AtlasRegionTone.Standard,
    val part: AtlasRegionPart = AtlasRegionPart.Muscle
)

// ─────────────────────────────────────────────────────────────────────────────
// PRE-ALLOCATED STATIC ANATOMICAL PATHS (Zero Draw-Time Allocations)
// ─────────────────────────────────────────────────────────────────────────────

private val StaticFacePath: Path = Path().apply {
    moveTo(107f, 19f)
    lineTo(120f, 13f)
    lineTo(133f, 19f)
    lineTo(134f, 35f)
    cubicTo(134f, 42f, 130f, 48f, 125f, 51f)
    cubicTo(122f, 53f, 118f, 53f, 115f, 51f)
    cubicTo(110f, 48f, 106f, 42f, 106f, 35f)
    close()
}

private val StaticLeftHandPath: Path = Path().apply {
    moveTo(12f, 301f)
    cubicTo(7f, 306f, 4f, 313f, 3f, 320f)
    lineTo(0f, 326f)
    lineTo(8f, 322f)
    lineTo(6f, 339f)
    cubicTo(6f, 341.5f, 6.5f, 342.5f, 7.5f, 342.5f)
    cubicTo(8.5f, 342.5f, 9f, 341.5f, 9f, 339f)
    lineTo(10.5f, 321f)
    lineTo(12.5f, 321f)
    lineTo(13f, 341f)
    cubicTo(13f, 343f, 13.5f, 344f, 14.5f, 344f)
    cubicTo(15.5f, 344f, 16f, 343f, 16f, 341f)
    lineTo(17f, 320f)
    lineTo(19f, 320f)
    lineTo(19.5f, 341f)
    cubicTo(19.5f, 343f, 20f, 344f, 21f, 344f)
    cubicTo(22f, 344f, 22.5f, 343f, 22.5f, 341f)
    lineTo(23f, 321f)
    lineTo(25f, 321f)
    lineTo(25.5f, 338f)
    cubicTo(25.5f, 340f, 26f, 341f, 27f, 341f)
    cubicTo(28f, 341f, 28.5f, 340f, 28.5f, 338f)
    lineTo(28f, 313f)
    cubicTo(24f, 305f, 18f, 301f, 12f, 301f)
    close()
}

private val StaticRightHandPath: Path = Path().apply {
    moveTo(228f, 301f)
    cubicTo(233f, 306f, 236f, 313f, 237f, 320f)
    lineTo(240f, 326f)
    lineTo(232f, 322f)
    lineTo(234f, 339f)
    cubicTo(234f, 341.5f, 233.5f, 342.5f, 232.5f, 342.5f)
    cubicTo(231.5f, 342.5f, 231f, 341.5f, 231f, 339f)
    lineTo(229.5f, 321f)
    lineTo(227.5f, 321f)
    lineTo(227f, 341f)
    cubicTo(227f, 343f, 226.5f, 344f, 225.5f, 344f)
    cubicTo(224.5f, 344f, 224f, 343f, 224f, 341f)
    lineTo(223f, 320f)
    lineTo(221f, 320f)
    lineTo(220.5f, 341f)
    cubicTo(220.5f, 343f, 220f, 344f, 219f, 344f)
    cubicTo(218f, 344f, 217.5f, 343f, 217.5f, 341f)
    lineTo(217f, 321f)
    lineTo(215f, 321f)
    lineTo(214.5f, 338f)
    cubicTo(214.5f, 340f, 214f, 341f, 213f, 341f)
    cubicTo(212f, 341f, 211.5f, 340f, 211.5f, 338f)
    lineTo(212f, 313f)
    cubicTo(216f, 305f, 222f, 301f, 228f, 301f)
    close()
}

private val StaticFrontFeetMalePath: Path = Path().apply {
    val top = 519f
    val sole = 553f
    val specs = listOf(floatArrayOf(73f, 84f, 68f, 90f), floatArrayOf(132f, 143f, 127f, 149f))
    specs.forEachIndexed { index, (ankleLeft, ankleRight, soleLeft, soleRight) ->
        if (index == 0) {
            moveTo(ankleLeft, top)
            cubicTo(ankleLeft - 0.5f, top + 10f, soleLeft + 1f, sole - 10f, soleLeft, sole - 5f)
            cubicTo(soleLeft - 0.5f, sole - 1f, soleLeft + 2f, sole + 1f, soleLeft + 5f, sole + 1f)
            cubicTo(soleRight - 4f, sole + 1f, soleRight - 1f, sole, soleRight, sole - 2f)
            cubicTo(soleRight + 1f, sole - 6f, ankleRight + 1f, top + 10f, ankleRight, top)
            close()
        } else {
            moveTo(ankleLeft, top)
            cubicTo(ankleLeft - 1f, top + 10f, soleLeft - 1f, sole - 6f, soleLeft, sole - 2f)
            cubicTo(soleLeft + 1f, sole, soleRight - 5f, sole + 1f, soleRight - 4f, sole + 1f)
            cubicTo(soleRight - 1f, sole + 1f, soleRight + 0.5f, sole - 1f, soleRight, sole - 5f)
            cubicTo(soleRight - 1f, sole - 10f, ankleRight + 0.5f, top + 10f, ankleRight, top)
            close()
        }
    }
}

private val StaticFrontFeetFemalePath: Path = Path().apply {
    val top = 519f
    val sole = 553f
    val specs = listOf(floatArrayOf(105f, 112f, 100f, 116f), floatArrayOf(128f, 135f, 124f, 140f))
    specs.forEachIndexed { index, (ankleLeft, ankleRight, soleLeft, soleRight) ->
        if (index == 0) {
            moveTo(ankleLeft, top)
            cubicTo(ankleLeft - 0.5f, top + 10f, soleLeft + 1f, sole - 10f, soleLeft, sole - 5f)
            cubicTo(soleLeft - 0.5f, sole - 1f, soleLeft + 2f, sole + 1f, soleLeft + 5f, sole + 1f)
            cubicTo(soleRight - 4f, sole + 1f, soleRight - 1f, sole, soleRight, sole - 2f)
            cubicTo(soleRight + 1f, sole - 6f, ankleRight + 1f, top + 10f, ankleRight, top)
            close()
        } else {
            moveTo(ankleLeft, top)
            cubicTo(ankleLeft - 1f, top + 10f, soleLeft - 1f, sole - 6f, soleLeft, sole - 2f)
            cubicTo(soleLeft + 1f, sole, soleRight - 5f, sole + 1f, soleRight - 4f, sole + 1f)
            cubicTo(soleRight - 1f, sole + 1f, soleRight + 0.5f, sole - 1f, soleRight, sole - 5f)
            cubicTo(soleRight - 1f, sole - 10f, ankleRight + 0.5f, top + 10f, ankleRight, top)
            close()
        }
    }
}

private val FrontAtlasRegions: List<AtlasRegion> = listOf(
    AtlasRegion("shoulders", Offset(120f, 112f), BodyMapPaths.shoulders),
    AtlasRegion("chest", Offset(120f, 142f), BodyMapPaths.chest),
    AtlasRegion("biceps", Offset(120f, 178f), BodyMapPaths.biceps),
    AtlasRegion("forearms", Offset(120f, 278f), BodyMapPaths.forearms),
    AtlasRegion("legs", Offset(120f, 346f), BodyMapPaths.thighPrimary, tone = AtlasRegionTone.LegPrimary, part = AtlasRegionPart.Thigh),
    AtlasRegion("legs", Offset(120f, 356f), BodyMapPaths.thighSecondary, tone = AtlasRegionTone.LegSecondary, part = AtlasRegionPart.Thigh),
    AtlasRegion("legs", Offset(120f, 470f), BodyMapPaths.calfLateral, tone = AtlasRegionTone.LegPrimary, part = AtlasRegionPart.Calf),
    AtlasRegion("legs", Offset(120f, 470f), BodyMapPaths.calfMedial, tone = AtlasRegionTone.LegPrimary, part = AtlasRegionPart.Calf),
    AtlasRegion("legs", Offset(120f, 470f), BodyMapPaths.calfShin, tone = AtlasRegionTone.LegShin, part = AtlasRegionPart.Calf),
    AtlasRegion("legs", Offset(120f, 470f), BodyMapPaths.knees, part = AtlasRegionPart.Knee),
    AtlasRegion("core", Offset(120f, 254f), BodyMapPaths.coreObliques, AtlasRegionTone.CoreSide),
    AtlasRegion("core", Offset(120f, 202f), BodyMapPaths.coreRibs, AtlasRegionTone.CoreRib),
    AtlasRegion("core", Offset(120f, 222f), BodyMapPaths.coreAbs)
)

private val BackAtlasRegions: List<AtlasRegion> = listOf(
    AtlasRegion("shoulders", Offset(120f, 104f), BodyMapPaths.backShoulders),
    AtlasRegion("back", Offset(120f, 176f), BodyMapPaths.back),
    AtlasRegion("triceps", Offset(120f, 220f), BodyMapPaths.triceps),
    AtlasRegion("core", Offset(120f, 252f), BodyMapPaths.lowerBack),
    AtlasRegion("legs", Offset(120f, 398f), BodyMapPaths.backLegs, part = AtlasRegionPart.Thigh)
)

private fun atlasRegions(view: BodyMapView): List<AtlasRegion> =
    if (view == BodyMapView.Front) FrontAtlasRegions else BackAtlasRegions

/**
 * Resolves localized muscle group display name.
 */
fun getLocalizedMuscleName(muscleId: String, fallbackDisplayName: String, context: android.content.Context): String {
    val resId = context.resources.getIdentifier("muscle_$muscleId", "string", context.packageName)
    return if (resId != 0) context.getString(resId) else fallbackDisplayName
}

@Composable
fun BodySilhouetteCanvas(
    muscles: List<MuscleGroup>,
    selectedMuscleId: String?,
    onSelectMuscle: (String) -> Unit,
    modifier: Modifier = Modifier,
    displayMode: BodyMapDisplayMode = BodyMapDisplayMode.RecoveryReadiness,
    sex: BodyMapSex = BodyMapSex.Male,
    atlasView: BodyMapView = BodyMapView.Front,
    onToggleFrontBack: (() -> Unit)? = null
) {
    val colors = LocalAxiomColors.current
    val context = LocalContext.current
    val reducedMotion = LocalAxiomReducedMotion.current
    val isDark = colors.voidBlack.luminance() < 0.5f
    val canvasPalette = remember(colors, isDark) { AtlasCanvasPalette.from(colors, isDark) }
    val coroutineScope = rememberCoroutineScope()
    val currentOnSelectMuscle by rememberUpdatedState(onSelectMuscle)
    val profile = remember(sex) { BodyMapAtlasModel.profileFor(sex) }

    val activeMuscleId = selectedMuscleId ?: BodyMapAtlasModel.defaultSelectedFor(muscles, displayMode)
    val selectedGroup = remember(activeMuscleId) {
        activeMuscleId?.let { BodyMapAtlasModel.groupLabelFor(it) }
    }

    val involvementRoles = remember(displayMode, activeMuscleId) {
        if (displayMode == BodyMapDisplayMode.MusclesInvolved && activeMuscleId != null) {
            BodyMapAtlasModel.involvementRoles(
                primaryMuscleId = activeMuscleId,
                secondaryMuscleIds = BodyMapAtlasModel.secondaryForPrimary(activeMuscleId)
            )
        } else {
            emptyMap()
        }
    }

    var isLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isLoaded = true }

    val fadeDuration = AxiomMotion.effectiveDuration(520, reducedMotion)
    val fillAlpha by animateFloatAsState(
        targetValue = if (isLoaded) 1f else 0f,
        animationSpec = tween(fadeDuration, easing = FastOutSlowInEasing),
        label = "bodymap_atlas_fade"
    )
    val bounceScale = remember { Animatable(1f) }

    // Front artwork is used for Front view in Strength Balance mode
    val usesFrontArtwork = atlasView == BodyMapView.Front && displayMode == BodyMapDisplayMode.StrengthBalance
    val frontArtworkRes = if (sex == BodyMapSex.Male) R.drawable.body_male_light else R.drawable.body_female_light
    val frontArtworkBitmap = ImageBitmap.imageResource(frontArtworkRes)
    val artworkHorizontalScale = if (sex == BodyMapSex.Male) 0.950f else 0.982f
    val artworkTopTrim = 0

    // ─────────────────────────────────────────────────────────────────────────
    // ASYNC PREPARATION (Remediating Risk 1 & Risk 2)
    // ─────────────────────────────────────────────────────────────────────────
    var preparedFrontArtworkBitmap by remember(sex) { mutableStateOf<ImageBitmap?>(null) }
    var frontArtworkOutlinePainter by remember(sex, isDark) { mutableStateOf<BitmapPainter?>(null) }
    var renderedArtworkBitmap by remember(sex, muscles) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(sex, frontArtworkBitmap) {
        withContext(Dispatchers.Default) {
            val prepared = prepareFrontArtwork(frontArtworkBitmap, sex)
            val outline = outlineFrontArtwork(prepared)
            preparedFrontArtworkBitmap = prepared
            frontArtworkOutlinePainter = BitmapPainter(
                image = outline,
                srcOffset = IntOffset(0, artworkTopTrim),
                srcSize = IntSize(outline.width, outline.height - artworkTopTrim),
                filterQuality = FilterQuality.High
            )
        }
    }

    LaunchedEffect(preparedFrontArtworkBitmap, muscles, sex, usesFrontArtwork) {
        val prep = preparedFrontArtworkBitmap ?: return@LaunchedEffect
        if (!usesFrontArtwork || muscles.isEmpty()) {
            renderedArtworkBitmap = prep
            return@LaunchedEffect
        }
        withContext(Dispatchers.Default) {
            // Keyed ONLY on (muscles, sex), NOT on selectedGroup!
            // Decouples selection highlight from bitmap retinting loop.
            val tinted = tintStrengthArtwork(
                source = prep,
                muscles = muscles,
                sex = sex,
                palette = canvasPalette
            )
            renderedArtworkBitmap = tinted
        }
    }

    val frontArtworkPainter = remember(renderedArtworkBitmap, artworkTopTrim) {
        val bitmap = renderedArtworkBitmap
        if (bitmap != null) {
            BitmapPainter(
                image = bitmap,
                srcOffset = IntOffset(0, artworkTopTrim),
                srcSize = IntSize(bitmap.width, bitmap.height - artworkTopTrim),
                filterQuality = FilterQuality.High
            )
        } else null
    }

    val touchPoints = remember(atlasView, sex, usesFrontArtwork) {
        if (usesFrontArtwork) {
            BodyMapGeometry.artworkHotspotsFor(atlasView, sex)
        } else {
            BodyMapAtlasModel.hotspotsFor(atlasView).map { BodyMapGeometry.projectHotspot(it, sex) }
        }
    }

    val accessibleMuscles = remember(muscles, atlasView) {
        val visibleIds = BodyMapAtlasModel.hotspotsFor(atlasView).map { it.muscleId }.distinct()
        visibleIds.mapNotNull { id -> muscles.firstOrNull { it.id == id } }
    }

    var totalDragY by remember { mutableStateOf(0f) }

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {
                    contentDescription = "${sex.name} ${atlasView.name.lowercase()} body map"
                    stateDescription = selectedGroup?.let { "$it selected" } ?: "No muscle selected"
                    customActions = accessibleMuscles.map { muscle ->
                        CustomAccessibilityAction(label = "Select ${muscle.displayName}") {
                            currentOnSelectMuscle(muscle.id)
                            true
                        }
                    }
                }
        ) {
            // If Front Artwork is ready, render outline & tinted artwork
            if (usesFrontArtwork && frontArtworkPainter != null && frontArtworkOutlinePainter != null) {
                Image(
                    painter = frontArtworkOutlinePainter!!,
                    contentDescription = null,
                    alignment = Alignment.TopCenter,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(canvasPalette.shellStroke, BlendMode.SrcIn),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = fillAlpha * if (isDark) 0.36f else 0.62f
                            scaleX = bounceScale.value * artworkHorizontalScale
                            scaleY = bounceScale.value
                        }
                )
                Image(
                    painter = frontArtworkPainter,
                    contentDescription = null,
                    alignment = Alignment.TopCenter,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = fillAlpha
                            scaleX = bounceScale.value * artworkHorizontalScale
                            scaleY = bounceScale.value
                        }
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("body_silhouette_canvas")
                    .pointerInput(
                        atlasView,
                        touchPoints,
                        usesFrontArtwork,
                        frontArtworkBitmap.width,
                        frontArtworkBitmap.height,
                        artworkTopTrim
                    ) {
                        detectTapGestures { tapLoc ->
                            val localTap = if (usesFrontArtwork) {
                                val transform = BodyMapGeometry.artworkTransformFor(
                                    viewportWidth = size.width.toFloat(),
                                    viewportHeight = size.height.toFloat(),
                                    sourceWidth = frontArtworkBitmap.width.toFloat(),
                                    sourceHeight = frontArtworkBitmap.height.toFloat(),
                                    topTrim = artworkTopTrim.toFloat(),
                                    horizontalScale = artworkHorizontalScale
                                )
                                BodyMapGeometry.artworkViewportToAtlas(tapLoc, transform)
                            } else {
                                val transform = BodyMapGeometry.transformFor(
                                    size.width.toFloat(),
                                    size.height.toFloat()
                                )
                                BodyMapGeometry.viewportToAtlas(tapLoc, transform)
                            }

                            BodyMapGeometry.resolveMuscleAt(localTap, touchPoints)?.let { id ->
                                currentOnSelectMuscle(id)
                                if (!reducedMotion) {
                                    coroutineScope.launch {
                                        bounceScale.snapTo(1f)
                                        bounceScale.animateTo(1.08f, animationSpec = tween(90, easing = FastOutSlowInEasing))
                                        bounceScale.animateTo(1f, animationSpec = tween(130, easing = FastOutSlowInEasing))
                                    }
                                }
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { totalDragY = 0f },
                            onDrag = { _, dragAmount -> totalDragY += dragAmount.y },
                            onDragEnd = {
                                if (abs(totalDragY) > 150f && onToggleFrontBack != null) {
                                    onToggleFrontBack()
                                }
                            }
                        )
                    }
                    .graphicsLayer {
                        if (usesFrontArtwork) {
                            alpha = fillAlpha
                            scaleX = bounceScale.value
                            scaleY = bounceScale.value
                        }
                    }
            ) {
                if (usesFrontArtwork && frontArtworkPainter != null) {
                    val transform = BodyMapGeometry.artworkTransformFor(
                        viewportWidth = size.width,
                        viewportHeight = size.height,
                        sourceWidth = frontArtworkBitmap.width.toFloat(),
                        sourceHeight = frontArtworkBitmap.height.toFloat(),
                        topTrim = artworkTopTrim.toFloat(),
                        horizontalScale = artworkHorizontalScale
                    )
                    drawFrontExtremityRepair(sex = sex, transform = transform, palette = canvasPalette)

                    // Draw-time dynamic selection highlight (0 bitmap retinting)
                    if (activeMuscleId != null) {
                        val activeHotspots = touchPoints.filter { it.muscleId == activeMuscleId }
                        activeHotspots.forEach { hotspot ->
                            val viewportHotspot = BodyMapGeometry.atlasToArtworkViewport(
                                Offset(hotspot.x, hotspot.y),
                                transform
                            )
                            drawCircle(
                                color = colors.legendaryGold.copy(alpha = 0.85f),
                                radius = 18.dp.toPx() * bounceScale.value,
                                center = viewportHotspot,
                                style = Stroke(width = 2.5.dp.toPx())
                            )
                            drawCircle(
                                color = colors.legendaryGold.copy(alpha = 0.22f),
                                radius = 24.dp.toPx() * bounceScale.value,
                                center = viewportHotspot
                            )
                        }
                    }
                    return@Canvas
                }

                // ─────────────────────────────────────────────────────────────
                // HIGH-FI NATIVE VECTOR ATLAS ENGINE (Front & Back Views)
                // ─────────────────────────────────────────────────────────────
                val transform = BodyMapGeometry.transformFor(size.width, size.height)

                withTransform({
                    translate(left = transform.offsetX, top = transform.offsetY)
                    scale(scaleX = transform.scale, scaleY = transform.scale * transform.bodyYScale, pivot = Offset.Zero)
                }) {
                    withTransform({
                        scale(
                            scaleX = 1f,
                            scaleY = profile.heightSpan,
                            pivot = Offset(120f, BodyMapGeometry.SOLE_Y)
                        )
                    }) {
                        if (sex == BodyMapSex.Female) {
                            drawPath(
                                path = BodyMapPaths.femaleHair,
                                color = canvasPalette.hair.copy(alpha = 0.88f * fillAlpha)
                            )
                            drawPath(
                                path = BodyMapPaths.femaleHair,
                                color = canvasPalette.shellStroke.copy(alpha = 0.70f),
                                style = Stroke(width = 2.2f)
                            )
                        }

                        val shellFill = canvasPalette.shellFill.copy(alpha = fillAlpha)
                        val neckPath = BodyMapPaths.neckFor(sex)
                        val headPath = BodyMapPaths.headFor(sex)
                        drawPath(path = neckPath, color = shellFill)
                        drawPath(path = neckPath, color = canvasPalette.shellStroke, style = Stroke(width = 2.0f))
                        drawPath(path = neckPath, color = canvasPalette.muscleRim.copy(alpha = 0.88f), style = Stroke(width = 1.05f))
                        drawPath(path = headPath, color = shellFill)
                        drawPath(path = headPath, color = canvasPalette.shellStroke, style = Stroke(width = 1.35f))

                        val upperBodyScale = (profile.shoulderSpan + profile.chestSpan) / 2f
                        if (atlasView == BodyMapView.Front) {
                            withTransform({ scale(scaleX = upperBodyScale, scaleY = 1f, pivot = Offset(120f, 102f)) }) {
                                drawPath(path = BodyMapPaths.upperTorsoShell, color = shellFill)
                                drawPath(path = BodyMapPaths.upperTorsoShell, color = canvasPalette.shellStroke, style = Stroke(width = 2.0f))
                                drawPath(path = BodyMapPaths.upperTorsoShell, color = canvasPalette.muscleRim.copy(alpha = 0.88f), style = Stroke(width = 1.05f))
                            }
                        }

                        // Pre-allocated static regions (Zero Allocations in draw)
                        val regions = atlasRegions(atlasView)
                        for (i in regions.indices) {
                            val region = regions[i]
                            val isSelected = activeMuscleId == region.id ||
                                (selectedGroup != null && BodyMapAtlasModel.groupLabelFor(region.id) == selectedGroup)
                            val regionScaleX = horizontalScaleFor(region, profile)
                            val regionScaleY = verticalScaleFor(region.id, profile)
                            val baseFillColor = colorForRegion(
                                muscleId = region.id,
                                selectedGroup = selectedGroup,
                                muscles = muscles,
                                displayMode = displayMode,
                                role = involvementRoles[region.id] ?: BodyMapRegionRole.Neutral,
                                colors = colors,
                                palette = canvasPalette,
                                alpha = fillAlpha
                            )
                            val fillColor = when {
                                displayMode != BodyMapDisplayMode.StrengthBalance || isSelected -> baseFillColor
                                region.tone == AtlasRegionTone.CoreRib -> canvasPalette.coreRib.copy(alpha = 0.94f * fillAlpha)
                                region.tone == AtlasRegionTone.CoreSide -> canvasPalette.coreSide.copy(alpha = 0.88f * fillAlpha)
                                region.tone == AtlasRegionTone.LegPrimary -> canvasPalette.legPrimary.copy(alpha = 0.88f * fillAlpha)
                                region.tone == AtlasRegionTone.LegSecondary -> canvasPalette.legSecondary.copy(alpha = fillAlpha)
                                region.tone == AtlasRegionTone.LegShin -> canvasPalette.leg.copy(alpha = fillAlpha)
                                else -> baseFillColor
                            }

                            withTransform({
                                scale(scaleX = regionScaleX, scaleY = regionScaleY, pivot = region.center)
                                if (isSelected) {
                                    scale(scaleX = bounceScale.value, scaleY = bounceScale.value, pivot = region.center)
                                }
                            }) {
                                drawPath(path = region.path, color = fillColor)
                                drawPath(
                                    path = region.path,
                                    color = if (isSelected) colors.legendaryGold else canvasPalette.muscleRim,
                                    style = Stroke(width = if (isSelected) 2.6f else 2.15f)
                                )
                                drawPath(
                                    path = region.path,
                                    color = canvasPalette.innerShadow.copy(alpha = if (isSelected) 0.16f else 0.09f),
                                    style = Stroke(width = 0.8f)
                                )
                            }
                        }

                        if (atlasView == BodyMapView.Front) {
                            withTransform({ scale(scaleX = profile.pelvisSpan, scaleY = 1f, pivot = Offset(120f, 294f)) }) {
                                drawPath(path = BodyMapPaths.pelvis, color = canvasPalette.shellFill.copy(alpha = 0.92f * fillAlpha))
                                drawPath(path = BodyMapPaths.pelvis, color = canvasPalette.muscleRim, style = Stroke(width = 2.15f))
                                drawPath(path = BodyMapPaths.pelvis, color = canvasPalette.shellStroke.copy(alpha = 0.28f), style = Stroke(width = 0.8f))
                            }
                        }

                        drawAnatomyLines(atlasView, sex, profile, canvasPalette)

                        withTransform({ scale(scaleX = profile.handSpan, scaleY = 1f, pivot = Offset(120f, 305f)) }) {
                            withTransform({ translate(top = -14f) }) {
                                drawPath(path = StaticLeftHandPath, color = shellFill)
                                drawPath(path = StaticLeftHandPath, color = canvasPalette.shellStroke, style = Stroke(width = 2.05f))
                                drawPath(path = StaticRightHandPath, color = shellFill)
                                drawPath(path = StaticRightHandPath, color = canvasPalette.shellStroke, style = Stroke(width = 2.05f))
                            }
                        }

                        withTransform({ scale(scaleX = profile.footSpan, scaleY = profile.legSpan, pivot = Offset(120f, 526f)) }) {
                            drawPath(path = BodyMapPaths.feet, color = shellFill)
                            drawPath(path = BodyMapPaths.feet, color = canvasPalette.shellStroke, style = Stroke(width = 2.05f))
                        }
                    }
                }
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // ACCESSIBLE MUSCLE HOTSPOT NAVIGATION CHIPS (WCAG 2.1 AA >= 48dp)
        // ─────────────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AxiomSpacing.xs)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AxiomSpacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            accessibleMuscles.forEach { muscle ->
                val freshness = muscle.freshnessPercent
                val localizedName = getLocalizedMuscleName(
                    muscleId = muscle.id,
                    fallbackDisplayName = muscle.displayName,
                    context = context
                )
                val isSelected = selectedMuscleId == muscle.id
                val statusLabel = when {
                    freshness >= 80 -> stringResource(R.string.bodymap_status_recovered)
                    freshness >= 40 -> stringResource(R.string.bodymap_status_fatigued)
                    else -> stringResource(R.string.bodymap_status_sore)
                }
                val dotColor = when {
                    freshness >= 80 -> colors.systemGreen
                    freshness >= 40 -> colors.legendaryGold
                    else -> colors.penaltyRed
                }

                val itemDesc = stringResource(
                    R.string.bodymap_a11y_muscle_item,
                    localizedName,
                    freshness,
                    statusLabel
                )

                Box(
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .clip(RoundedCornerShape(AxiomRadius.m))
                        .background(if (isSelected) colors.shadowSurface else colors.dimSurface)
                        .border(
                            width = if (isSelected) AxiomBorder.thick else AxiomBorder.hairline,
                            color = if (isSelected) colors.legendaryGold else colors.borderFaint,
                            shape = RoundedCornerShape(AxiomRadius.m)
                        )
                        .clickable { onSelectMuscle(muscle.id) }
                        .semantics {
                            role = Role.Button
                            contentDescription = itemDesc
                        }
                        .padding(horizontal = AxiomSpacing.m, vertical = AxiomSpacing.s)
                        .testTag("muscle_chip_${muscle.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AxiomSpacing.xs)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Text(
                            text = localizedName,
                            fontFamily = Inter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.legendaryGold else colors.textPrimary
                        )
                        Text(
                            text = "$freshness%",
                            fontFamily = FiraCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = dotColor
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BITMAP PREPARATION & ARTWORK PIPELINE (Run on Dispatchers.Default)
// ─────────────────────────────────────────────────────────────────────────────

private fun prepareFrontArtwork(source: ImageBitmap, sex: BodyMapSex): ImageBitmap {
    val sourceBitmap = source.asAndroidBitmap()
    val width = sourceBitmap.width
    val height = sourceBitmap.height
    val pixels = IntArray(width * height)
    sourceBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    val originalPixels = pixels.copyOf()

    if (sex == BodyMapSex.Male) {
        for (y in 0 until minOf(10, height)) {
            for (x in 0 until width) {
                pixels[y * width + x] = AndroidColor.TRANSPARENT
            }
        }
        val handRows = 185..minOf(231, height - 1)
        val damagedHand = findShellComponent(
            pixels = originalPixels,
            width = width,
            height = height,
            xRange = 0..minOf(54, width - 1),
            yRange = handRows,
            seedX = 12,
            seedY = 202
        )
        val intactHand = findShellComponent(
            pixels = originalPixels,
            width = width,
            height = height,
            xRange = minOf(140, width - 1)..(width - 1),
            yRange = handRows,
            seedX = minOf(176, width - 1),
            seedY = 202
        )

        damagedHand.forEach { pixels[it] = AndroidColor.TRANSPARENT }
        intactHand.forEach { sourceIndex ->
            val sourceX = sourceIndex % width
            val sourceY = sourceIndex / width
            val targetX = width - 1 - sourceX
            pixels[sourceY * width + targetX] = originalPixels[sourceIndex]
        }
    }

    val clearRegions = if (sex == BodyMapSex.Male) {
        listOf(45..82, 94..131) to 318
    } else {
        listOf(72..98, 97..123) to 318
    }
    val (xRanges, startY) = clearRegions
    for (y in startY.coerceAtMost(height) until height) {
        xRanges.forEach { range ->
            for (x in range.first.coerceAtLeast(0)..range.last.coerceAtMost(width - 1)) {
                pixels[y * width + x] = AndroidColor.TRANSPARENT
            }
        }
    }

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, width, 0, 0, width, height)
    }.asImageBitmap()
}

private fun findShellComponent(
    pixels: IntArray,
    width: Int,
    height: Int,
    xRange: IntRange,
    yRange: IntRange,
    seedX: Int,
    seedY: Int
): IntArray {
    var seedIndex = -1
    var closestDistance = Int.MAX_VALUE
    for (y in yRange) {
        for (x in xRange) {
            val index = y * width + x
            if (!isShellPixel(pixels[index])) continue
            val dx = x - seedX
            val dy = y - seedY
            val distance = dx * dx + dy * dy
            if (distance < closestDistance) {
                seedIndex = index
                closestDistance = distance
            }
        }
    }
    if (seedIndex < 0) return IntArray(0)

    val visited = BooleanArray(pixels.size)
    val component = ArrayList<Int>()
    val queue = ArrayDeque<Int>()
    visited[seedIndex] = true
    queue.add(seedIndex)

    while (queue.isNotEmpty()) {
        val index = queue.removeFirst()
        component.add(index)
        val x = index % width
        val y = index / width
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                val nextX = x + dx
                val nextY = y + dy
                if (nextX !in xRange || nextY !in yRange || nextX !in 0 until width || nextY !in 0 until height) {
                    continue
                }
                val nextIndex = nextY * width + nextX
                if (!visited[nextIndex] && isShellPixel(pixels[nextIndex])) {
                    visited[nextIndex] = true
                    queue.add(nextIndex)
                }
            }
        }
    }
    return component.toIntArray()
}

private fun isShellPixel(pixel: Int): Boolean {
    if (AndroidColor.alpha(pixel) < 8) return false
    val red = AndroidColor.red(pixel)
    val green = AndroidColor.green(pixel)
    val blue = AndroidColor.blue(pixel)
    val brightness = (red + green + blue) / 3
    val channelSpread = maxOf(red, green, blue) - minOf(red, green, blue)
    return brightness in 140..235 && channelSpread <= 32
}

private fun outlineFrontArtwork(source: ImageBitmap): ImageBitmap {
    val sourceBitmap = source.asAndroidBitmap()
    val width = sourceBitmap.width
    val height = sourceBitmap.height
    val sourcePixels = IntArray(width * height)
    val outlinePixels = IntArray(width * height)
    sourceBitmap.getPixels(sourcePixels, 0, width, 0, 0, width, height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val index = y * width + x
            if (AndroidColor.alpha(sourcePixels[index]) >= 32) continue
            var neighborAlpha = 0
            for (dy in -1..1) {
                for (dx in -1..1) {
                    if (dx == 0 && dy == 0) continue
                    val nx = x + dx
                    val ny = y + dy
                    if (nx in 0 until width && ny in 0 until height) {
                        neighborAlpha = maxOf(
                            neighborAlpha,
                            AndroidColor.alpha(sourcePixels[ny * width + nx])
                        )
                    }
                }
            }
            if (neighborAlpha >= 32) {
                outlinePixels[index] = AndroidColor.argb(neighborAlpha, 255, 255, 255)
            }
        }
    }

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        setPixels(outlinePixels, 0, width, 0, 0, width, height)
    }.asImageBitmap()
}

private fun DrawScope.drawFrontExtremityRepair(
    sex: BodyMapSex,
    transform: BodyMapArtworkTransform,
    palette: AtlasCanvasPalette
) {
    val scaleX = transform.sourceWidth / CW * transform.scale * transform.horizontalScale
    val scaleY = transform.sourceHeight / 568f * transform.scale

    withTransform({
        translate(left = transform.offsetX, top = transform.offsetY - transform.topTrim * transform.scale)
        scale(scaleX = scaleX, scaleY = scaleY, pivot = Offset.Zero)
    }) {
        val feet = if (sex == BodyMapSex.Male) StaticFrontFeetMalePath else StaticFrontFeetFemalePath
        drawPath(path = feet, color = palette.shellFill)
        drawPath(path = feet, color = palette.muscleRim, style = Stroke(width = 1.45f))
        drawPath(
            path = feet,
            color = palette.shellStroke.copy(alpha = 0.55f),
            style = Stroke(width = 0.65f)
        )
    }
}

/**
 * Tints strength artwork keyed ONLY on (muscles, sex).
 * Does NOT bake selectedGroup into bitmap, preventing frame drops during selection bounce.
 */
private fun tintStrengthArtwork(
    source: ImageBitmap,
    muscles: List<MuscleGroup>,
    sex: BodyMapSex,
    palette: AtlasCanvasPalette
): ImageBitmap {
    val sourceBitmap = source.asAndroidBitmap()
    val width = sourceBitmap.width
    val height = sourceBitmap.height
    val pixels = IntArray(width * height)
    sourceBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    val scores = BodyMapAtlasModel.measuredStrengthMuscles(muscles)
        .associate { it.id to it.strengthScore }
    val hotspots = BodyMapGeometry.artworkHotspotsFor(BodyMapView.Front, sex)

    for (y in 0 until height) {
        val atlasY = y.toFloat() / (height - 1).coerceAtLeast(1) * 568f
        for (x in 0 until width) {
            val index = y * width + x
            val pixel = pixels[index]
            val alpha = AndroidColor.alpha(pixel)
            if (alpha < 32) continue

            val red = AndroidColor.red(pixel)
            val green = AndroidColor.green(pixel)
            val blue = AndroidColor.blue(pixel)
            val maximum = maxOf(red, green, blue)
            val minimum = minOf(red, green, blue)
            val saturation = if (maximum == 0) 0f else (maximum - minimum) / maximum.toFloat()
            val brightness = (red + green + blue) / 3f
            if (brightness >= 228f && saturation <= 0.12f) continue

            val atlasX = x.toFloat() / (width - 1).coerceAtLeast(1) * CW
            val muscleId = artworkMuscleForPixel(
                atlasX = atlasX,
                atlasY = atlasY,
                red = red,
                green = green,
                blue = blue,
                hotspots = hotspots
            ) ?: continue
            val score = scores[muscleId] ?: continue
            val target = strengthTint(score, palette)
            val mix = 0.35f

            pixels[index] = AndroidColor.argb(
                alpha,
                blendChannel(red, target.red, mix),
                blendChannel(green, target.green, mix),
                blendChannel(blue, target.blue, mix)
            )
        }
    }

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, width, 0, 0, width, height)
    }.asImageBitmap()
}

private fun artworkMuscleForPixel(
    atlasX: Float,
    atlasY: Float,
    red: Int,
    green: Int,
    blue: Int,
    hotspots: List<com.axiom.app.presentation.bodymap.BodyMapHotspot>
): String? {
    if (atlasY < 80f || atlasY > 535f) return null
    val maximumChannel = maxOf(red, green, blue)
    val minimumChannel = minOf(red, green, blue)
    val saturation = if (maximumChannel == 0) 0f else (maximumChannel - minimumChannel) / maximumChannel.toFloat()
    if (atlasY > 225f && abs(atlasX - 120f) > 76f && saturation < 0.16f) return null
    if (atlasY > 235f && abs(atlasX - 120f) > 92f) return null
    if (atlasY in 270f..305f && abs(atlasX - 120f) < 45f) return null

    val isChestRed = red >= 120 && red > green * 1.22f && red > blue * 1.12f
    if (isChestRed) return "chest"

    val nearest = hotspots
        .asSequence()
        .filter { it.muscleId != "chest" }
        .map { hotspot ->
            val dx = atlasX - hotspot.x
            val dy = atlasY - hotspot.y
            hotspot to (dx * dx + dy * dy)
        }
        .minByOrNull { (_, distanceSquared) -> distanceSquared }
        ?: return null
    val maximumDistance = when (nearest.first.muscleId) {
        "shoulders" -> 54f
        "biceps", "forearms" -> 72f
        "core" -> 82f
        "legs" -> 86f
        else -> 60f
    }
    return nearest.first.muscleId.takeIf { nearest.second <= maximumDistance * maximumDistance }
}

private fun blendChannel(source: Int, target: Float, mix: Float): Int =
    (source * (1f - mix) + target * 255f * mix).toInt().coerceIn(0, 255)

private fun strengthTint(score: Int, palette: AtlasCanvasPalette): Color {
    val strength = score.coerceIn(0, 100) / 100f
    return if (strength <= 0.5f) {
        lerp(palette.strengthLow, palette.strengthMid, strength * 2f)
    } else {
        lerp(palette.strengthMid, palette.strengthHigh, (strength - 0.5f) * 2f)
    }
}

private fun horizontalScaleFor(region: AtlasRegion, profile: BodyMapAnatomyProfile): Float =
    when (region.part) {
        AtlasRegionPart.Muscle -> BodyMapGeometry.horizontalScaleFor(region.id, profile)
        AtlasRegionPart.Thigh -> profile.legHorizontalSpan
        AtlasRegionPart.Knee -> profile.kneeSpan
        AtlasRegionPart.Calf -> profile.calfSpan
    }

private fun verticalScaleFor(muscleId: String, profile: BodyMapAnatomyProfile): Float =
    if (muscleId == "legs") profile.legSpan else 1f

private fun colorForRegion(
    muscleId: String,
    selectedGroup: String?,
    muscles: List<MuscleGroup>,
    displayMode: BodyMapDisplayMode,
    role: BodyMapRegionRole,
    colors: AxiomColorScheme,
    palette: AtlasCanvasPalette,
    alpha: Float
): Color {
    val muscle = muscles.firstOrNull { it.id == muscleId }
    return when (displayMode) {
        BodyMapDisplayMode.StrengthBalance -> if (muscle != null && BodyMapAtlasModel.isStrengthMeasured(muscle)) {
            strengthColor(
                score = muscle.strengthScore,
                isSelected = selectedGroup != null && BodyMapAtlasModel.groupLabelFor(muscleId) == selectedGroup,
                palette = palette,
                alpha = alpha
            )
        } else {
            palette.neutral.copy(alpha = 0.58f * alpha)
        }
        BodyMapDisplayMode.RecoveryReadiness -> recoveryColor(muscle?.freshnessPercent ?: 100, colors, alpha)
        BodyMapDisplayMode.MusclesInvolved -> when (role) {
            BodyMapRegionRole.Primary -> palette.primary.copy(alpha = 0.94f * alpha)
            BodyMapRegionRole.Secondary -> palette.secondary.copy(alpha = 0.88f * alpha)
            BodyMapRegionRole.Neutral -> palette.neutral.copy(alpha = 0.58f * alpha)
        }
    }
}

private fun strengthColor(
    score: Int,
    isSelected: Boolean,
    palette: AtlasCanvasPalette,
    alpha: Float
): Color = strengthTint(score, palette).copy(alpha = (if (isSelected) 0.95f else 0.82f) * alpha)

private fun recoveryColor(freshness: Int, colors: AxiomColorScheme, alpha: Float): Color =
    when {
        freshness >= 80 -> colors.systemGreen.copy(alpha = 0.78f * alpha)
        freshness >= 40 -> colors.legendaryGold.copy(alpha = 0.76f * alpha)
        else -> colors.penaltyRed.copy(alpha = 0.76f * alpha)
    }

private fun DrawScope.drawAnatomyLines(
    view: BodyMapView,
    sex: BodyMapSex,
    profile: BodyMapAnatomyProfile,
    palette: AtlasCanvasPalette
) {
    val light = palette.muscleLine.copy(alpha = 0.40f)
    val line = palette.muscleLine.copy(alpha = 0.75f)
    val width = 1.4f

    if (view == BodyMapView.Front) {
        drawPath(StaticFacePath, line.copy(alpha = 0.82f), style = Stroke(width = 1.9f))
        drawLine(light, Offset(107f, 56f), Offset(120f, 99f), 2.0f, cap = StrokeCap.Round)
        drawLine(light, Offset(133f, 56f), Offset(120f, 99f), 2.0f, cap = StrokeCap.Round)
        withTransform({ scale(scaleX = profile.armSpan, scaleY = 1f, pivot = Offset(120f, 220f)) }) {
            drawLine(light, Offset(50f, 143f), Offset(32f, 207f), width, cap = StrokeCap.Round)
            drawLine(light, Offset(190f, 143f), Offset(208f, 207f), width, cap = StrokeCap.Round)
            drawLine(light, Offset(42f, 211f), Offset(12f, 283f), width, cap = StrokeCap.Round)
            drawLine(light, Offset(198f, 211f), Offset(228f, 283f), width, cap = StrokeCap.Round)
        }
        if (sex == BodyMapSex.Female) {
            withTransform({ scale(scaleX = profile.pelvisSpan, scaleY = 1f, pivot = Offset(120f, 294f)) }) {
                drawLine(line.copy(alpha = 0.48f), Offset(96f, 284f), Offset(82f, 310f), 1.7f, cap = StrokeCap.Round)
                drawLine(line.copy(alpha = 0.48f), Offset(144f, 284f), Offset(158f, 310f), 1.7f, cap = StrokeCap.Round)
            }
            drawLine(light, Offset(108f, 19f), Offset(133f, 33f), 1.65f, cap = StrokeCap.Round)
        }
    } else {
        drawLine(light, Offset(84f, 100f), Offset(108f, 124f), width, cap = StrokeCap.Round)
        drawLine(light, Offset(156f, 100f), Offset(132f, 124f), width, cap = StrokeCap.Round)
        drawLine(light, Offset(120f, 116f), Offset(120f, 243f), 1.8f, cap = StrokeCap.Round)
        drawLine(light, Offset(108f, 127f), Offset(88f, 145f), width, cap = StrokeCap.Round)
        drawLine(light, Offset(132f, 127f), Offset(152f, 145f), width, cap = StrokeCap.Round)
        drawLine(light, Offset(88f, 145f), Offset(103f, 166f), width, cap = StrokeCap.Round)
        drawLine(light, Offset(152f, 145f), Offset(137f, 166f), width, cap = StrokeCap.Round)
        drawLine(light, Offset(74f, 184f), Offset(103f, 166f), width, cap = StrokeCap.Round)
        drawLine(light, Offset(166f, 184f), Offset(137f, 166f), width, cap = StrokeCap.Round)
        drawLine(light, Offset(103f, 166f), Offset(102f, 226f), width, cap = StrokeCap.Round)
        drawLine(light, Offset(137f, 166f), Offset(138f, 226f), width, cap = StrokeCap.Round)
        withTransform({ scale(scaleX = profile.pelvisSpan, scaleY = 1f, pivot = Offset(120f, 294f)) }) {
            drawLine(light, Offset(96f, 270f), Offset(81f, 314f), 1.8f, cap = StrokeCap.Round)
            drawLine(light, Offset(144f, 270f), Offset(159f, 314f), 1.8f, cap = StrokeCap.Round)
        }
        withTransform({ scale(scaleX = profile.armSpan, scaleY = 1f, pivot = Offset(120f, 220f)) }) {
            drawLine(light, Offset(50f, 123f), Offset(32f, 201f), width, cap = StrokeCap.Round)
            drawLine(light, Offset(190f, 123f), Offset(208f, 201f), width, cap = StrokeCap.Round)
            drawLine(light, Offset(42f, 211f), Offset(12f, 283f), width, cap = StrokeCap.Round)
            drawLine(light, Offset(198f, 211f), Offset(228f, 283f), width, cap = StrokeCap.Round)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CANONICAL-TOKEN-COMPLIANT ATLAS PALETTE (Manifest §12 Remediation)
// ─────────────────────────────────────────────────────────────────────────────

private data class AtlasCanvasPalette(
    val shellFill: Color,
    val shellStroke: Color,
    val muscleRim: Color,
    val muscleLine: Color,
    val innerShadow: Color,
    val hair: Color,
    val activeAccent: Color,
    val primary: Color,
    val secondary: Color,
    val shoulder: Color,
    val coreRib: Color,
    val coreSide: Color,
    val core: Color,
    val legPrimary: Color,
    val legSecondary: Color,
    val leg: Color,
    val neutral: Color,
    val strengthLow: Color,
    val strengthMid: Color,
    val strengthHigh: Color
) {
    companion object {
        fun from(colors: AxiomColorScheme, isDark: Boolean): AtlasCanvasPalette =
            AtlasCanvasPalette(
                shellFill = colors.dimSurface.copy(alpha = if (isDark) 0.42f else 0.60f),
                shellStroke = colors.borderFaint.copy(alpha = 0.88f),
                muscleRim = colors.textPrimary.copy(alpha = 0.92f),
                muscleLine = colors.borderFaint,
                innerShadow = colors.voidBlack,
                hair = colors.dimSurface,
                activeAccent = colors.legendaryGold,
                primary = colors.penaltyRed,
                secondary = colors.rareBlue,
                shoulder = colors.dimSurface,
                coreRib = colors.shadowSurface,
                coreSide = colors.dimSurface,
                core = colors.dimSurface,
                legPrimary = colors.dimSurface,
                legSecondary = colors.dimSurface,
                leg = colors.dimSurface,
                neutral = colors.dimSurface,
                strengthLow = colors.penaltyRed,
                strengthMid = colors.legendaryGold,
                strengthHigh = colors.systemGreen
            )
    }
}
