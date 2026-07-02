package com.axiom.app.presentation.ceremony

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import kotlinx.coroutines.delay

@Composable
fun CeremonyContainer(
    spec: CeremonySpec,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // 1. Timer for auto-dismiss (if durationMs > 0)
    if (spec.durationMs > 0) {
        LaunchedEffect(spec.durationMs) {
            delay(spec.durationMs.toLong())
            onDismiss()
        }
    }

    // 2. Wrap content in a Box that handles dismiss on tap
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                if (spec.dismissible) {
                    contentDescription = "Dismiss celebration"
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // No visual ripple for overlay background click
                enabled = spec.dismissible
            ) {
                onDismiss()
            },
        content = content
    )
}
