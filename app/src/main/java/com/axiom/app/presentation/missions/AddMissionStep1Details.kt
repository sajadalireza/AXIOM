package com.axiom.app.presentation.missions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.components.TerminalTextField
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.TextDim

@Composable
fun AddMissionStep1Details(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // MISSION TITLE (TerminalTextField)
        TerminalTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "MISSION TITLE",
            placeholder = { Text("E.g. Clear the data analysis gate", color = TextDim, fontFamily = JetBrainsMono, fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("field_mission_title")
        )

        // MISSION DESCRIPTION / DETAILS (TerminalTextField)
        TerminalTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "MISSION DESCRIPTION / DETAILS",
            placeholder = { Text("E.g. Investigate user retention metrics and outline key improvements", color = TextDim, fontFamily = JetBrainsMono, fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("field_mission_description")
        )
    }
}
