package com.axiom.app.ui.components.xion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*

data class XionMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long,
    val isStreaming: Boolean = false
)

@Composable
fun XionChatList(
    messages: List<XionMessage>,
    systemColor: Color,
    xionMood: XionMood,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val listState = rememberLazyListState()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .testTag("xion_chat_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp)
    ) {
        itemsIndexed(messages, key = { _, msg -> msg.id }) { index, message ->
            // Check if there's a 5 minute (300,000 ms) gap, or if it's the first message
            val showSessionHeader = if (index == 0) {
                true
            } else {
                val prevMsg = messages[index - 1]
                (message.timestamp - prevMsg.timestamp) > 300_000L
            }

            if (showSessionHeader) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(1.dp)
                            .background(colors.borderFaint)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "TRANSMISSION RECEIVED",
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = colors.textDim,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(1.dp)
                            .background(colors.borderFaint)
                    )
                }
            }

            XionMessageBubble(
                text = message.text,
                isUser = message.isUser,
                timestamp = message.timestamp,
                isStreaming = message.isStreaming,
                systemColor = systemColor,
                xionMood = xionMood
            )
        }
    }
}
