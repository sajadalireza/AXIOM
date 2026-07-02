package com.axiom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.axiom.app.R
import com.axiom.app.ui.theme.Inter
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.LocalAxiomColors

/**
 * One-time, dismissible nudge for anonymous users, shown proactively
 * (e.g. on Home) once they've been using the app for a few days.
 * Encourages linking an email so rank/streak/Skill Tree survive a
 * reinstall or device switch.
 *
 * This dialog itself has no memory of whether it's been shown before —
 * the caller is responsible for checking
 * AxiomPreferences.saveProgressNudgeShownFlow before displaying it, and
 * calling setSaveProgressNudgeShown() as soon as it's dismissed or
 * acted on, so it never appears more than once per install.
 */
@Composable
fun SaveProgressNudgeDialog(
    onSaveProgressClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAxiomColors.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, colors.systemGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
            color = colors.shadowSurface,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = stringResource(R.string.save_progress_nudge_title),
                    color = colors.systemGreen,
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.save_progress_nudge_body),
                    color = colors.textSecondary,
                    fontFamily = Inter,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(colors.systemGreen, RoundedCornerShape(4.dp))
                        .clickable { onSaveProgressClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.save_progress_nudge_cta),
                        color = colors.voidBlack,
                        fontSize = 12.sp,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDismiss() }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.save_progress_nudge_dismiss),
                        color = colors.textDim,
                        fontSize = 12.sp,
                        fontFamily = JetBrainsMono
                    )
                }
            }
        }
    }
}
