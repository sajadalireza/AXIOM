package com.axiom.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScanlineOverlay(
    modifier: Modifier = Modifier
) {
    CyberScanEffect(
        modifier = modifier,
        mode = ScanMode.AMBIENT
    )
}
