package com.axiom.app.presentation.bodymap

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class BodyMapGeometryTest {
    @Test
    fun viewportTransform_usesReferenceCalibratedVerticalScale() {
        assertEquals(0.811455f, BodyMapGeometry.BODY_Y_SCALE, 0.000001f)
    }

    @Test
    fun viewportTransform_rejectsNonPositiveDimensions() {
        assertThrows(IllegalArgumentException::class.java) {
            BodyMapGeometry.transformFor(viewportWidth = 0f, viewportHeight = 568f)
        }
        assertThrows(IllegalArgumentException::class.java) {
            BodyMapGeometry.transformFor(viewportWidth = 240f, viewportHeight = -1f)
        }
    }

    @Test
    fun viewportTransform_rejectsNonFiniteDimensions() {
        assertThrows(IllegalArgumentException::class.java) {
            BodyMapGeometry.transformFor(viewportWidth = Float.NaN, viewportHeight = 568f)
        }
        assertThrows(IllegalArgumentException::class.java) {
            BodyMapGeometry.transformFor(viewportWidth = 240f, viewportHeight = Float.POSITIVE_INFINITY)
        }
    }

    @Test
    fun viewportTransform_roundTripsAtlasPointInPortraitViewport() {
        val source = Offset(182f, 470f)
        val transform = BodyMapGeometry.transformFor(viewportWidth = 360f, viewportHeight = 720f)

        val roundTrip = BodyMapGeometry.viewportToAtlas(
            BodyMapGeometry.atlasToViewport(source, transform),
            transform
        )

        assertEquals(source.x, roundTrip.x, 0.001f)
        assertEquals(source.y, roundTrip.y, 0.001f)
    }

    @Test
    fun viewportTransform_roundTripsAtlasPointInSquareViewport() {
        val source = Offset(58f, 122f)
        val transform = BodyMapGeometry.transformFor(viewportWidth = 600f, viewportHeight = 600f)

        val roundTrip = BodyMapGeometry.viewportToAtlas(
            BodyMapGeometry.atlasToViewport(source, transform),
            transform
        )

        assertEquals(source.x, roundTrip.x, 0.001f)
        assertEquals(source.y, roundTrip.y, 0.001f)
    }

    @Test
    fun artworkTransform_roundTripsAtlasPointThroughTrimmedBitmap() {
        val source = Offset(92f, 137f)
        val transform = BodyMapGeometry.artworkTransformFor(
            viewportWidth = 204f,
            viewportHeight = 500f,
            sourceWidth = 195f,
            sourceHeight = 345f,
            topTrim = 11f
        )

        val roundTrip = BodyMapGeometry.artworkViewportToAtlas(
            BodyMapGeometry.atlasToArtworkViewport(source, transform),
            transform
        )

        assertEquals(source.x, roundTrip.x, 0.001f)
        assertEquals(source.y, roundTrip.y, 0.001f)
    }

    @Test
    fun artworkTransform_topCentersFitContentWithoutVerticalLetterboxing() {
        val transform = BodyMapGeometry.artworkTransformFor(
            viewportWidth = 204f,
            viewportHeight = 500f,
            sourceWidth = 195f,
            sourceHeight = 345f,
            topTrim = 11f
        )

        assertEquals(204f / 195f, transform.scale, 0.001f)
        assertEquals(0f, transform.offsetX, 0.001f)
        assertEquals(0f, transform.offsetY, 0.001f)
    }

    @Test
    fun artworkTransform_roundTripsHorizontallyCalibratedArtwork() {
        val source = Offset(38f, 180f)
        val transform = BodyMapGeometry.artworkTransformFor(
            viewportWidth = 204f,
            viewportHeight = 500f,
            sourceWidth = 195f,
            sourceHeight = 345f,
            topTrim = 11f,
            horizontalScale = 0.950f
        )

        val viewport = BodyMapGeometry.atlasToArtworkViewport(source, transform)
        val roundTrip = BodyMapGeometry.artworkViewportToAtlas(viewport, transform)

        assertEquals(5.1f, transform.offsetX, 0.001f)
        assertEquals(source.x, roundTrip.x, 0.001f)
        assertEquals(source.y, roundTrip.y, 0.001f)
    }

    @Test
    fun projectHotspot_placesMaleShouldersFartherFromCenterline() {
        val hotspot = BodyMapHotspot(muscleId = "shoulders", x = 58f, y = 122f)

        val male = BodyMapGeometry.projectHotspot(hotspot, BodyMapSex.Male)
        val female = BodyMapGeometry.projectHotspot(hotspot, BodyMapSex.Female)

        assertTrue(abs(male.x - 120f) > abs(female.x - 120f))
        assertEquals(hotspot.y, male.y, 0f)
        assertEquals(134.04f, female.y, 0.001f)
    }

    @Test
    fun projectHotspot_placesFemaleLegsCloserToCenterline() {
        val hotspot = BodyMapHotspot(muscleId = "legs", x = 87f, y = 350f)

        val male = BodyMapGeometry.projectHotspot(hotspot, BodyMapSex.Male)
        val female = BodyMapGeometry.projectHotspot(hotspot, BodyMapSex.Female)

        assertTrue(abs(female.x - 120f) < abs(male.x - 120f))
        assertEquals(hotspot.y, male.y, 0f)
        assertEquals(355.656f, female.y, 0.001f)
    }

    @Test
    fun projectHotspot_keepsSolePivotFixedForFemale() {
        val sole = BodyMapHotspot(muscleId = "legs", x = 120f, y = 552f)

        val projected = BodyMapGeometry.projectHotspot(sole, BodyMapSex.Female)

        assertEquals(552f, projected.y, 0.001f)
    }

    @Test
    fun artworkHitTest_usesTheSameFemaleProjectionAsTheVisibleHotspots() {
        val hotspots = BodyMapGeometry.artworkHotspotsFor(BodyMapView.Front, BodyMapSex.Female)
        val shoulder = hotspots.first { it.muscleId == "shoulders" }

        val resolved = BodyMapGeometry.resolveMuscleAt(Offset(shoulder.x, shoulder.y), hotspots)

        assertEquals(74.616f, shoulder.x, 0.001f)
        assertEquals(134.04f, shoulder.y, 0.001f)
        assertEquals("shoulders", resolved)
    }

    @Test
    fun artworkHitTest_resolvesEveryRepresentativeRegionForBothSexes() {
        BodyMapSex.entries.forEach { sex ->
            val hotspots = BodyMapGeometry.artworkHotspotsFor(BodyMapView.Front, sex)
            hotspots.distinctBy { it.muscleId }.forEach { hotspot ->
                assertEquals(
                    "${sex.name} ${hotspot.muscleId}",
                    hotspot.muscleId,
                    BodyMapGeometry.resolveMuscleAt(
                        point = Offset(hotspot.x, hotspot.y),
                        hotspots = hotspots
                    )
                )
            }
        }
    }
}
