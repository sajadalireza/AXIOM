package com.axiom.app.presentation.bodymap

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

/**
 * Anatomically-shaped muscle group silhouettes for the body scanner, front + back.
 * Coordinate space: 240 wide x 640 tall, centerline x = 120. Every path is built
 * as a left-side shape mirrored across the centerline for the right side, using
 * cubic beziers for organic muscle contours instead of straight-edge polygons.
 */
object BodyMapPaths {
    const val CANVAS_WIDTH = 240f
    const val CANVAS_HEIGHT = 640f
    private const val CX = 120f

    private fun mirror(x: Float) = 2 * CX - x

    /** Mirrors a left-side path to build the matching right-side path. */
    private fun Path.addMirrored(build: Path.() -> Unit) {
        val temp = Path().apply(build)
        val matrix = androidx.compose.ui.graphics.Matrix().apply {
            scale(x = -1f, y = 1f)
            translate(x = -2 * CX)
        }
        temp.transform(matrix)
        addPath(temp)
    }

    // ───────────────────────────── FRONT VIEW ─────────────────────────────

    private val maleHead: Path = Path().apply {
        moveTo(108f, 5f)
        cubicTo(115f, 5f, 125f, 5f, 132f, 8f)
        cubicTo(139f, 12f, 142f, 20f, 143.303f, 28f)
        lineTo(143.303f, 36f)
        cubicTo(142f, 48f, 137f, 57f, 130f, 64f)
        cubicTo(126f, 67.5f, 122f, 70.093f, 120f, 70.093f)
        cubicTo(118f, 70.093f, 114f, 67.5f, 110f, 64f)
        cubicTo(103f, 57f, 98f, 48f, 96.697f, 36f)
        lineTo(96.697f, 28f)
        cubicTo(98f, 20f, 101f, 12f, 108f, 5f)
        close()
    }

    private val femaleHead: Path = Path().apply {
        moveTo(109f, 5f)
        cubicTo(115f, 5f, 125f, 5f, 131f, 8f)
        cubicTo(137f, 12f, 140f, 20f, 141.572f, 29f)
        lineTo(141.572f, 37f)
        cubicTo(140f, 49f, 136f, 58f, 129f, 65f)
        cubicTo(125f, 69f, 122f, 72.281f, 120f, 72.281f)
        cubicTo(118f, 72.281f, 115f, 69f, 111f, 65f)
        cubicTo(104f, 58f, 100f, 49f, 98.428f, 37f)
        lineTo(98.428f, 29f)
        cubicTo(100f, 20f, 103f, 12f, 109f, 5f)
        close()
    }

    private val maleNeck: Path = Path().apply {
        moveTo(104.908f, 60.980f)
        cubicTo(104f, 75f, 102f, 89f, 98.441f, 101.272f)
        lineTo(141.559f, 101.272f)
        cubicTo(138f, 89f, 136f, 75f, 135.092f, 60.980f)
        cubicTo(130f, 66f, 125f, 70f, 120f, 72f)
        cubicTo(115f, 70f, 110f, 66f, 104.908f, 60.980f)
        close()
    }

    private val femaleNeck: Path = Path().apply {
        moveTo(107.919f, 62.862f)
        cubicTo(107f, 77f, 105f, 91f, 102.742f, 104.007f)
        lineTo(137.258f, 104.007f)
        cubicTo(135f, 91f, 133f, 77f, 132.081f, 62.862f)
        cubicTo(128f, 68f, 124f, 72f, 120f, 74f)
        cubicTo(116f, 72f, 112f, 68f, 107.919f, 62.862f)
        close()
    }

    val head: Path = maleHead
    val neck: Path = maleNeck

    fun headFor(sex: BodyMapSex): Path =
        if (sex == BodyMapSex.Male) maleHead else femaleHead

    fun neckFor(sex: BodyMapSex): Path =
        if (sex == BodyMapSex.Male) maleNeck else femaleNeck

