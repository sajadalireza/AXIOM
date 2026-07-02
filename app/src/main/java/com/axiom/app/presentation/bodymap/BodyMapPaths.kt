package com.axiom.app.presentation.bodymap

import androidx.compose.ui.graphics.Path

object BodyMapPaths {
    // Helper to build from points
    private fun buildPath(vararg points: Float): Path {
        val path = Path()
        if (points.size >= 2) {
            path.moveTo(points[0], points[1])
            for (i in 2 until points.size step 2) {
                if (i + 1 < points.size) {
                    path.lineTo(points[i], points[i + 1])
                }
            }
            path.close()
        }
        return path
    }

    // 200 x 400 canvas coordinate system
    val chest: Path = Path().apply {
        // Left chest
        addPath(buildPath(100f, 75f, 72f, 75f, 72f, 105f, 100f, 105f))
        // Right chest
        addPath(buildPath(100f, 75f, 128f, 75f, 128f, 105f, 100f, 105f))
    }

    val shoulders: Path = Path().apply {
        // Left shoulder
        addPath(buildPath(70f, 70f, 50f, 75f, 60f, 95f, 75f, 90f))
        // Right shoulder
        addPath(buildPath(130f, 70f, 150f, 75f, 140f, 95f, 125f, 90f))
    }

    val biceps: Path = Path().apply {
        // Left bicep
        addPath(buildPath(50f, 95f, 35f, 110f, 45f, 130f, 60f, 115f))
        // Right bicep
        addPath(buildPath(150f, 95f, 165f, 110f, 155f, 130f, 140f, 115f))
    }

    val triceps: Path = Path().apply {
        // Left tricep
        addPath(buildPath(50f, 95f, 35f, 110f, 45f, 130f, 60f, 115f))
        // Right tricep
        addPath(buildPath(150f, 95f, 165f, 110f, 155f, 130f, 140f, 115f))
    }

    val forearms: Path = Path().apply {
        // Left forearm
        addPath(buildPath(45f, 130f, 30f, 170f, 40f, 180f, 55f, 140f))
        // Right forearm
        addPath(buildPath(155f, 130f, 170f, 170f, 160f, 180f, 145f, 140f))
    }

    val back: Path = Path().apply {
        // Upper back / traps
        addPath(buildPath(75f, 70f, 100f, 58f, 125f, 70f, 120f, 95f, 80f, 95f))
        // Lats Left
        addPath(buildPath(75f, 95f, 65f, 140f, 85f, 140f, 80f, 95f))
        // Lats Right
        addPath(buildPath(125f, 95f, 135f, 140f, 115f, 140f, 120f, 95f))
    }

    val core: Path = Path().apply {
        addPath(buildPath(80f, 110f, 120f, 110f, 115f, 170f, 85f, 170f))
    }

    val legs: Path = Path().apply {
        // Front/Back quads/hamstrings left
        addPath(buildPath(80f, 175f, 65f, 270f, 85f, 270f, 95f, 175f))
        // Front/Back quads/hamstrings right
        addPath(buildPath(120f, 175f, 135f, 270f, 115f, 270f, 105f, 175f))
        // Calves left
        addPath(buildPath(65f, 280f, 55f, 360f, 75f, 360f, 85f, 280f))
        // Calves right
        addPath(buildPath(135f, 280f, 145f, 360f, 125f, 360f, 115f, 280f))
    }

    // Dynamic aliases for left/right arms as asked by prompt
    val leftArm: Path = biceps
    val rightArm: Path = triceps
}
