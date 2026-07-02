package com.axiom.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.axiom.app.R
import java.util.Calendar
import java.util.concurrent.TimeUnit

enum class XionAlertMood {
    CALM_BRIEFING,
    INSPIRED_BOOST,
    SYSTEM_WARNING,
    VOID_GLITCH
}

object AxiomNotificationManager {
    const val CHANNEL_ID = "axiom_streak_channel"
    const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "System Alerts"
            val descriptionText = "[ WARRIOR ] Daily briefing"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = android.graphics.Color.GREEN
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun generateXionFace(context: Context, mood: XionAlertMood): Bitmap {
        val size = 256
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val cx = size / 2f
        val cy = size / 2f

        // 1. Establish colors according to mood
        val primaryColor = when (mood) {
            XionAlertMood.CALM_BRIEFING -> 0xFF00FF66.toInt() // Neon green
            XionAlertMood.INSPIRED_BOOST -> 0xFF00E5FF.toInt() // Cyber cyan (energy)
            XionAlertMood.SYSTEM_WARNING -> 0xFFE53935.toInt() // Crimson red (alarm)
            XionAlertMood.VOID_GLITCH -> 0xFFD500F9.toInt() // Glitch purple (void)
        }
        val secondaryColor = when (mood) {
            XionAlertMood.CALM_BRIEFING -> 0x3300FF66.toInt()
            XionAlertMood.INSPIRED_BOOST -> 0x4400E5FF.toInt()
            XionAlertMood.SYSTEM_WARNING -> 0x44E53935.toInt()
            XionAlertMood.VOID_GLITCH -> 0x44D500F9.toInt()
        }

        // Draw radial aura/glow in the background
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy + size * 0.25f, size * 0.45f,
                intArrayOf(secondaryColor, 0x00000000),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy + size * 0.2f, size * 0.4f, glowPaint)