    val femaleHair: Path = Path().apply {
        moveTo(112f, 5f)
        cubicTo(102f, 5f, 94f, 11f, 91f, 21f)
        cubicTo(89f, 33f, 86f, 44f, 82f, 52f)
        cubicTo(79f, 59f, 76f, 65f, 74f, 67f)
        lineTo(74f, 70f)
        cubicTo(84f, 70f, 95f, 66f, 102f, 58f)
        cubicTo(109f, 50f, 111f, 39f, 108f, 28f)
        cubicTo(106f, 19f, 103f, 12f, 101f, 9f)
        cubicTo(105f, 7f, 109f, 5f, 112f, 5f)
        close()

        moveTo(109f, 5f)
        cubicTo(123f, 5f, 136f, 11f, 141f, 21f)
        lineTo(144f, 30f)
        lineTo(144f, 39f)
        cubicTo(143f, 47f, 140f, 53f, 136f, 58f)
        cubicTo(139f, 45f, 138f, 31f, 132f, 20f)
        cubicTo(127f, 11f, 118f, 7f, 109f, 5f)
        close()
    }

    val upperTorsoShell: Path = Path().apply {
        moveTo(108f, 88f)
        lineTo(92f, 91f)
        cubicTo(82f, 93f, 73f, 97f, 66f, 103f)
        lineTo(76f, 111f)
        lineTo(120f, 99f)
        close()
        addMirrored {
            moveTo(108f, 88f)
            lineTo(92f, 91f)
            cubicTo(82f, 93f, 73f, 97f, 66f, 103f)
            lineTo(76f, 111f)
            lineTo(120f, 99f)
            close()
        }
    }

    val pelvis: Path = Path().apply {
        // Outer iliac wing.
        moveTo(92f, 258f)
        lineTo(80f, 269f)
        lineTo(76f, 300f)
        lineTo(87f, 286f)
        lineTo(100f, 263f)
        close()
        // Upper inguinal band, meeting the abdominal point at center.
        moveTo(96f, 259f)
        lineTo(120f, 290f)
        lineTo(112f, 302f)
        lineTo(90f, 276f)
        close()
        // Lower inguinal band: the long V that bridges the pelvis into the thigh.
        moveTo(112f, 302f)
        lineTo(120f, 290f)
        lineTo(108f, 352f)
        lineTo(98f, 338f)
        close()
        addMirrored {
            moveTo(92f, 258f)
            lineTo(80f, 269f)
            lineTo(76f, 300f)
            lineTo(87f, 286f)
            lineTo(100f, 263f)
            close()
            moveTo(96f, 259f)
            lineTo(120f, 290f)
            lineTo(112f, 302f)
            lineTo(90f, 276f)
            close()
            moveTo(112f, 302f)
            lineTo(120f, 290f)
            lineTo(108f, 352f)
            lineTo(98f, 338f)
            close()
        }
    }

    val shoulders: Path = Path().apply {
        moveTo(76f, 98f)
        cubicTo(68f, 96f, 59f, 99f, 52f, 106f)
        cubicTo(47f, 114f, 45f, 126f, 47f, 138f)
        cubicTo(48f, 145f, 51f, 150f, 55f, 151f)
        cubicTo(58f, 143f, 61f, 134f, 64f, 124f)
        cubicTo(68f, 113f, 72f, 104f, 76f, 98f)
        close()
        addMirrored {
            moveTo(76f, 98f)
            cubicTo(68f, 96f, 59f, 99f, 52f, 106f)
            cubicTo(47f, 114f, 45f, 126f, 47f, 138f)
            cubicTo(48f, 145f, 51f, 150f, 55f, 151f)
            cubicTo(58f, 143f, 61f, 134f, 64f, 124f)
            cubicTo(68f, 113f, 72f, 104f, 76f, 98f)
            close()
        }
    }

    val chest: Path = Path().apply {
        moveTo(120f, 102f)
        lineTo(98f, 99f)
        lineTo(77f, 108f)
        lineTo(65f, 123f)
        cubicTo(67f, 139f, 75f, 150f, 88f, 157f)
        cubicTo(101f, 161f, 112f, 155f, 120f, 148f)
        lineTo(120f, 102f)
        close()
        addMirrored {
            moveTo(120f, 102f)
            lineTo(98f, 99f)
            lineTo(77f, 108f)
            lineTo(65f, 123f)
            cubicTo(67f, 139f, 75f, 150f, 88f, 157f)
            cubicTo(101f, 161f, 112f, 155f, 120f, 148f)
            lineTo(120f, 102f)
            close()
        }
    }

