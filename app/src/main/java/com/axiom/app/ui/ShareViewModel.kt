package com.axiom.app.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.ui.theme.rankColorMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

import com.axiom.app.data.local.AxiomPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class ShareViewModel @Inject constructor(
    getHunterProfileUseCase: GetHunterProfileUseCase,
    private val preferences: AxiomPreferences
) : ViewModel() {

    val hunter: StateFlow<Hunter?> = getHunterProfileUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun shareRankCard(context: Context, bitmap: Bitmap) {
        viewModelScope.launch {
            val cachePath = File(context.cacheDir, "shared_images")
            cachePath.mkdirs()
            val file = File(cachePath, "axiom_rank_card.png")
            try {
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

            val currentStreak = preferences.streakFlow.first()
            val hunterVal = hunter.value
            val rankLabel = hunterVal?.rankLabel ?: "E-Rank"
            // TODO: append store link once published: " — warrior.app/download"
            val shareText = "⬟ I just reached $rankLabel in WARRIOR after a $currentStreak-day streak. WARRIOR PROTOCOL active."

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, shareText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(intent, "Share Rank Card")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }

    /**
     * Helper to draw a flawless, high-resolution (1080x1920) graphics card
     * representing the precise rank share screen layout for Instagram stories.
     */
    fun createHighResBitmap(hunter: Hunter): Bitmap {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Solid background (VoidBlack: 0xFF06060F)
        canvas.drawColor(0xFF06060F.toInt())

        val paint = Paint().apply {
            isAntiAlias = true
        }

        // 2. Scanline overlay (opacity 3% white lines)
        paint.color = android.graphics.Color.WHITE
        paint.alpha = 8 
        paint.strokeWidth = 2f
        var y = 0f
        while (y < height) {
            canvas.drawLine(0f, y, width.toFloat(), y, paint)
            y += 12f
        }

        // 3. System Green Targeting Reticle (corners)
        paint.color = 0xFF1D9E75.toInt() // SystemGreen
        paint.alpha = 255
        paint.strokeWidth = 6f
        paint.style = Paint.Style.STROKE
        val margin = 50f
        val len = 60f
        
        // Top-left
        canvas.drawLine(margin, margin, margin + len, margin, paint)
        canvas.drawLine(margin, margin, margin, margin + len, paint)
        // Top-right
        canvas.drawLine(width - margin, margin, width - margin - len, margin, paint)
        canvas.drawLine(width - margin, margin, width - margin, margin + len, paint)
        // Bottom-left
        canvas.drawLine(margin, height - margin, margin + len, height - margin, paint)
        canvas.drawLine(margin, height - margin, margin, height - margin - len, paint)
        // Bottom-right
        canvas.drawLine(width - margin, height - margin, width - margin - len, height - margin, paint)
        canvas.drawLine(width - margin, height - margin, width - margin, height - margin - len, paint)

        // Identify rank color Hex
        val rawColorHex = when (hunter.rankLabel.trim().uppercase()) {
            "S", "S-RANK" -> 0xFFEF9F27.toInt() // LegendaryGold
            "A", "A-RANK" -> 0xFF7F77DD.toInt() // EpicPurple
            "B", "B-RANK" -> 0xFF378ADD.toInt() // RareBlue
            "C", "C-RANK" -> 0xFF1D9E75.toInt() // SystemGreen / UncommonTeal
            else -> 0xFF8A8AA0.toInt() // CommonGray
        }

        // 4. Header configuration
        paint.style = Paint.Style.FILL
        paint.color = 0xFF1D9E75.toInt() // SystemGreen
        paint.textSize = 36f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.MONOSPACE
        paint.isFakeBoldText = true
        canvas.drawText("[ WARRIOR ]", (width / 2).toFloat(), 200f, paint)

        paint.color = 0xFF505068.toInt() // TextDim
        paint.textSize = 28f
        paint.isFakeBoldText = false
        canvas.drawText("WARRIOR REGISTRY", (width / 2).toFloat(), 250f, paint)

        // Divider
        paint.color = 0xFF1E1E32.toInt() // BorderFaint
        paint.strokeWidth = 3f
        canvas.drawLine(100f, 300f, (width - 100).toFloat(), 300f, paint)

        // 5. Center Rank Icon
        paint.color = rawColorHex
        paint.textSize = 280f
        paint.isFakeBoldText = true
        canvas.drawText(hunter.rankGlyph, (width / 2).toFloat(), 650f, paint)

        // Rank label
        paint.textSize = 60f
        canvas.drawText(hunter.rankLabel, (width / 2).toFloat(), 750f, paint)

        // Hunter name
        paint.color = 0xFFE8E8F0.toInt() // TextPrimary
        paint.textSize = 80f
        canvas.drawText(hunter.name.uppercase(), (width / 2).toFloat(), 900f, paint)

        // LV. level
        paint.color = 0xFF1D9E75.toInt() // SystemGreen
        paint.textSize = 48f
        canvas.drawText("LV. ${hunter.level}", (width / 2).toFloat(), 980f, paint)

        // 6. Centered Stats info Row
        val statValY = 1310f
        val statLblY = 1370f
        
        // Total XP
        paint.color = rawColorHex
        paint.textSize = 50f
        canvas.drawText(hunter.totalXP.toString(), (width * 0.25).toFloat(), statValY, paint)
        paint.color = 0xFF505068.toInt() // TextDim
        paint.textSize = 28f
        canvas.drawText("TOTAL XP", (width * 0.25).toFloat(), statLblY, paint)

        // Rank label (center col)
        paint.color = rawColorHex
        paint.textSize = 50f
        canvas.drawText(hunter.rankLabel, (width * 0.5f).toFloat(), statValY, paint)
        paint.color = 0xFF505068.toInt() // TextDim
        paint.textSize = 28f
        canvas.drawText("RANK", (width * 0.5f).toFloat(), statLblY, paint)

        // Protocol label (right col)
        paint.color = rawColorHex
        paint.textSize = 50f
        canvas.drawText("WARRIOR", (width * 0.75f).toFloat(), statValY, paint)
        paint.color = 0xFF505068.toInt() // TextDim
        paint.textSize = 28f
        canvas.drawText("PROTOCOL", (width * 0.75f).toFloat(), statLblY, paint)

        // 7. XP Bar drawing
        val barY = 1500f
        val barLeft = 150f
        val barRight = (width - 150).toFloat()
        val barWidth = barRight - barLeft
        val progressRight = barLeft + (barWidth * hunter.progressPercent)

        // Track bar
        paint.color = 0xFF1E1E32.toInt() // BorderFaint
        paint.strokeWidth = 16f
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(barLeft, barY, barRight, barY, paint)

        // Progress fill
        paint.color = 0xFF1D9E75.toInt() // SystemGreen
        canvas.drawLine(barLeft, barY, progressRight, barY, paint)

        // 8. Bottom Date/Footer
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        paint.color = 0xFF505068.toInt() // TextDim
        paint.textSize = 28f
        paint.isFakeBoldText = false
        canvas.drawText("[ AWAKENING PROTOCOL — SHARE YOUR RANK ]", (width / 2).toFloat(), 1700f, paint)
        canvas.drawText(currentDate, (width / 2).toFloat(), 1750f, paint)

        return bitmap
    }
}