        // Draw Cyber Ears
        val earPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryColor
            style = Paint.Style.FILL
        }
        val leftEar = Path().apply {
            moveTo(cx - size * 0.3f, cy - size * 0.08f)
            lineTo(cx - size * 0.42f, cy - size * 0.25f)
            lineTo(cx - size * 0.35f, cy - size * 0.04f)
            close()
        }
        val rightEar = Path().apply {
            moveTo(cx + size * 0.3f, cy - size * 0.08f)
            lineTo(cx + size * 0.42f, cy - size * 0.25f)
            lineTo(cx + size * 0.35f, cy - size * 0.04f)
            close()
        }
        canvas.drawPath(leftEar, earPaint)
        canvas.drawPath(rightEar, earPaint)

        // Draw Rounded Visor Background
        val vW = size * 0.72f
        val vH = size * 0.44f
        val vL = cx - vW / 2f
        val vT = cy - vH / 2f
        val visorRect = RectF(vL, vT, vL + vW, vT + vH)
        
        val visorBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF0D0D12.toInt() // clean system black background
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(visorRect, 18f, 18f, visorBgPaint)

        val visorStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryColor
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRoundRect(visorRect, 18f, 18f, visorStrokePaint)

        // Draw 2 horizontal custom scan grid lines inside visor
        val scanlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryColor
            alpha = 45
            strokeWidth = 2f
        }
        canvas.drawLine(vL + 8f, cy - 8f, vL + vW - 8f, cy - 8f, scanlinePaint)
        canvas.drawLine(vL + 8f, cy + 12f, vL + vW - 8f, cy + 12f, scanlinePaint)

        // Eye positions (similar to CompanionXionWidget Compose design)
        val lX = cx - vW * 0.22f
        val rX = cx + vW * 0.22f
        val eY = cy + 4f
        val baseRadius = 14f

        val eyePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryColor
            style = Paint.Style.FILL
        }
        val whiteCatchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            style = Paint.Style.FILL
        }

        when (mood) {
            XionAlertMood.CALM_BRIEFING -> {
                // NORMAL IDLE STATE (dual glowing green/neon eyes)
                canvas.drawCircle(lX, eY, baseRadius, eyePaint)
                canvas.drawCircle(rX, eY, baseRadius, eyePaint)
                canvas.drawCircle(lX - 4f, eY - 4f, 4f, whiteCatchPaint)
                canvas.drawCircle(rX - 4f, eY - 4f, 4f, whiteCatchPaint)
                
                // Focused neutral brows
                val browPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = primaryColor
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawLine(lX - 12f, eY - 22f, lX + 10f, eY - 22f, browPaint)
                canvas.drawLine(rX - 10f, eY - 22f, rX + 12f, eY - 22f, browPaint)
            }
            XionAlertMood.INSPIRED_BOOST -> {
                // HIGH ENERGY / EXCITED (Wide bright concentric glowing eyes)
                val outerGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = primaryColor
                    style = Paint.Style.STROKE
                    strokeWidth = 5f
                }
                canvas.drawCircle(lX, eY, baseRadius * 1.3f, eyePaint)
                canvas.drawCircle(rX, eY, baseRadius * 1.3f, eyePaint)
                canvas.drawCircle(lX, eY, baseRadius * 1.8f, outerGlowPaint)
                canvas.drawCircle(rX, eY, baseRadius * 1.8f, outerGlowPaint)
                
                canvas.drawCircle(lX - 5f, eY - 5f, 5f, whiteCatchPaint)
                canvas.drawCircle(rX - 5f, eY - 5f, 5f, whiteCatchPaint)

                // Enthusiastic arched eyebrows
                val happyBrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = primaryColor
                    style = Paint.Style.STROKE
                    strokeWidth = 3.5f
                    strokeCap = Paint.Cap.ROUND
                }
                val browPathL = Path().apply {
                    arcTo(RectF(lX - 14f, eY - 30f, lX + 10f, eY - 18f), 190f, 160f)
                }
                val browPathR = Path().apply {
                    arcTo(RectF(rX - 10f, eY - 30f, rX + 14f, eY - 18f), 190f, 160f)
                }
                canvas.drawPath(browPathL, happyBrowPaint)
                canvas.drawPath(browPathR, happyBrowPaint)
            }
            XionAlertMood.SYSTEM_WARNING -> {
                // WARNING RED ALARM (Angered sharp glowing crimson eyes & inverted vbrows)
                canvas.drawCircle(lX, eY, baseRadius * 1.1f, eyePaint)
                canvas.drawCircle(rX, eY, baseRadius * 1.1f, eyePaint)
                
                val shadowGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = primaryColor
                    alpha = 90
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                }
                canvas.drawCircle(lX, eY, baseRadius * 1.5f, shadowGlow)
                canvas.drawCircle(rX, eY, baseRadius * 1.5f, shadowGlow)
                
                canvas.drawCircle(lX - 3f, eY - 3f, 3.5f, whiteCatchPaint)
                canvas.drawCircle(rX - 3f, eY - 3f, 3.5f, whiteCatchPaint)

                // Critical frown brows
                val angryBrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = primaryColor
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawLine(lX - 12f, eY - 18f, lX + 8f, eY - 25f, angryBrowPaint)
                canvas.drawLine(rX - 8f, eY - 25f, rX + 12f, eY - 18f, angryBrowPaint)
            }
            XionAlertMood.VOID_GLITCH -> {
                // DIGITAL SCRAMBLE / GLITCHED (Offset squared fragments in magenta and cyber cyan)
                val cyberCyanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFF00E5FF.toInt()
                    style = Paint.Style.FILL
                }
                // Draw cyan layer offset
                canvas.drawRect(lX - 16f, eY - 5f, lX + 8f, eY + 5f, cyberCyanPaint)
                canvas.drawRect(rX - 8f, eY - 5f, rX + 16f, eY + 5f, cyberCyanPaint)
                
                // Draw main magenta layer
                canvas.drawRect(lX - 8f, eY - 7f, lX + 16f, eY + 7f, eyePaint)
                canvas.drawRect(rX - 16f, eY - 7f, rX + 8f, eY + 7f, eyePaint)

                // Tech digital glitch lines inside the visor
                val pulsePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0xFF00E5FF.toInt()
                    strokeWidth = 2.5f
                }
                canvas.drawLine(vL + 12f, cy - 24f, vL + vW * 0.38f, cy - 24f, pulsePaint)
                canvas.drawLine(vL + vW * 0.62f, cy + 22f, vL + vW - 12f, cy + 22f, pulsePaint)
                
                // Brow fragments
                canvas.drawRect(lX - 10f, eY - 24f, lX + 2f, eY - 21f, eyePaint)
                canvas.drawRect(rX - 2f, eY - 24f, rX + 10f, eY - 21f, eyePaint)
            }
        }

        return bitmap
    }

    fun sendXionNotification(context: Context, mood: XionAlertMood, title: String? = null, message: String? = null) {
        val faceBitmap = generateXionFace(context, mood)

        val lang = context.getSharedPreferences("axiom_lang", Context.MODE_PRIVATE).getString("lang", "en") ?: "en"
        val isFa = lang == "fa" || java.util.Locale.getDefault().language == "fa"

        val defaultTitle = if (isFa) {
            when (mood) {
                XionAlertMood.CALM_BRIEFING -> "◈ رابط پروتکل سیستم ◈"
                XionAlertMood.INSPIRED_BOOST -> "◈ اوج شار سیناپسی ◈"
                XionAlertMood.SYSTEM_WARNING -> "◈ هشدار: نقض پروتکل ◈"
                XionAlertMood.VOID_GLITCH -> "◈ سـیـگنال‌های خلاء شناسایی شدند ◈"
            }
        } else {
            when (mood) {
                XionAlertMood.CALM_BRIEFING -> "◈ SYSTEM PROTOCOL LIAISON ◈"
                XionAlertMood.INSPIRED_BOOST -> "◈ PEAK SYNAPSE FLUX ◈"
                XionAlertMood.SYSTEM_WARNING -> "◈ ALARM: PROTOCOL BREACHED ◈"
                XionAlertMood.VOID_GLITCH -> "◈ V-V-VOID SIGNALS DETECTED ◈"
            }
        }

        val defaultMessage = if (isFa) {
            when (mood) {
                XionAlertMood.CALM_BRIEFING -> "مامور، تداوم سنگ بنای تسلط است. همین حالا وظایف پروتکل خود را کامل کنید."
                XionAlertMood.INSPIRED_BOOST -> "جهش تمرکز و سرعت فوق‌العاده! فعالیت عصبی با کارایی ۱۲۰٪ عمل می‌کند. برای ارتقای لول آماده‌اید؟"
                XionAlertMood.SYSTEM_WARNING -> "وظیفه حیاتی تکمیل نشده است. اهمال‌کاری را قبل از فروپاشی زنجیره روزانه خود نابود کنید!"
                XionAlertMood.VOID_GLITCH -> "مـمخـتصاتزم...مـمانی در حال تـغییـر... مامور، آنلاین بمان! برای مهار واقعیت یک مأموریت را تکمیل کن..."
            }
        } else {
            when (mood) {
                XionAlertMood.CALM_BRIEFING -> "Agent, consistency is the bedrock of dominance. Complete your protocol tasks now."
                XionAlertMood.INSPIRED_BOOST -> "Phenomenal focus spikes! Neural velocity is operating at 120% efficiency. Ready to level up?"
                XionAlertMood.SYSTEM_WARNING -> "CRITICAL ASSIGNMENT INCOMPLETE. Eliminate procrastination before streak integrity collapses!"
                XionAlertMood.VOID_GLITCH -> "t--temporal coordinates... shifting... S-Stay online, Agent! Complete a mission to anchor reality..."
            }
        }

        val finalTitle = title ?: defaultTitle
        val finalMessage = message ?: defaultMessage

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_axiom_notification)
            .setLargeIcon(faceBitmap)
            .setContentTitle(finalTitle)
            .setContentText(finalMessage)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(finalMessage))

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID + mood.ordinal, builder.build())
        } catch (_: SecurityException) {
            // Android 13 check handled gracefully
        }
    }

    fun scheduleStreakReminder(context: Context, hour: Int = 21, minute: Int = 0) {
        val calendar = Calendar.getInstance()
        val now = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.before(now)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = calendar.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(
            24, TimeUnit.HOURS,
            30, TimeUnit.MINUTES
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("streak_reminder")
            .build()

        try {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "streak_reminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun cancelReminder(context: Context) {
        try {
            WorkManager.getInstance(context).cancelAllWorkByTag("streak_reminder")
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}