    val biceps: Path = Path().apply {
        moveTo(62f, 139f)
        cubicTo(55f, 145f, 48f, 155f, 43f, 166f)
        cubicTo(39f, 178f, 39f, 193f, 43f, 207f)
        cubicTo(47f, 214f, 53f, 218f, 58f, 213f)
        cubicTo(63f, 201f, 67f, 184f, 67f, 169f)
        cubicTo(69f, 157f, 68f, 146f, 62f, 139f)
        close()
        addMirrored {
            moveTo(62f, 139f)
            cubicTo(55f, 145f, 48f, 155f, 43f, 166f)
            cubicTo(39f, 178f, 39f, 193f, 43f, 207f)
            cubicTo(47f, 214f, 53f, 218f, 58f, 213f)
            cubicTo(63f, 201f, 67f, 184f, 67f, 169f)
            cubicTo(69f, 157f, 68f, 146f, 62f, 139f)
            close()
        }
    }

    val forearms: Path = Path().apply {
        moveTo(43f, 203f)
        cubicTo(37f, 217f, 28f, 233f, 20f, 249f)
        cubicTo(16f, 264f, 16f, 278f, 20f, 289f)
        cubicTo(24f, 294f, 31f, 293f, 36f, 288f)
        cubicTo(42f, 276f, 48f, 261f, 52f, 246f)
        cubicTo(56f, 232f, 58f, 218f, 58f, 207f)
        cubicTo(53f, 211f, 47f, 209f, 43f, 203f)
        close()
        addMirrored {
            moveTo(43f, 203f)
            cubicTo(37f, 217f, 28f, 233f, 20f, 249f)
            cubicTo(16f, 264f, 16f, 278f, 20f, 289f)
            cubicTo(24f, 294f, 31f, 293f, 36f, 288f)
            cubicTo(42f, 276f, 48f, 261f, 52f, 246f)
            cubicTo(56f, 232f, 58f, 218f, 58f, 207f)
            cubicTo(53f, 211f, 47f, 209f, 43f, 203f)
            close()
        }
    }

    /** Three serratus/rib plates tucked below each pectoral. */
    val coreRibs: Path = Path().apply {
        moveTo(70f, 166f)
        lineTo(93f, 172f)
        lineTo(95f, 190f)
        lineTo(78f, 185f)
        close()
        moveTo(75f, 194f)
        lineTo(95f, 198f)
        lineTo(96f, 215f)
        lineTo(80f, 210f)
        close()
        moveTo(79f, 218f)
        lineTo(97f, 221f)
        lineTo(97f, 239f)
        lineTo(84f, 232f)
        close()
        addMirrored {
            moveTo(70f, 166f)
            lineTo(93f, 172f)
            lineTo(95f, 190f)
            lineTo(78f, 185f)
            close()
            moveTo(75f, 194f)
            lineTo(95f, 198f)
            lineTo(96f, 215f)
            lineTo(80f, 210f)
            close()
            moveTo(79f, 218f)
            lineTo(97f, 221f)
            lineTo(97f, 239f)
            lineTo(84f, 232f)
            close()
        }
    }

    /** Continuous external-oblique plane below the rib plates. */
    val coreObliques: Path = Path().apply {
        moveTo(80f, 218f)
        lineTo(97f, 222f)
        lineTo(98f, 250f)
        cubicTo(98f, 266f, 97f, 280f, 95f, 288f)
        lineTo(77f, 272f)
        cubicTo(74f, 254f, 75f, 234f, 80f, 218f)
        close()
        addMirrored {
            moveTo(80f, 218f)
            lineTo(97f, 222f)
            lineTo(98f, 250f)
            cubicTo(98f, 266f, 97f, 280f, 95f, 288f)
            lineTo(77f, 272f)
            cubicTo(74f, 254f, 75f, 234f, 80f, 218f)
            close()
        }
    }

