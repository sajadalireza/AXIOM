package com.axiom.app.presentation.skilltree

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.domain.model.Skill
import com.axiom.app.ui.SkillTreeViewModel
import com.axiom.app.ui.SkillTreeUiState
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Category configs
private data class VisualCategory(
    val name: String,
    val color: Color,
    val angleDegrees: Float
)

private data class ParticleBurst(
    val x: Float, // model position
    val y: Float, // model position
    val startTime: Long
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun SkillTreeCanvas(
    viewModel: SkillTreeViewModel,
    modifier: Modifier = Modifier,
    onCenterHubClick: () -> Unit = {}
) {
    val uiState by viewModel.skillsState.collectAsStateWithLifecycle()
    val selectedSkillId by viewModel.selectedSkillId.collectAsStateWithLifecycle()
    val activeUpgradeAnim by viewModel.upgradeAnimationState.collectAsStateWithLifecycle()
    val collapsedSkillIds by viewModel.collapsedSkillIds.collectAsStateWithLifecycle()

    // Theme-aware colors — read here so Canvas draw lambdas can capture them
    val colors = LocalAxiomColors.current
    val themeVoidBlack = colors.voidBlack

    val upgradeProgress = remember(activeUpgradeAnim) { Animatable(0f) }
    LaunchedEffect(activeUpgradeAnim) {
        if (activeUpgradeAnim != null) {
            upgradeProgress.snapTo(0f)
            upgradeProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
            )
        }
    }

    val density = LocalDensity.current
    val densityPx = density.density
    val textMeasurer = rememberTextMeasurer()
    val context = androidx.compose.ui.platform.LocalContext.current

    val programmingLabel = stringResource(R.string.category_programming)
    val businessLabel = stringResource(R.string.category_business)
    val knowledgeLabel = stringResource(R.string.category_knowledge)
    val healthLabel = stringResource(R.string.category_health)
    val creativityLabel = stringResource(R.string.category_creativity)
    val zoneTemplate = stringResource(R.string.skill_canvas_zone)

    val skillsList: List<Skill> = when (val s = uiState) {
        is SkillTreeUiState.Success -> s.skills
        else -> emptyList()
    }

    // Dynamic Categories based on skillsList to cleanly support custom sectors
    val visualCategories = remember(skillsList, programmingLabel, businessLabel, knowledgeLabel, healthLabel, creativityLabel) {
        val defaultCats = listOf(
            programmingLabel to SystemGreen,
            businessLabel to LegendaryGold,
            knowledgeLabel to RareBlue,
            healthLabel to UncommonTeal,
            creativityLabel to EpicPurple
        )
        val defaultNamesUpper = defaultCats.map { it.first.uppercase().trim() }.toSet()
        val customCats = skillsList
            .map { it.category.trim() }
            .filter { it.isNotBlank() && !defaultNamesUpper.contains(it.uppercase()) }
            .distinctBy { it.uppercase() }

        val combined = defaultCats.toMutableList()
        val customColors = listOf(
            Color(0xFFFF79C6), // Hot pink
            Color(0xFF8BE9FD), // Cyan
            Color(0xFFFFB86C), // Orange
            Color(0xFFBD93F9), // Light purple
            Color(0xFF50FA7B)  // Neon green
        )
        customCats.forEachIndexed { i, cat ->
            val color = customColors[i % customColors.size]
            combined.add(cat to color)
        }

        val n = combined.size
        val wedgeAngle = 360f / n
        combined.mapIndexed { index, (name, color) ->
            val angleDegrees = -90f + (index * wedgeAngle)
            VisualCategory(name, color, angleDegrees)
        }
    }

    // Gesture State
    val coroutineScope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1.0f) }
    val offsetAnim = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val scale = scaleAnim.value
    val offset = offsetAnim.value

    var layoutWidth by remember { mutableStateOf(0f) }
    var layoutHeight by remember { mutableStateOf(0f) }

    // Pulsing, scales, light travel Spec
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_and_light_travel")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "candidate_pulse"
    )

    val travelingPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "light_phase"
    )

    val selectedScaleAnim by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = EaseInOutBack),
            repeatMode = RepeatMode.Reverse
        ),
        label = "selected_scale"
    )

    val breathingGlowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_glow_scale"
    )

    val selectedRippleProgress = remember { Animatable(0f) }
    LaunchedEffect(selectedSkillId) {
        if (selectedSkillId != null) {
            selectedRippleProgress.snapTo(0f)
            selectedRippleProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = EaseOutQuad)
            )
        }
    }

    val isSkillHidden = remember(skillsList, collapsedSkillIds) {
        val map = HashMap<String, Skill>()
        for (s in skillsList) {
            map[s.id] = s
        }
        { skillId: String ->
            var hidden = false
            var currentId: String? = skillId
            while (currentId != null) {
                val current = map[currentId]
                val parentId = current?.parentId
                if (parentId != null && collapsedSkillIds.contains(parentId)) {
                    hidden = true
                    break
                }
                currentId = parentId
            }
            hidden
        }
    }

    // Particle bursts lists & ticker setups
    var previousUnlockedIds by remember { mutableStateOf(skillsList.filter { it.isUnlocked }.map { it.id }.toSet()) }
    val activeBursts = remember { mutableStateListOf<ParticleBurst>() }

    // Memoize the mapping logic so we don't recalculate on every tick
    val computedBranchPositions = remember(skillsList, visualCategories) {
        val mappedPositions = mutableMapOf<String, Offset>()
        if (skillsList.isEmpty()) return@remember mappedPositions

        val getBranchIndex = { categoryName: String ->
            val normalizedCat = normalizeCategoryName(
                categoryName,
                programmingLabel,
                businessLabel,
                knowledgeLabel,
                healthLabel,
                creativityLabel
            )
            var idx = visualCategories.indexOfFirst {
                it.name.uppercase().trim() == normalizedCat.uppercase().trim()
            }
            if (idx == -1) {
                idx = Math.abs(normalizedCat.hashCode()) % visualCategories.size
            }
            idx
        }

        val roots = skillsList.filter { it.parentId == null }
        val rootsByBranch = roots.groupBy { getBranchIndex(it.category) }

        val wedgeSize = 360f / visualCategories.size
        val maxSpreadDegrees = wedgeSize * 0.6f

        rootsByBranch.forEach { (branchIndex, categoryRoots) ->
            if (branchIndex in visualCategories.indices) {
                val config = visualCategories[branchIndex]
                val baseAngleDegrees = config.angleDegrees
                val baseAngleRad = Math.toRadians(baseAngleDegrees.toDouble())

                val K = categoryRoots.size
                categoryRoots.forEachIndexed { i, skill ->
                    val spreadStep = if (K > 1) {
                        -maxSpreadDegrees / 2f + (i * (maxSpreadDegrees / (K - 1)))
                    } else {
                        0f
                    }
                    val rootAngleRad = baseAngleRad + Math.toRadians(spreadStep.toDouble())
                    val distance = 220f + (i * 90f)
                    val x = (distance * cos(rootAngleRad)).toFloat()
                    val y = (distance * sin(rootAngleRad)).toFloat()

                    mappedPositions[skill.id] = Offset(x, y)
                }
            }
        }

        val children = skillsList.filter { it.parentId != null }
        val parentToKids = children.groupBy { it.parentId }

        val queue = java.util.ArrayDeque<String>()
        val visited = mutableSetOf<String>()

        roots.forEach { root ->
            if (mappedPositions.containsKey(root.id)) {
                queue.add(root.id)
                visited.add(root.id)
            }
        }

        while (queue.isNotEmpty()) {
            val parentId = queue.poll() ?: break
            val parentPos = mappedPositions[parentId] ?: Offset.Zero
            val parentSkill = skillsList.firstOrNull { it.id == parentId }
            val parentCategory = parentSkill?.category ?: "Mind"
            val branchIndex = getBranchIndex(parentCategory)

            val dx = parentPos.x
            val dy = parentPos.y
            val parentAngleRad = kotlin.math.atan2(dy.toDouble(), dx.toDouble())

            val angleSpread = 40.0

            val kids = parentToKids[parentId] ?: emptyList()
            val kidsCount = kids.size
            kids.forEachIndexed { index, kid ->
                val spreadStep = if (kidsCount > 1) {
                    -angleSpread / 2.0 + (index * (angleSpread / (kidsCount - 1)))
                } else {
                    0.0
                }
                val kidAngleRad = parentAngleRad + Math.toRadians(spreadStep)
                val distance = 165f

                val x = parentPos.x + (distance * cos(kidAngleRad)).toFloat()
                val y = parentPos.y + (distance * sin(kidAngleRad)).toFloat()

                mappedPositions[kid.id] = Offset(x, y)
                if (!visited.contains(kid.id)) {
                    visited.add(kid.id)
                    queue.add(kid.id)
                }
            }
        }

        val stranded = skillsList.filter { !visited.contains(it.id) }
        val strandedByCategory = stranded.groupBy { getBranchIndex(it.category) }

        strandedByCategory.forEach { (branchIndex, categoryStranded) ->
            if (branchIndex in visualCategories.indices) {
                val config = visualCategories[branchIndex]
                val angleRad = Math.toRadians(config.angleDegrees.toDouble())

                categoryStranded.forEachIndexed { count, skill ->
                    val distance = 350f + (count * 150f)
                    val x = (distance * cos(angleRad)).toFloat()
                    val y = (distance * sin(angleRad)).toFloat()
                    mappedPositions[skill.id] = Offset(x, y)
                }
            }
        }

        mappedPositions
    }

    // O(1) Spatial Hash and Bounding Box Cache setup
    val cellSize = 150f
    val spatialGrid = remember(skillsList, computedBranchPositions) {
        val grid = mutableMapOf<Pair<Int, Int>, MutableList<String>>()
        skillsList.forEach { skill ->
            val pos = computedBranchPositions[skill.id]
            if (pos != null) {
                val x = pos.x * densityPx
                val y = pos.y * densityPx
                val cellX = (x / cellSize).toInt()
                val cellY = (y / cellSize).toInt()
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        grid.getOrPut(Pair(cellX + dx, cellY + dy)) { mutableListOf() }.add(skill.id)
                    }
                }
            }
        }
        grid
    }

    val nodeBounds = remember(skillsList, computedBranchPositions) {
        val map = mutableStateMapOf<String, Rect>()
        skillsList.forEach { skill ->
            val pos = computedBranchPositions[skill.id]
            if (pos != null) {
                val isRoot = skill.parentId == null
                val isBranch = skill.parentId != null && skillsList.any { it.parentId == skill.id }
                val tierSize = when {
                    isRoot -> 44.dp
                    isBranch -> 36.dp
                    else -> 28.dp
                }
                val radius = with(density) { (tierSize / 2f).toPx() }
                map[skill.id] = Rect(
                    left = pos.x * densityPx - radius,
                    top = pos.y * densityPx - radius,
                    right = pos.x * densityPx + radius,
                    bottom = pos.y * densityPx + radius
                )
            }
        }
        map
    }

    // Trigger newly unlocked particle bursts
    LaunchedEffect(skillsList) {
        val currentUnlocked = skillsList.filter { it.isUnlocked }.map { it.id }.toSet()
        val newlyUnlocked = currentUnlocked - previousUnlockedIds
        newlyUnlocked.forEach { id ->
            val pos = computedBranchPositions[id]
            if (pos != null) {
                activeBursts.add(ParticleBurst(pos.x, pos.y, System.currentTimeMillis()))
            }
        }
        previousUnlockedIds = currentUnlocked
    }

    // Active particle ticks
    LaunchedEffect(Unit) {
        while (true) {
            if (activeBursts.isNotEmpty()) {
                val now = System.currentTimeMillis()
                activeBursts.removeAll { now - it.startTime > 1000L }
                delay(16L)
            } else {
                delay(100L)
            }
        }
    }

    // Network awakening path trace animation on load
    val loadTime = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        loadTime.animateTo(
            targetValue = 3000f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack)
            .onSizeChanged { size ->
                if (size.width > 0 && size.height > 0) {
                    layoutWidth = size.width.toFloat()
                    layoutHeight = size.height.toFloat()
                    if (offsetAnim.value == Offset.Zero) {
                        coroutineScope.launch {
                            val initialSelected = selectedSkillId
                            val modelPos = if (initialSelected != null) computedBranchPositions[initialSelected] else null
                            if (modelPos != null) {
                                val targetScale = 1.35f
                                val targetOffsetX = layoutWidth / 2f - (modelPos.x * densityPx * targetScale)
                                val targetOffsetY = layoutHeight / 2f - (modelPos.y * densityPx * targetScale)
                                scaleAnim.snapTo(targetScale)
                                offsetAnim.snapTo(Offset(targetOffsetX, targetOffsetY))
                            } else {
                                offsetAnim.snapTo(Offset(layoutWidth / 2f, layoutHeight / 2f))
                            }
                        }
                    }
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("skill_tree_canvas")
                .pointerInput(skillsList, computedBranchPositions, isSkillHidden) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        coroutineScope.launch {
                            val oldScale = scaleAnim.value
                            val targetScale = (oldScale * zoom).coerceIn(0.4f, 3.0f)
                            val scaleFactor = targetScale / oldScale
                            val targetOffset = (offsetAnim.value + pan) - (centroid - offsetAnim.value) * (scaleFactor - 1f)
                            
                            scaleAnim.snapTo(targetScale)
                            offsetAnim.snapTo(targetOffset)
                        }
                    }
                }
                .pointerInput(skillsList, computedBranchPositions, isSkillHidden) {
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->
                            coroutineScope.launch {
                                scaleAnim.animateTo(1.0f, spring(stiffness = Spring.StiffnessLow))
                                offsetAnim.animateTo(Offset(layoutWidth / 2f, layoutHeight / 2f), spring(stiffness = Spring.StiffnessLow))
                            }
                        },
                        onTap = { tapOffset ->
                            val untransformedTap = Offset(
                                (tapOffset.x - offsetAnim.value.x) / scaleAnim.value,
                                (tapOffset.y - offsetAnim.value.y) / scaleAnim.value
                            )
                            val cellX = (untransformedTap.x / cellSize).toInt()
                            val cellY = (untransformedTap.y / cellSize).toInt()
                            val candidateIds = spatialGrid[Pair(cellX, cellY)] ?: emptyList()

                            var selectedNodeId: String? = null
                            var minDistance = Float.MAX_VALUE

                            candidateIds.forEach { id ->
                                val rect = nodeBounds[id]
                                if (rect != null && rect.contains(untransformedTap)) {
                                    val radius = rect.width / 2f
                                    val badgeCenter = Offset(rect.center.x + radius, rect.center.y - radius)
                                    val distToBadge = (untransformedTap - badgeCenter).getDistance()
                                    if (distToBadge < with(density) { 24.dp.toPx() }) {
                                        viewModel.toggleCollapseSkill(id)
                                        return@detectTapGestures
                                    }
                                    
                                    val center = rect.center
                                    val dx = untransformedTap.x - center.x
                                    val dy = untransformedTap.y - center.y
                                    val dist = sqrt(dx * dx + dy * dy)
                                    if (dist < minDistance) {
                                        minDistance = dist
                                        selectedNodeId = id
                                    }
                                }
                            }

                            if (selectedNodeId != null) {
                                viewModel.selectSkill(selectedNodeId!!)
                            } else {
                                val dxCenter = untransformedTap.x - 0f
                                val dyCenter = untransformedTap.y - 0f
                                val distToCenter = sqrt(dxCenter * dxCenter + dyCenter * dyCenter)
                                val centerInteractRadius = with(density) { 36.dp.toPx() }
                                if (distToCenter <= centerInteractRadius) {
                                    onCenterHubClick()
                                } else {
                                    viewModel.clearSelection()
                                }
                            }
                        }
                    )
                }
        ) {
            val densityPx = density.density

            // 1. Subtle radial gradient from center
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(themeVoidBlack, BorderFaint.copy(alpha = 0.02f)),
                    center = offset,
                    radius = maxOf(size.width, size.height)
                )
            )

            // Category labels & wedge lines
            visualCategories.forEach { category ->
                val angleRad = Math.toRadians(category.angleDegrees.toDouble())
                val endPoint = Offset(
                    offset.x + (1000f * cos(angleRad)).toFloat() * scale,
                    offset.y + (1000f * sin(angleRad)).toFloat() * scale
                )

                drawLine(
                    color = category.color.copy(alpha = 0.05f),
                    start = offset,
                    end = endPoint,
                    strokeWidth = 1.dp.toPx() * scale
                )

                // Render sector category names
                val textAlpha = ((scale - 0.35f) / 0.25f).coerceIn(0f, 1f)
                if (textAlpha > 0.05f) {
                    val labelLayout = textMeasurer.measure(
                        text = AnnotatedString(category.name.uppercase()),
                        style = TextStyle(
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = (10f * scale).coerceIn(4f, 14f).sp,
                            color = category.color.copy(alpha = 0.44f * textAlpha)
                        )
                    )

                    val labelDist = 340f * densityPx * scale
                    val labelX = offset.x + (labelDist * cos(angleRad)).toFloat()
                    val labelY = offset.y + (labelDist * sin(angleRad)).toFloat()

                    drawText(
                        textLayoutResult = labelLayout,
                        topLeft = Offset(
                            labelX - (labelLayout.size.width / 2),
                            labelY - (labelLayout.size.height / 2)
                        )
                    )
                }
            }

            // 2. Draw central faint glyph ring
            drawCircle(
                color = Color(0xFF9E9E9E).copy(alpha = 0.15f),
                radius = 70.dp.toPx() * scale,
                center = offset,
                style = Stroke(width = 1.dp.toPx())
            )

            // 3. DRAW CONNECTIONS BEFORE NODES
            var connectionIndex = 0
            val pathMeasure = PathMeasure()

            skillsList.forEach { skill ->
                if (isSkillHidden(skill.id)) return@forEach
                val parentId = skill.parentId
                if (parentId != null) {
                    val pPos = computedBranchPositions[parentId]
                    val kPos = computedBranchPositions[skill.id]

                    if (pPos != null && kPos != null) {
                        connectionIndex++
                        val elapsedMs = loadTime.value
                        val edgeDelay = connectionIndex * 50f
                        val edgeProgress = ((elapsedMs - edgeDelay) / 600f).coerceIn(0f, 1f)

                        if (edgeProgress > 0f) {
                            val startPx = Offset(
                                offset.x + (pPos.x * densityPx * scale),
                                offset.y + (pPos.y * densityPx * scale)
                            )
                            val endPx = Offset(
                                offset.x + (kPos.x * densityPx * scale),
                                offset.y + (kPos.y * densityPx * scale)
                            )

                            val parentSkill = skillsList.firstOrNull { it.id == parentId }
                            val isParentUnlocked = parentSkill?.isUnlocked == true
                            val isBothUnlocked = skill.isUnlocked && isParentUnlocked

                            val dx = endPx.x - startPx.x
                            val dy = endPx.y - startPx.y

                            val ctrlX = -0.3f * dy
                            val ctrlY = 0.3f * dx

                            val cp1 = Offset(startPx.x + dx / 3f + ctrlX, startPx.y + dy / 3f + ctrlY)
                            val cp2 = Offset(startPx.x + 2f * dx / 3f - ctrlX, startPx.y + 2f * dy / 3f - ctrlY)

                            val curvePath = Path().apply {
                                moveTo(startPx.x, startPx.y)
                                cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, endPx.x, endPx.y)
                            }

                            val drawPathSegment = if (edgeProgress < 1f) {
                                val segmentPath = Path()
                                pathMeasure.setPath(curvePath, false)
                                pathMeasure.getSegment(0f, pathMeasure.length * edgeProgress, segmentPath, true)
                                segmentPath
                            } else {
                                curvePath
                            }

                            if (isBothUnlocked) {
                                drawPath(
                                    path = drawPathSegment,
                                    color = SystemGreen.copy(alpha = 0.6f),
                                    style = Stroke(width = 2.dp.toPx() * scale)
                                )
                            } else if (isParentUnlocked) {
                                drawPath(
                                    path = drawPathSegment,
                                    color = SystemGreen.copy(alpha = 0.35f),
                                    style = Stroke(
                                        width = 2.dp.toPx() * scale,
                                        pathEffect = PathEffect.dashPathEffect(
                                            intervals = floatArrayOf(12.dp.toPx() * scale, 8.dp.toPx() * scale),
                                            phase = 0f
                                        )
                                    )
                                )
                            } else {
                                drawPath(
                                    path = drawPathSegment,
                                    color = BorderFaint.copy(alpha = 0.4f),
                                    style = Stroke(
                                        width = 1.5.dp.toPx() * scale,
                                        pathEffect = PathEffect.dashPathEffect(
                                            intervals = floatArrayOf(8.0.dp.toPx() * scale, 6.0.dp.toPx() * scale),
                                            phase = 0f
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // UPGRADE ANIMATION LAYER
            activeUpgradeAnim?.let { anim ->
                val pPos = anim.parentId?.let { computedBranchPositions[it] } ?: Offset.Zero
                val kPos = computedBranchPositions[anim.childId]

                if (kPos != null) {
                    val startPx = Offset(
                        offset.x + (pPos.x * densityPx * scale),
                        offset.y + (pPos.y * densityPx * scale)
                    )
                    val endPx = Offset(
                        offset.x + (kPos.x * densityPx * scale),
                        offset.y + (kPos.y * densityPx * scale)
                    )

                    val tShared = upgradeProgress.value

                    val dx = endPx.x - startPx.x
                    val dy = endPx.y - startPx.y
                    val ctrlX = -0.3f * dy
                    val ctrlY = 0.3f * dx

                    val cp1 = Offset(startPx.x + dx / 3f + ctrlX, startPx.y + dy / 3f + ctrlY)
                    val cp2 = Offset(startPx.x + 2f * dx / 3f - ctrlX, startPx.y + 2f * dy / 3f - ctrlY)

                    val curvePath = Path().apply {
                        moveTo(startPx.x, startPx.y)
                        cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, endPx.x, endPx.y)
                    }

                    drawPath(
                        path = curvePath,
                        color = SystemGlint.copy(alpha = 0.35f * (1f - tShared)),
                        style = Stroke(width = 8.dp.toPx() * scale)
                    )

                    drawPath(
                        path = curvePath,
                        color = Color.White.copy(alpha = 0.85f * (1f - tShared)),
                        style = Stroke(width = 3.dp.toPx() * scale)
                    )

                    val cometPos = getBezierPosition(startPx, cp1, cp2, endPx, tShared)
                    drawCircle(
                        color = SystemGlint,
                        radius = 12.dp.toPx() * scale,
                        center = cometPos
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 6.dp.toPx() * scale,
                        center = cometPos
                    )

                    for (s in 0 until 6) {
                        val angle = (s * (360f / 6f)) * (Math.PI / 180f)
                        val rOffset = 18.dp.toPx() * scale * sin((tShared * 50f + s).toDouble()).toFloat()
                        val sparklePos = Offset(
                            cometPos.x + (rOffset * cos(angle)).toFloat(),
                            cometPos.y + (rOffset * sin(angle)).toFloat()
                        )
                        drawCircle(
                            color = LegendaryGold.copy(alpha = 0.9f),
                            radius = 2.dp.toPx() * scale,
                            center = sparklePos
                        )
                    }
                }
            }

            // 4. DRAW NODES ON TOP
            skillsList.forEach { skill ->
                if (isSkillHidden(skill.id)) return@forEach
                val modelPos = computedBranchPositions[skill.id]
                if (modelPos != null) {
                    val nodeCenter = Offset(
                        offset.x + (modelPos.x * densityPx * scale),
                        offset.y + (modelPos.y * densityPx * scale)
                    )

                    val isSelected = skill.id == selectedSkillId
                    val isRoot = skill.parentId == null
                    val isBranch = skill.parentId != null && skillsList.any { it.parentId == skill.id }

                    val tierSize = when {
                        isRoot -> 44.dp
                        isBranch -> 36.dp
                        else -> 28.dp
                    }

                    val currentScale = if (isSelected) selectedScaleAnim else 1.0f
                    val rawRadius = with(density) { (tierSize / 2f).toPx() } * scale
                    val nodeRadius = rawRadius * currentScale

                    val rarityColor = Color(skill.rankColor.toInt())
                    val rankLabelClean = skill.rankLabel.uppercase().replace("-RANK", "").trim()
                    val isS_Rank = rankLabelClean == "S" || rankLabelClean == "S+" || rankLabelClean == "LEGENDARY"

                    val isParentUnlocked = skill.parentId?.let { pId ->
                        skillsList.firstOrNull { it.id == pId }?.isUnlocked
                    } ?: true

                    val state = when {
                        !skill.isUnlocked && !isParentUnlocked -> "LOCKED"
                        !skill.isUnlocked && isParentUnlocked -> "AVAILABLE"
                        skill.isUnlocked && (skill.level >= 10 || isS_Rank) -> "MASTERED"
                        else -> "ACTIVE"
                    }

                    // Level of Detail zoom optimization
                    if (scale < 0.5f) {
                        val dotColor = when (state) {
                            "LOCKED" -> BorderFaint.copy(alpha = 0.4f)
                            "AVAILABLE" -> SystemGreen.copy(alpha = 0.5f)
                            "MASTERED" -> LegendaryGold
                            else -> rarityColor
                        }
                        drawCircle(
                            color = dotColor,
                            radius = nodeRadius * 0.6f,
                            center = nodeCenter
                        )
                    } else {
                        // Drawing logic by visual state
                        when (state) {
                            "LOCKED" -> {
                                drawCircle(
                                    color = BorderFaint.copy(alpha = 0.4f),
                                    radius = nodeRadius,
                                    center = nodeCenter,
                                    style = Stroke(width = 2.dp.toPx() * scale)
                                )

                                val symbolLayout = textMeasurer.measure(
                                    text = AnnotatedString("🔒"),
                                    style = TextStyle(
                                        fontSize = (13.sp.value * scale * currentScale).coerceIn(4f, 22f).sp,
                                        color = TextDim.copy(alpha = 0.4f)
                                    )
                                )
                                drawText(
                                    textLayoutResult = symbolLayout,
                                    topLeft = Offset(
                                        nodeCenter.x - (symbolLayout.size.width / 2f),
                                        nodeCenter.y - (symbolLayout.size.height / 2f)
                                    )
                                )
                            }
                            "AVAILABLE" -> {
                                drawCircle(
                                    color = SystemGreen.copy(alpha = pulseAlpha),
                                    radius = nodeRadius,
                                    center = nodeCenter,
                                    style = Stroke(width = 2.dp.toPx() * scale)
                                )

                                val symbolLayout = textMeasurer.measure(
                                    text = AnnotatedString("?"),
                                    style = TextStyle(
                                        fontFamily = JetBrainsMono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (13.sp.value * scale * currentScale).coerceIn(4f, 22f).sp,
                                        color = SystemGreen.copy(alpha = pulseAlpha)
                                    )
                                )
                                drawText(
                                    textLayoutResult = symbolLayout,
                                    topLeft = Offset(
                                        nodeCenter.x - (symbolLayout.size.width / 2f),
                                        nodeCenter.y - (symbolLayout.size.height / 2f)
                                    )
                                )
                            }
                            "ACTIVE" -> {
                                drawIntoCanvas { canvas ->
                                    val glowPaint = Paint().asFrameworkPaint().apply {
                                        isAntiAlias = true
                                        color = rarityColor.toArgb()
                                        maskFilter = android.graphics.BlurMaskFilter(12.dp.toPx() * scale, android.graphics.BlurMaskFilter.Blur.NORMAL)
                                    }
                                    canvas.nativeCanvas.drawCircle(nodeCenter.x, nodeCenter.y, nodeRadius + 4.dp.toPx() * scale, glowPaint)
                                }

                                drawCircle(
                                    color = rarityColor,
                                    radius = nodeRadius,
                                    center = nodeCenter
                                )

                                drawCircle(
                                    color = themeVoidBlack.copy(alpha = 0.55f),
                                    radius = nodeRadius * 0.9f,
                                    center = nodeCenter
                                )

                                drawCircle(
                                    color = rarityColor,
                                    radius = nodeRadius,
                                    center = nodeCenter,
                                    style = Stroke(width = 2.dp.toPx() * scale)
                                )

                                val nodeSymbol = skill.rankLabel.take(2)
                                val symbolLayout = textMeasurer.measure(
                                    text = AnnotatedString(nodeSymbol),
                                    style = TextStyle(
                                        fontFamily = JetBrainsMono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (13.sp.value * scale * currentScale).coerceIn(4f, 22f).sp,
                                        color = rarityColor
                                    )
                                )
                                drawText(
                                    textLayoutResult = symbolLayout,
                                    topLeft = Offset(
                                        nodeCenter.x - (symbolLayout.size.width / 2f),
                                        nodeCenter.y - (symbolLayout.size.height / 2f)
                                    )
                                )
                            }
                            "MASTERED" -> {
                                val goldGradient = Brush.linearGradient(
                                    colors = listOf(LegendaryGold, Color(0xFFFFF0B3), LegendaryGold),
                                    start = Offset(nodeCenter.x - nodeRadius, nodeCenter.y - nodeRadius),
                                    end = Offset(nodeCenter.x + nodeRadius, nodeCenter.y + nodeRadius)
                                )
                                drawCircle(
                                    brush = goldGradient,
                                    radius = nodeRadius,
                                    center = nodeCenter
                                )

                                drawCircle(
                                    color = themeVoidBlack.copy(alpha = 0.55f),
                                    radius = nodeRadius * 0.9f,
                                    center = nodeCenter
                                )

                                drawCircle(
                                    color = LegendaryGold,
                                    radius = nodeRadius,
                                    center = nodeCenter,
                                    style = Stroke(width = 3.dp.toPx() * scale)
                                )

                                val nodeSymbol = skill.rankLabel.take(2)
                                val symbolLayout = textMeasurer.measure(
                                    text = AnnotatedString(nodeSymbol),
                                    style = TextStyle(
                                        fontFamily = JetBrainsMono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (13.sp.value * scale * currentScale).coerceIn(4f, 22f).sp,
                                        color = LegendaryGold
                                    )
                                )
                                drawText(
                                    textLayoutResult = symbolLayout,
                                    topLeft = Offset(
                                        nodeCenter.x - (symbolLayout.size.width / 2f),
                                        nodeCenter.y - (symbolLayout.size.height / 2f)
                                    )
                                )

                                val crownLayout = textMeasurer.measure(
                                    text = AnnotatedString("👑"),
                                    style = TextStyle(
                                        fontSize = (11 * scale * currentScale).sp
                                    )
                                )
                                drawText(
                                    textLayoutResult = crownLayout,
                                    topLeft = Offset(
                                        nodeCenter.x - (crownLayout.size.width / 2f),
                                        nodeCenter.y - nodeRadius - (14.dp.toPx() * scale)
                                    )
                                )
                            }
                        }

                        // Draw fold/unfold tactile badge (⊕ / ⊖) if there are children
                        val hasChildren = skillsList.any { it.parentId == skill.id }
                        if (hasChildren) {
                            val isCollapsed = collapsedSkillIds.contains(skill.id)
                            val badgeText = if (isCollapsed) "⊕" else "⊖"
                            val badgeColor = if (isCollapsed) LegendaryGold else SystemGreen.copy(alpha = 0.8f)

                            val badgeLayout = textMeasurer.measure(
                                text = AnnotatedString(badgeText),
                                style = TextStyle(
                                    fontFamily = JetBrainsMono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (14 * scale * currentScale).coerceIn(6f, 20f).sp,
                                    color = badgeColor
                                )
                            )
                            drawText(
                                textLayoutResult = badgeLayout,
                                topLeft = Offset(
                                    nodeCenter.x + nodeRadius - (6.dp.toPx() * scale),
                                    nodeCenter.y - nodeRadius - (8.dp.toPx() * scale)
                                )
                            )
                        }

                        // XP progress bar below node (only at scale > 1.0x)
                        if (skill.isUnlocked && scale > 1.0f) {
                            val xpProgressWidth = (nodeRadius * 1.5f) * skill.rankProgressPercent
                            val barHeight = 4.0.dp.toPx() * scale
                            val barY = nodeCenter.y + nodeRadius + (6.0.dp.toPx() * scale)
                            val barX = nodeCenter.x - (nodeRadius * 0.75f)

                            drawRect(
                                color = BorderFaint,
                                topLeft = Offset(barX, barY),
                                size = Size(nodeRadius * 1.5f, barHeight)
                            )
                            drawRect(
                                color = rarityColor,
                                topLeft = Offset(barX, barY),
                                size = Size(xpProgressWidth, barHeight)
                            )
                        }

                        // Skill name label
                        val textAlpha = ((scale - 0.35f) / 0.25f).coerceIn(0f, 1f)
                        val labelColor = (if (skill.isUnlocked) TextPrimary else TextDim).copy(alpha = textAlpha)

                        if (textAlpha > 0.05f) {
                            val labelLayout = textMeasurer.measure(
                                text = AnnotatedString(com.axiom.app.ui.LocalizationUtils.getLocalizedSkillName(skill.name, context)),
                                style = TextStyle(
                                    fontFamily = Inter,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (11f * scale).coerceIn(4f, 15f).sp,
                                    color = labelColor,
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 2,
                                softWrap = true,
                                constraints = Constraints(
                                    maxWidth = (110.dp.toPx() * scale).toInt().coerceAtLeast((24.dp.toPx() * scale).toInt().coerceAtLeast(10))
                                )
                            )

                            drawText(
                                textLayoutResult = labelLayout,
                                topLeft = Offset(
                                    nodeCenter.x - (labelLayout.size.width / 2f),
                                    nodeCenter.y + nodeRadius + (16.0.dp.toPx() * scale)
                                )
                            )
                        }
                    }
                }
            }

            // 5. DRAW CENTRAL HUNTER HUB GLYPH
            val hubRadius = 30.0.dp.toPx() * scale
            val hubHex = Path().apply {
                for (j in 0 until 6) {
                    val angleRad = Math.toRadians((60.0 * j) - 30.0)
                    val x = (offset.x + hubRadius * cos(angleRad)).toFloat()
                    val y = (offset.y + hubRadius * sin(angleRad)).toFloat()
                    if (j == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            drawPath(hubHex, themeVoidBlack)
            drawPath(hubHex, BorderFaint, style = Stroke(width = 1.5.dp.toPx() * scale))

            val centerHubLayout = textMeasurer.measure(
                text = AnnotatedString("⚔"),
                style = TextStyle(
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = (20 * scale).coerceIn(8f, 32f).sp,
                    color = LegendaryGold
                )
            )

            drawText(
                textLayoutResult = centerHubLayout,
                topLeft = Offset(
                    offset.x - (centerHubLayout.size.width / 2f),
                    offset.y - (centerHubLayout.size.height / 2f)
                )
            )

            // 6. DRAW NEWLY UNLOCKED PARTICLE BURSTS
            val now = System.currentTimeMillis()
            activeBursts.forEach { burst ->
                val elapsed = now - burst.startTime
                if (elapsed in 0..1000L) {
                    val progress = elapsed / 1000f
                    val alpha = (1f - (elapsed / 800f)).coerceIn(0f, 1f)
                    
                    val burstCenter = Offset(
                        offset.x + (burst.x * densityPx * scale),
                        offset.y + (burst.y * densityPx * scale)
                    )

                    for (p in 0 until 12) {
                        val angleRad = (p * (360f / 12f)) * (Math.PI / 180f)
                        val maxTravel = 50.dp.toPx() * scale
                        val travelDist = progress * maxTravel

                        val px = burstCenter.x + (travelDist * cos(angleRad)).toFloat()
                        val py = burstCenter.y + (travelDist * sin(angleRad)).toFloat()

                        drawCircle(
                            color = SystemGlint.copy(alpha = alpha),
                            radius = 3.dp.toPx() * scale,
                            center = Offset(px, py)
                        )
                    }
                }
            }
        }

        // 7. MINI-MAP
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .width(110.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(LocalAxiomColors.current.voidBlack.copy(alpha = 0.88f))
                .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                .pointerInput(skillsList, computedBranchPositions) {
                    detectTapGestures { tapLoc ->
                        val percentX = tapLoc.x / 110.0.dp.toPx()
                        val percentY = tapLoc.y / 70.0.dp.toPx()

                        val targetModelX = (percentX - 0.5f) * 600f * density.density
                        val targetModelY = (percentY - 0.5f) * 600f * density.density

                        coroutineScope.launch {
                            offsetAnim.animateTo(
                                Offset(
                                    layoutWidth / 2f - targetModelX * scaleAnim.value,
                                    layoutHeight / 2f - targetModelY * scaleAnim.value
                                ),
                                spring(stiffness = Spring.StiffnessMediumLow)
                            )
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val mapSize = size

                val gridSpacing = 15.dp.toPx()
                for (x in 0 until (mapSize.width / gridSpacing).toInt() + 1) {
                    val xPx = x * gridSpacing
                    drawLine(
                        color = BorderFaint.copy(alpha = 0.05f),
                        start = Offset(xPx, 0f),
                        end = Offset(xPx, mapSize.height),
                        strokeWidth = 0.5.dp.toPx()
                    )
                }
                for (y in 0 until (mapSize.height / gridSpacing).toInt() + 1) {
                    val yPx = y * gridSpacing
                    drawLine(
                        color = BorderFaint.copy(alpha = 0.05f),
                        start = Offset(0f, yPx),
                        end = Offset(mapSize.width, yPx),
                        strokeWidth = 0.5.dp.toPx()
                    )
                }

                skillsList.forEach { skill ->
                    if (isSkillHidden(skill.id)) return@forEach
                    val modelPos = computedBranchPositions[skill.id]
                    if (modelPos != null) {
                        val px = mapSize.width / 2f + (modelPos.x * densityPx / (600f * densityPx)) * mapSize.width
                        val py = mapSize.height / 2f + (modelPos.y * densityPx / (600f * densityPx)) * mapSize.height

                        val dotColor = if (skill.isUnlocked) {
                            Color(skill.rankColor.toInt())
                        } else {
                            BorderFaint.copy(alpha = 0.4f)
                        }

                        drawCircle(
                            color = dotColor,
                            radius = 2.dp.toPx(),
                            center = Offset(px, py)
                        )
                    }
                }

                // Aspect ratio viewport bounding box inside the Mini-Map
                val aspectViewportWidth = (mapSize.width * (size.width / (600f * densityPx * scaleAnim.value))).coerceIn(8f, mapSize.width)
                val aspectViewportHeight = (mapSize.height * (size.height / (600f * densityPx * scaleAnim.value))).coerceIn(8f, mapSize.height)

                val rectLeft = (mapSize.width / 2f - (offsetAnim.value.x - size.width / 2f) / (600f * densityPx * scaleAnim.value) * mapSize.width) - aspectViewportWidth / 2f
                val rectTop = (mapSize.height / 2f - (offsetAnim.value.y - size.height / 2f) / (600f * densityPx * scaleAnim.value) * mapSize.height) - aspectViewportHeight / 2f

                drawRect(
                    color = SystemGlint.copy(alpha = 0.45f),
                    topLeft = Offset(rectLeft, rectTop),
                    size = Size(aspectViewportWidth.coerceAtLeast(8f), aspectViewportHeight.coerceAtLeast(8f)),
                    style = Stroke(width = 1.0.dp.toPx())
                )
            }
        }
    }
}

private fun getBezierPosition(p0: Offset, p1: Offset, p2: Offset, p3: Offset, t: Float): Offset {
    val u = 1f - t
    val tt = t * t
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * t
    
    val x = uuu * p0.x + 3f * uu * t * p1.x + 3f * u * tt * p2.x + ttt * p3.x
    val y = uuu * p0.y + 3f * uu * t * p1.y + 3f * u * tt * p2.y + ttt * p3.y
    return Offset(x, y)
}

private fun normalizeCategoryName(
    categoryName: String,
    programmingLabel: String,
    businessLabel: String,
    knowledgeLabel: String,
    healthLabel: String,
    creativityLabel: String
): String {
    val norm = categoryName.lowercase().trim()
    return when {
        norm == "mind" || norm == "intellect" || norm == "programming" || norm == "software engineering" || norm == "engineering" || norm == programmingLabel.lowercase().trim() -> programmingLabel
        norm == "business" || norm == "finance" || norm == "entrepreneurship" || norm == "marketing" || norm == "network" || norm == businessLabel.lowercase().trim() -> businessLabel
        norm == "knowledge" || norm == "spirit" || norm == "mental fortitude" || norm == "discovery" || norm == "science" || norm == knowledgeLabel.lowercase().trim() -> knowledgeLabel
        norm == "body" || norm == "health" || norm == "physical conditioning" || norm == "fitness" || norm == healthLabel.lowercase().trim() -> healthLabel
        norm == "creativity" || norm == "design" || norm == "arts" || norm == creativityLabel.lowercase().trim() -> creativityLabel
        else -> categoryName.trim()
    }
}
