package com.axiom.app.presentation.firstwin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.domain.firstwin.FirstWinArea
import com.axiom.app.domain.firstwin.FirstWinPosition

@Composable
fun FirstWinScreen(
    viewModel: FirstWinViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.start()
    }

    FirstWinContent(
        state = state,
        onSelectArea = viewModel::selectArea,
        onContinueArea = viewModel::continueFromArea,
        onActionTitleChange = viewModel::setActionTitle,
        onBackToArea = viewModel::backToArea,
        onCreateMission = viewModel::createMission,
        onRetryLoad = viewModel::start,
    )
}

@Composable
private fun FirstWinContent(
    state: FirstWinEntryUiState,
    onSelectArea: (FirstWinArea) -> Unit,
    onContinueArea: () -> Unit,
    onActionTitleChange: (String) -> Unit,
    onBackToArea: () -> Unit,
    onCreateMission: () -> Unit,
    onRetryLoad: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            state.isLoading || (state.position == null && state.error == null) -> {
                FirstWinLoading()
            }

            state.error == FirstWinUiError.LOAD && state.sessionId == null -> {
                FirstWinLoadError(onRetryLoad)
            }

            state.position == FirstWinPosition.AREA &&
                state.draft.step == FirstWinDraftStep.AREA -> {
                FirstWinAreaStep(
                    selectedArea = state.draft.selectedArea,
                    onSelectArea = onSelectArea,
                    onContinue = onContinueArea,
                )
            }

            state.position == FirstWinPosition.AREA &&
                state.draft.step == FirstWinDraftStep.ACTION -> {
                FirstWinActionStep(
                    actionTitle = state.draft.actionTitle,
                    canCreateMission = state.draft.canCreateMission,
                    isBusy = state.isBusy,
                    showError = state.error == FirstWinUiError.CREATE_MISSION,
                    onActionTitleChange = onActionTitleChange,
                    onBack = onBackToArea,
                    onCreateMission = onCreateMission,
                )
            }

            state.position == FirstWinPosition.DO && state.mission != null -> {
                FirstWinDoStep(state.mission.title)
            }

            else -> FirstWinLoading()
        }
    }
}

@Composable
private fun FirstWinLoading() {
    val description = stringResource(R.string.first_win_loading)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
private fun FirstWinLoadError(onRetry: () -> Unit) {
    FirstWinPage {
        Text(
            text = stringResource(R.string.first_win_load_error),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.first_win_load_error_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        PrimaryButton(
            text = stringResource(R.string.first_win_try_again),
            enabled = true,
            onClick = onRetry,
        )
    }
}

@Composable
private fun FirstWinAreaStep(
    selectedArea: FirstWinArea?,
    onSelectArea: (FirstWinArea) -> Unit,
    onContinue: () -> Unit,
) {
    FirstWinPage {
        StepLabel(R.string.first_win_step_1_of_4)
        Text(
            text = stringResource(R.string.first_win_area_title),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.first_win_area_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            firstWinAreas.forEach { area ->
                AreaOption(
                    area = area,
                    selected = selectedArea == area,
                    onSelect = { onSelectArea(area) },
                )
            }
        }
        PrimaryButton(
            text = stringResource(R.string.first_win_continue),
            enabled = selectedArea != null,
            onClick = onContinue,
        )
    }
}

@Composable
private fun AreaOption(
    area: FirstWinArea,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outline
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton,
            ),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
            )
            Text(
                text = areaLabel(area),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun FirstWinActionStep(
    actionTitle: String,
    canCreateMission: Boolean,
    isBusy: Boolean,
    showError: Boolean,
    onActionTitleChange: (String) -> Unit,
    onBack: () -> Unit,
    onCreateMission: () -> Unit,
) {
    FirstWinPage {
        TextButton(
            onClick = onBack,
            enabled = !isBusy,
            modifier = Modifier.heightIn(min = 48.dp),
        ) {
            Text(stringResource(R.string.first_win_back))
        }
        StepLabel(R.string.first_win_step_2_of_4)
        Text(
            text = stringResource(R.string.first_win_action_title),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.first_win_action_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = actionTitle,
            onValueChange = onActionTitleChange,
            enabled = !isBusy,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            label = { Text(stringResource(R.string.first_win_action_label)) },
            minLines = 2,
        )
        if (showError) {
            Text(
                text = stringResource(R.string.first_win_create_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
        PrimaryButton(
            text = if (isBusy) {
                stringResource(R.string.first_win_starting)
            } else {
                stringResource(R.string.first_win_start_action)
            },
            enabled = canCreateMission && !isBusy,
            onClick = onCreateMission,
        )
    }
}

@Composable
private fun FirstWinDoStep(missionTitle: String) {
    FirstWinPage {
        StepLabel(R.string.first_win_step_3_of_4)
        Text(
            text = stringResource(R.string.first_win_do_title),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.first_win_do_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Text(
                text = missionTitle,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun FirstWinPage(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@Composable
private fun StepLabel(resourceId: Int) {
    Text(
        text = stringResource(resourceId),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun PrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
        ),
    ) {
        Text(text)
    }
}

@Composable
private fun areaLabel(area: FirstWinArea): String = stringResource(
    when (area) {
        FirstWinArea.WORK -> R.string.first_win_area_work
        FirstWinArea.STUDY -> R.string.first_win_area_study
        FirstWinArea.HEALTH -> R.string.first_win_area_health
        FirstWinArea.PERSONAL -> R.string.first_win_area_personal
    }
)

private val firstWinAreas = listOf(
    FirstWinArea.WORK,
    FirstWinArea.STUDY,
    FirstWinArea.HEALTH,
    FirstWinArea.PERSONAL,
)