    /** Combined side alias retained for tests and legacy callers. */
    val coreSides: Path = Path().apply {
        addPath(coreRibs)
        addPath(coreObliques)
    }

    /** Four articulated rectus-abdominis plates, selectable with the side core planes. */
    val coreAbs: Path = Path().apply {
        moveTo(91f, 171f)
        lineTo(118f, 163f)
        lineTo(118f, 190f)
        lineTo(92f, 196f)
        close()
        moveTo(92f, 199f)
        lineTo(118f, 193f)
        lineTo(118f, 216f)
        lineTo(93f, 222f)
        close()
        moveTo(94f, 225f)
        lineTo(118f, 219f)
        lineTo(118f, 243f)
        lineTo(96f, 248f)
        close()
        moveTo(96f, 252f)
        lineTo(118f, 247f)
        lineTo(118f, 290f)
        lineTo(103f, 275f)
        close()
        addMirrored {
            moveTo(91f, 171f)
            lineTo(118f, 163f)
            lineTo(118f, 190f)
            lineTo(92f, 196f)
            close()
            moveTo(92f, 199f)
            lineTo(118f, 193f)
            lineTo(118f, 216f)
            lineTo(93f, 222f)
            close()
            moveTo(94f, 225f)
            lineTo(118f, 219f)
            lineTo(118f, 243f)
            lineTo(96f, 248f)
            close()
            moveTo(96f, 252f)
            lineTo(118f, 247f)
            lineTo(118f, 290f)
            lineTo(103f, 275f)
            close()
        }
    }

    /** Combined alias retained for tests and legacy callers. */
    val core: Path = Path().apply {
        addPath(coreSides)
        addPath(coreAbs)
    }

    /** Main quadriceps plane plus the narrow lateral strip. */
    val thighPrimary: Path = Path().apply {
        moveTo(79f, 282f)
        lineTo(108f, 304f)
        cubicTo(111f, 322f, 112f, 344f, 110f, 365f)
        cubicTo(106f, 384f, 98f, 404f, 88f, 412f)
        cubicTo(79f, 408f, 72f, 399f, 68f, 386f)
        cubicTo(64f, 369f, 63f, 349f, 65f, 330f)
        cubicTo(67f, 310f, 72f, 291f, 79f, 282f)
        close()
        moveTo(70f, 303f)
        lineTo(77f, 294f)
        lineTo(71f, 386f)
        lineTo(67f, 405f)
        lineTo(64f, 363f)
        cubicTo(64f, 340f, 66f, 320f, 70f, 303f)
        close()
        addMirrored {
            moveTo(79f, 282f)
            lineTo(108f, 304f)
            cubicTo(111f, 322f, 112f, 344f, 110f, 365f)
            cubicTo(106f, 384f, 98f, 404f, 88f, 412f)
            cubicTo(79f, 408f, 72f, 399f, 68f, 386f)
            cubicTo(64f, 369f, 63f, 349f, 65f, 330f)
            cubicTo(67f, 310f, 72f, 291f, 79f, 282f)
            close()
            moveTo(70f, 303f)
            lineTo(77f, 294f)
            lineTo(71f, 386f)
            lineTo(67f, 405f)
            lineTo(64f, 363f)
            cubicTo(64f, 340f, 66f, 320f, 70f, 303f)
            close()
        }
    }

    /** Medial quadriceps/adductor plane beside the central leg gap. */
    val thighSecondary: Path = Path().apply {
        moveTo(108f, 304f)
        cubicTo(112f, 321f, 115f, 339f, 113f, 357f)
        cubicTo(111f, 379f, 103f, 399f, 90f, 411f)
        lineTo(86f, 407f)
        cubicTo(98f, 392f, 104f, 372f, 106f, 351f)
        cubicTo(108f, 332f, 108f, 315f, 108f, 304f)
        close()
        addMirrored {
            moveTo(108f, 304f)
            cubicTo(112f, 321f, 115f, 339f, 113f, 357f)
            cubicTo(111f, 379f, 103f, 399f, 90f, 411f)
            lineTo(86f, 407f)
            cubicTo(98f, 392f, 104f, 372f, 106f, 351f)
            cubicTo(108f, 332f, 108f, 315f, 108f, 304f)
            close()
        }
    }

