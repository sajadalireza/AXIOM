package com.axiom.app.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.ui.theme.*

/**
 * Collapsible section for secondary operational surfaces (decision countdown, habit logs,
 * vitals, challenges, intel feed) to minimize cognitive load and maintain focus on the
 * Core Loop and Next Meaningful Mission.
 */
@Composable
fun SecondarySurfacesSection(
    title: String = stringResource(R.string.home_secondary_surfaces_title),
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val colors = LocalAxiomColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("secondary_surfaces_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Toggle bar with minimum 48dp touch target
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.dimSurface)
                .border(1.dp, colors.borderFaint, RoundedCornerShape(8.dp))
                .clickable { expanded = !expanded }
                .testTag("secondary_surfaces_toggle")
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (expanded) "▼" else "▶",
                    fontSize = 10.sp,
                    color = colors.textDim
                )
                Text(
                    text = title,
                    fontFamily = FiraCode,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = if (expanded) {
                    stringResource(R.string.home_secondary_surfaces_collapse)
                } else {
                    stringResource(R.string.home_secondary_surfaces_expand)
                },
                fontFamily = FiraCode,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = colors.systemGreen
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                content()
            }
        }
    }
}
