package com.axiom.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.core.sound.AwakenSound
import com.axiom.app.core.sound.SoundEngine
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.ui.theme.SystemGreen
import com.axiom.app.ui.theme.TextDim
import com.axiom.app.ui.theme.TextPrimary
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.Inter

@Composable
fun SystemMessageItem(
    message: SystemMessage,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(message.id) {
        visible = true
        SoundEngine.play(AwakenSound.SYSTEM_ALERT)
    }

    val isFa = java.util.Locale.getDefault().language == "fa"
    val displayedMessage = remember(message.message, isFa) {
        if (isFa) {
            val msg = message.message
            when {
                msg.startsWith("Hunter profile established") -> 
                    "پروفایل هانتر با موفقیت ایجاد شد. هیچ مأموریت فعالی شناسایی نشد. اولین هدف خود را در بخش مأموریت‌ها ثبت کنید."
                msg.startsWith("Dungeons are multi-stage operations") -> 
                    "دانجن‌ها عملیات‌های چندمرحله‌ای هستند. تمام مراحل را تکمیل کرده و باس را شکست دهید تا جوایز ویژه XP دریافت نمایید."
                msg.startsWith("Skill tree initialized") -> 
                    "درخت مهارت راه‌اندازی شد. برای باز کردن قفل کلاس‌های رتبه، XP مأموریت‌ها را جمع‌آوری کنید. برای آشکارسازی سایه به رتبه B برسید."
                msg.startsWith("Tap [ + ] to register") -> 
                    "روی [ + ] ضربه بزنید تا اولین مأموریت خود را ثبت کنید. امتیاز قدرت مأموریت پاداش XP و ردیف کمیاب را تعیین می‌کند."
                msg.startsWith("Shadow Army online") -> 
                    "سپاه سایه آنلاین شد. هر سایه +۵٪ امتیاز XP اضافی برای مأموریت‌های دسته مهارت خود اهدا می‌کند. حداکثر امتیاز اضافی: +۵۰٪."
                msg.contains("Streak Shield activated") || msg.contains("محافظ زنجیره فعال شد") -> {
                    val number = msg.replace(Regex("[^0-9]"), "")
                    if (number.isNotEmpty()) {
                        "⬡ محافظ زنجیره فعال شد. زنجیره متوالی $number روزه شما حفظ گشت."
                    } else {
                        "⬡ محافظ زنجیره فعال شد. زنجیره متوالی شما حفظ گشت."
                    }
                }
                else -> msg
            }
        } else {
            message.message
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 600, easing = EaseOutCubic)
        ) + fadeIn(animationSpec = tween(durationMillis = 600)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isFa) "[ سیستم ]" else "[ SYSTEM ]",
                    color = SystemGreen,
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = displayedMessage,
                    color = TextPrimary,
                    fontFamily = Inter,
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            val formattedTime = remember(message.timestamp) {
                val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                sdf.format(java.util.Date(message.timestamp))
            }

            Text(
                text = formattedTime,
                color = TextDim,
                fontFamily = JetBrainsMono,
                fontSize = 11.sp
            )
        }
    }
}