    /** Articulated patella shield. */
    val knees: Path = Path().apply {
        moveTo(80f, 413f)
        cubicTo(84f, 409f, 91f, 409f, 95f, 413f)
        lineTo(99f, 422f)
        lineTo(96f, 432f)
        lineTo(89f, 441f)
        lineTo(81f, 436f)
        lineTo(76f, 427f)
        lineTo(79f, 416f)
        close()
        addMirrored {
            moveTo(80f, 413f)
            cubicTo(84f, 409f, 91f, 409f, 95f, 413f)
            lineTo(99f, 422f)
            lineTo(96f, 432f)
            lineTo(89f, 441f)
            lineTo(81f, 436f)
            lineTo(76f, 427f)
            lineTo(79f, 416f)
            close()
        }
    }

    /** Dark lateral calf plane, widest just below the knee. */
    val calfLateral: Path = Path().apply {
        moveTo(82f, 435f)
        lineTo(86f, 444f)
        cubicTo(85f, 462f, 84f, 487f, 86f, 512f)
        cubicTo(86f, 521f, 87f, 527f, 87f, 530f)
        lineTo(82f, 530f)
        cubicTo(80f, 522f, 77f, 510f, 75f, 493f)
        cubicTo(73f, 473f, 76f, 451f, 82f, 435f)
        close()
        addMirrored {
            moveTo(82f, 435f)
            lineTo(86f, 444f)
            cubicTo(85f, 462f, 84f, 487f, 86f, 512f)
            cubicTo(86f, 521f, 87f, 527f, 87f, 530f)
            lineTo(82f, 530f)
            cubicTo(80f, 522f, 77f, 510f, 75f, 493f)
            cubicTo(73f, 473f, 76f, 451f, 82f, 435f)
            close()
        }
    }

    /** Light anterior tibial plane running from patella to ankle. */
    val calfShin: Path = Path().apply {
        moveTo(84f, 440f)
        lineTo(96f, 440f)
        lineTo(97f, 452f)
        cubicTo(97f, 474f, 96f, 503f, 94f, 530f)
        lineTo(86f, 530f)
        cubicTo(85f, 513f, 83f, 487f, 84f, 463f)
        cubicTo(84f, 451f, 83f, 444f, 84f, 440f)
        close()
        addMirrored {
            moveTo(84f, 440f)
            lineTo(96f, 440f)
            lineTo(97f, 452f)
            cubicTo(97f, 474f, 96f, 503f, 94f, 530f)
            lineTo(86f, 530f)
            cubicTo(85f, 513f, 83f, 487f, 84f, 463f)
            cubicTo(84f, 451f, 83f, 444f, 84f, 440f)
            close()
        }
    }

    /** Narrow dark medial calf plane beside the shin. */
    val calfMedial: Path = Path().apply {
        moveTo(94f, 438f)
        lineTo(101f, 448f)
        cubicTo(102f, 468f, 100f, 490f, 95f, 511f)
        cubicTo(96f, 520f, 97f, 527f, 97f, 530f)
        lineTo(93f, 530f)
        cubicTo(92f, 503f, 94f, 470f, 94f, 438f)
        close()
        addMirrored {
            moveTo(94f, 438f)
            lineTo(101f, 448f)
            cubicTo(102f, 468f, 100f, 490f, 95f, 511f)
            cubicTo(96f, 520f, 97f, 527f, 97f, 530f)
            lineTo(93f, 530f)
            cubicTo(92f, 503f, 94f, 470f, 94f, 438f)
            close()
        }
    }

    /** Combined alias retained for hit tests and legacy callers. */
    val lowerLegs: Path = Path().apply {
        addPath(knees)
        addPath(calfLateral)
        addPath(calfShin)
        addPath(calfMedial)
    }

    /** Combined alias retained for hit tests and legacy callers. */
    val legs: Path = Path().apply {
        addPath(thighPrimary)
        addPath(thighSecondary)
        addPath(lowerLegs)
    }

