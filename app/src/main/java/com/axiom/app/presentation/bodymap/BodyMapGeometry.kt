package com.axiom.app.presentation.bodymap

import androidx.compose.ui.geometry.Offset
import kotlin.math.min

data class BodyMapTransform(
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float,
    val bodyYScale: Float
)

data class BodyMapArtworkTransform(
    val scale: Float,
    val horizontalScale: Float,
    val offsetX: Float,
    val offsetY: Float,
    val sourceWidth: Float,
    val sourceHeight: Float,
    val topTrim: Float
)

object BodyMapGeometry {
    private const val AtlasWidth = 240f
    private const val AtlasHeight = 568f
    const val BODY_Y_SCALE = 0.811455f
    const val SOLE_Y = 552f
    private const val DrawHeight = AtlasHeight * BODY_Y_SCALE
    private const val CenterX = AtlasWidth / 2f

    fun transformFor(viewportWidth: Float, viewportHeight: Float): BodyMapTransform {
        require(viewportWidth.isFinite() && viewportWidth > 0f) {
            "viewportWidth must be finite and positive"
        }
        require(viewportHeight.isFinite() && viewportHeight > 0f) {
            "viewportHeight must be finite and positive"
        }
        val scale = min(viewportWidth * 0.98f / AtlasWidth, viewportHeight * 0.98f / DrawHeight)
        return BodyMapTransform(
            scale = scale,
            offsetX = (viewportWidth - AtlasWidth * scale) / 2f,
            offsetY = (viewportHeight - DrawHeight * scale) / 2f,
            bodyYScale = BODY_Y_SCALE
        )
    }

    fun atlasToViewport(point: Offset, transform: BodyMapTransform): Offset =
        Offset(
            x = transform.offsetX + transform.scale * point.x,
            y = transform.offsetY + transform.scale * transform.bodyYScale * point.y
        )

    fun viewportToAtlas(point: Offset, transform: BodyMapTransform): Offset =
        Offset(
            x = (point.x - transform.offsetX) / transform.scale,
            y = (point.y - transform.offsetY) / (transform.scale * transform.bodyYScale)
        )

    fun artworkTransformFor(
        viewportWidth: Float,
        viewportHeight: Float,
        sourceWidth: Float,
        sourceHeight: Float,
        topTrim: Float,
        horizontalScale: Float = 1f
    ): BodyMapArtworkTransform {
        require(viewportWidth.isFinite() && viewportWidth > 0f) {
            "viewportWidth must be finite and positive"
        }
        require(viewportHeight.isFinite() && viewportHeight > 0f) {
            "viewportHeight must be finite and positive"
        }
        require(sourceWidth.isFinite() && sourceWidth > 0f) {
            "sourceWidth must be finite and positive"
        }
        require(sourceHeight.isFinite() && sourceHeight > 0f) {
            "sourceHeight must be finite and positive"
        }
        require(topTrim.isFinite() && topTrim >= 0f && topTrim < sourceHeight) {
            "topTrim must be finite and within the source image"
        }
        require(horizontalScale.isFinite() && horizontalScale > 0f) {
            "horizontalScale must be finite and positive"
        }

        val visibleHeight = sourceHeight - topTrim
        val scale = min(viewportWidth / sourceWidth, viewportHeight / visibleHeight)
        val displayedWidth = sourceWidth * scale * horizontalScale
        return BodyMapArtworkTransform(
            scale = scale,
            horizontalScale = horizontalScale,
            offsetX = (viewportWidth - displayedWidth) / 2f,
            offsetY = 0f,
            sourceWidth = sourceWidth,
            sourceHeight = sourceHeight,
            topTrim = topTrim
        )
    }

    fun atlasToArtworkViewport(point: Offset, transform: BodyMapArtworkTransform): Offset =
        Offset(
            x = transform.offsetX +
                point.x / AtlasWidth * transform.sourceWidth * transform.scale * transform.horizontalScale,
            y = transform.offsetY +
                (point.y / AtlasHeight * transform.sourceHeight - transform.topTrim) * transform.scale
        )

    fun artworkViewportToAtlas(point: Offset, transform: BodyMapArtworkTransform): Offset =
        Offset(
            x = (point.x - transform.offsetX) /
                (transform.scale * transform.horizontalScale) / transform.sourceWidth * AtlasWidth,
            y = ((point.y - transform.offsetY) / transform.scale + transform.topTrim) /
                transform.sourceHeight * AtlasHeight
        )

    fun projectHotspot(hotspot: BodyMapHotspot, sex: BodyMapSex): BodyMapHotspot =
        projectHotspot(hotspot, BodyMapAtlasModel.profileFor(sex))

    fun artworkHotspotFor(hotspot: BodyMapHotspot, sex: BodyMapSex): BodyMapHotspot =
        if (sex == BodyMapSex.Female) projectHotspot(hotspot, sex) else hotspot

    fun artworkHotspotsFor(view: BodyMapView, sex: BodyMapSex): List<BodyMapHotspot> =
        BodyMapAtlasModel.hotspotsFor(view).map { artworkHotspotFor(it, sex) }

    fun resolveMuscleAt(
        point: Offset,
        hotspots: List<BodyMapHotspot>,
        maximumDistance: Float = 46f
    ): String? {
        require(maximumDistance.isFinite() && maximumDistance > 0f) {
            "maximumDistance must be finite and positive"
        }
        val maximumDistanceSquared = maximumDistance * maximumDistance
        return hotspots
            .asSequence()
            .map { hotspot ->
                val dx = point.x - hotspot.x
                val dy = point.y - hotspot.y
                hotspot to (dx * dx + dy * dy)
            }
            .filter { (_, distanceSquared) -> distanceSquared < maximumDistanceSquared }
            .minByOrNull { (_, distanceSquared) -> distanceSquared }
            ?.first
            ?.muscleId
    }

    fun projectHotspot(hotspot: BodyMapHotspot, profile: BodyMapAnatomyProfile): BodyMapHotspot =
        hotspot.copy(
            x = CenterX + (hotspot.x - CenterX) * horizontalScaleFor(hotspot.muscleId, profile),
            y = SOLE_Y + (hotspot.y - SOLE_Y) * profile.heightSpan
        )

    fun horizontalScaleFor(muscleId: String, profile: BodyMapAnatomyProfile): Float =
        when (muscleId) {
            "shoulders" -> profile.shoulderSpan
            "chest", "back" -> profile.chestSpan
            "biceps", "triceps", "forearms" -> profile.armSpan
            "core" -> profile.waistSpan
            "legs" -> profile.legHorizontalSpan
            else -> 1f
        }
}