    val feet: Path = Path().apply {
        moveTo(82f, 526f)
        cubicTo(79f, 533f, 77f, 543f, 78f, 550f)
        cubicTo(84f, 552f, 91f, 552f, 98f, 550f)
        cubicTo(98f, 542f, 95f, 533f, 92f, 526f)
        close()
        addMirrored {
            moveTo(82f, 526f)
            cubicTo(79f, 533f, 77f, 543f, 78f, 550f)
            cubicTo(84f, 552f, 91f, 552f, 98f, 550f)
            cubicTo(98f, 542f, 95f, 533f, 92f, 526f)
            close()
        }
    }

    // ───────────────────────────── BACK VIEW ─────────────────────────────

    /** Trapezius + rear delts, drawn together as the visible "shoulders" region from behind. */
    val backShoulders: Path = Path().apply {
        moveTo(120f, 78f)
        lineTo(84f, 100f)
        lineTo(68f, 110f)
        cubicTo(79f, 118f, 93f, 123f, 108f, 126f)
        cubicTo(115f, 112f, 119f, 95f, 120f, 78f)
        close()
        addMirrored {
            moveTo(120f, 78f)
            lineTo(84f, 100f)
            lineTo(68f, 110f)
            cubicTo(79f, 118f, 93f, 123f, 108f, 126f)
            cubicTo(115f, 112f, 119f, 95f, 120f, 78f)
            close()
        }
        moveTo(68f, 106f)
        cubicTo(56f, 103f, 46f, 110f, 41f, 122f)
        cubicTo(33f, 135f, 42f, 148f, 50f, 153f)
        cubicTo(59f, 149f, 65f, 138f, 68f, 125f)
        cubicTo(70f, 117f, 70f, 111f, 68f, 106f)
        close()
        addMirrored {
            moveTo(68f, 106f)
            cubicTo(56f, 103f, 46f, 110f, 41f, 122f)
            cubicTo(33f, 135f, 42f, 148f, 50f, 153f)
            cubicTo(59f, 149f, 65f, 138f, 68f, 125f)
            cubicTo(70f, 117f, 70f, 111f, 68f, 106f)
            close()
        }
    }

    /** Latissimus dorsi — the wide back "wings" below the traps. */
    val back: Path = Path().apply {
        moveTo(118f, 116f)
        cubicTo(100f, 118f, 84f, 127f, 74f, 141f)
        cubicTo(68f, 153f, 69f, 169f, 74f, 183f)
        cubicTo(79f, 198f, 88f, 213f, 99f, 225f)
        lineTo(111f, 237f)
        cubicTo(114f, 240f, 117f, 241f, 119f, 239f)
        lineTo(119f, 121f)
        close()
        addMirrored {
            moveTo(118f, 116f)
            cubicTo(100f, 118f, 84f, 127f, 74f, 141f)
            cubicTo(68f, 153f, 69f, 169f, 74f, 183f)
            cubicTo(79f, 198f, 88f, 213f, 99f, 225f)
            lineTo(111f, 237f)
            cubicTo(114f, 240f, 117f, 241f, 119f, 239f)
            lineTo(119f, 121f)
            close()
        }
    }

    val triceps: Path = Path().apply {
        addPath(biceps)
        addPath(forearms)
    }

    /** Lower back / erector spinae — thin strip connecting lats to glutes. */
    val lowerBack: Path = Path().apply {
        moveTo(103f, 232f)
        lineTo(118f, 238f)
        lineTo(118f, 270f)
        lineTo(100f, 269f)
        cubicTo(98f, 255f, 99f, 243f, 103f, 232f)
        close()
        addMirrored {
            moveTo(103f, 232f)
            lineTo(118f, 238f)
            lineTo(118f, 270f)
            lineTo(100f, 269f)
            cubicTo(98f, 255f, 99f, 243f, 103f, 232f)
            close()
        }
    }

    /** Glutes + hamstrings + calves (back view) as the single "legs" back-view path. */
    val backLegs: Path = Path().apply {
        // Left glute
        moveTo(92f, 264f)
        cubicTo(84f, 267f, 78f, 276f, 75f, 288f)
        cubicTo(73f, 299f, 76f, 309f, 82f, 316f)
        cubicTo(88f, 319f, 96f, 317f, 101f, 311f)
        cubicTo(104f, 300f, 104f, 284f, 100f, 268f)
        cubicTo(97f, 265f, 94f, 264f, 92f, 264f)
        close()
        // Lateral hamstring
        moveTo(78f, 318f)
        lineTo(90f, 323f)
        lineTo(87f, 375f)
        lineTo(81f, 416f)
        cubicTo(74f, 401f, 70f, 378f, 71f, 352f)
        cubicTo(71f, 339f, 74f, 327f, 78f, 318f)
        close()
        // Medial hamstring
        moveTo(89f, 323f)
        lineTo(101f, 315f)
        cubicTo(103f, 339f, 99f, 368f, 92f, 394f)
        cubicTo(89f, 404f, 84f, 412f, 79f, 416f)
        lineTo(84f, 375f)
        close()
        // Back of knee
        moveTo(80f, 410f)
        lineTo(90f, 418f)
        lineTo(97f, 431f)
        lineTo(88f, 443f)
        lineTo(77f, 428f)
        close()
        // Lateral gastrocnemius
        moveTo(79f, 416f)
        cubicTo(74f, 432f, 72f, 450f, 74f, 468f)
        cubicTo(76f, 490f, 81f, 510f, 88f, 526f)
        cubicTo(93f, 522f, 96f, 514f, 97f, 504f)
        cubicTo(99f, 480f, 98f, 454f, 94f, 431f)
        cubicTo(91f, 424f, 86f, 419f, 79f, 416f)
        close()
        // Medial calf plane
        moveTo(94f, 433f)
        lineTo(101f, 447f)
        cubicTo(102f, 469f, 100f, 491f, 96f, 510f)
        cubicTo(94f, 519f, 91f, 525f, 88f, 526f)
        cubicTo(93f, 500f, 95f, 466f, 94f, 433f)
        close()
        addMirrored {
            moveTo(92f, 264f)
            cubicTo(84f, 267f, 78f, 276f, 75f, 288f)
            cubicTo(73f, 299f, 76f, 309f, 82f, 316f)
            cubicTo(88f, 319f, 96f, 317f, 101f, 311f)
            cubicTo(104f, 300f, 104f, 284f, 100f, 268f)
            cubicTo(97f, 265f, 94f, 264f, 92f, 264f)
            close()
            moveTo(78f, 318f)
            lineTo(90f, 323f)
            lineTo(87f, 375f)
            lineTo(81f, 416f)
            cubicTo(74f, 401f, 70f, 378f, 71f, 352f)
            cubicTo(71f, 339f, 74f, 327f, 78f, 318f)
            close()
            moveTo(89f, 323f)
            lineTo(101f, 315f)
            cubicTo(103f, 339f, 99f, 368f, 92f, 394f)
            cubicTo(89f, 404f, 84f, 412f, 79f, 416f)
            lineTo(84f, 375f)
            close()
            moveTo(80f, 410f)
            lineTo(90f, 418f)
            lineTo(97f, 431f)
            lineTo(88f, 443f)
            lineTo(77f, 428f)
            close()
            moveTo(79f, 416f)
            cubicTo(74f, 432f, 72f, 450f, 74f, 468f)
            cubicTo(76f, 490f, 81f, 510f, 88f, 526f)
            cubicTo(93f, 522f, 96f, 514f, 97f, 504f)
            cubicTo(99f, 480f, 98f, 454f, 94f, 431f)
            cubicTo(91f, 424f, 86f, 419f, 79f, 416f)
            close()
            moveTo(94f, 433f)
            lineTo(101f, 447f)
            cubicTo(102f, 469f, 100f, 491f, 96f, 510f)
            cubicTo(94f, 519f, 91f, 525f, 88f, 526f)
            cubicTo(93f, 500f, 95f, 466f, 94f, 433f)
            close()
        }
    }

    // Legacy aliases kept for any other call sites.
    val leftArm: Path get() = biceps
    val rightArm: Path get() = triceps
}
