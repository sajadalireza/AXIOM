package com.axiom.app.presentation.firstwin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.domain.firstwin.FirstWinArea
import com.axiom.app.domain.firstwin.FirstWinPosition
import com.axiom.app.ui.components.AxiomPillButton
import com.axiom.app.ui.components.AxiomWordmarkHeader
import com.axiom.app.ui.theme.LocalAxiomColors
import com.axiom.app.ui.theme.Outfit

@Composable
fun FirstWinScreen(
    onHandoffComplete: () -> Unit,
    viewModel: FirstWinViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.start()
    }
    LaunchedEffect(state.position) {
        if (state.position == FirstWinPosition.HOME) {
            onHandoffComplete()
        }
    }

    FirstWinContent(
        state = state,
        onSelectArea = viewModel::selectArea,
        onContinueArea = viewModel::continueFromArea,
        onActionTitleChange = viewModel::setActionTitle,
        onBackToArea = viewModel::backToArea,
        onCreateMission = viewModel::createMission,
        onCompleteMission = viewModel::completeMission,
        onContinueReward = viewModel::continueFromReward,
        onFinishForNow = viewModel::finishForNow,
        onCompleteHandoff = viewModel::completeHandoff,
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
    onCompleteMission: () -> Unit,
    onContinueReward: () -> Unit,
    onFinishForNow: () -> Unit,
    onCompleteHandoff: () -> Unit,
    onRetryLoad: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LocalAxiomColors.current.voidBlack,
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
                FirstWinDoStep(
                    missionTitle = state.mission.title,
                    isBusy = state.isBusy,
                    showError = state.error == FirstWinUiError.COMPLETE_MISSION,
                    onComplete = onCompleteMission,
                )
            }

            state.position == FirstWinPosition.REWARD ||
            state.position == FirstWinPosition.NEXT ||
            state.position == FirstWinPosition.HANDOFF -> {
                val missionTitle = state.mission?.title ?: state.draft.actionTitle.ifBlank { "Read 2 pages" }
                FirstWinRewardHandoffStep(
                    missionTitle = missionTitle,
                    isBusy = state.isBusy,
                    showError = state.error != null,
                    onContinue = {
                        when (state.position) {
                            FirstWinPosition.REWARD -> onContinueReward()
                            FirstWinPosition.NEXT -> onFinishForNow()
                            else -> onCompleteHandoff()
                        }
                    },
                    onBackHome = onCompleteHandoff,
                )
            }

            else -> FirstWinLoading()
        }
    }
}

@Composable
private fun FirstWinLoading() {
    val colors = LocalAxiomColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Loading" },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = Color(0xFF2EE6A8),
            strokeWidth = 3.dp,
        )
    }
}

@Composable
private fun FirstWinLoadError(onRetry: () -> Unit) {
    val colors = LocalAxiomColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.first_win_load_error),
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = Outfit,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            ),
            modifier = Modifier.semantics { heading() },
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.first_win_load_error_body),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = Outfit,
                color = colors.textSecondary,
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        AxiomPillButton(
            text = stringResource(R.string.first_win_try_again),
            onClick = onRetry
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STEP 1: Exact PO Reference 04_first_win_step1.png
// 2x2 Grid (Work, Study, Health, Personal) with mountain backdrop
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FirstWinAreaStep(
    selectedArea: FirstWinArea?,
    onSelectArea: (FirstWinArea) -> Unit,
    onContinue: () -> Unit,
) {
    val colors = LocalAxiomColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        // Mountain Backdrop
        Image(
            painter = painterResource(R.drawable.bg_mountain_monolith),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 200.dp)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AxiomWordmarkHeader(
                    modifier = Modifier.padding(top = 10.dp),
                    fontSize = 22.sp
                )

                CinematicStepHeader(currentStep = 1, totalSteps = 4)

                // Title: "Where would one small win help most?"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.first_win_step1_title_1),
                            fontFamily = Outfit,
                            fontWeight = FontWeight.Light,
                            fontSize = 27.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = stringResource(R.string.first_win_step1_title_gold),
                            fontFamily = Outfit,
                            fontWeight = FontWeight.Normal,
                            fontSize = 27.sp,
                            color = Color(0xFFE5C88F)
                        )
                    }
                    Text(
                        text = stringResource(R.string.first_win_step1_title_2).trimStart(),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Light,
                        fontSize = 27.sp,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = stringResource(R.string.first_win_step1_subtitle),
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Light,
                    fontSize = 15.sp,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // 2x2 Grid Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Row 1: Work & Study
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AreaCard(
                        area = FirstWinArea.WORK,
                        title = stringResource(R.string.first_win_area_work),
                        subtitle = stringResource(R.string.first_win_area_work_desc),
                        bgDrawableId = R.drawable.bg_card_work,
                        isSelected = selectedArea == FirstWinArea.WORK,
                        onClick = { onSelectArea(FirstWinArea.WORK) },
                        modifier = Modifier.weight(1f)
                    ) {
                        WorkIcon(color = Color(0xFFE5C88F))
                    }

                    AreaCard(
                        area = FirstWinArea.STUDY,
                        title = stringResource(R.string.first_win_area_study),
                        subtitle = stringResource(R.string.first_win_area_study_desc),
                        bgDrawableId = R.drawable.bg_card_study,
                        isSelected = selectedArea == FirstWinArea.STUDY,
                        onClick = { onSelectArea(FirstWinArea.STUDY) },
                        modifier = Modifier.weight(1f)
                    ) {
                        StudyIcon(color = Color(0xFF2EE6A8))
                    }
                }

                // Row 2: Health & Personal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AreaCard(
                        area = FirstWinArea.HEALTH,
                        title = stringResource(R.string.first_win_area_health),
                        subtitle = stringResource(R.string.first_win_area_health_desc),
                        bgDrawableId = R.drawable.bg_card_health,
                        isSelected = selectedArea == FirstWinArea.HEALTH,
                        onClick = { onSelectArea(FirstWinArea.HEALTH) },
                        modifier = Modifier.weight(1f)
                    ) {
                        HealthIcon(color = Color(0xFF2EE6A8))
                    }

                    AreaCard(
                        area = FirstWinArea.PERSONAL,
                        title = stringResource(R.string.first_win_area_personal),
                        subtitle = stringResource(R.string.first_win_area_personal_desc),
                        bgDrawableId = R.drawable.bg_card_personal,
                        isSelected = selectedArea == FirstWinArea.PERSONAL,
                        onClick = { onSelectArea(FirstWinArea.PERSONAL) },
                        modifier = Modifier.weight(1f)
                    ) {
                        PersonalIcon(color = Color(0xFFE5C88F))
                    }
                }
            }

            // Bottom CTA
            AxiomPillButton(
                text = stringResource(R.string.btn_continue),
                enabled = selectedArea != null,
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )
        }
    }
}

@Composable
private fun AreaCard(
    area: FirstWinArea,
    title: String,
    subtitle: String,
    bgDrawableId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit,
) {
    val cardShape = RoundedCornerShape(20.dp)
    val borderColor = if (isSelected) Color(0xFF2EE6A8) else Color(0x334E655C)
    val borderWidth = if (isSelected) 1.5.dp else 1.dp

    Box(
        modifier = modifier
            .height(154.dp)
            .clip(cardShape)
            .background(Color(0xCC06120E))
            .border(BorderStroke(borderWidth, borderColor), cardShape)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton,
            )
    ) {
        // Mountain Backdrop Image
        Image(
            painter = painterResource(bgDrawableId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Gradient Scrim overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33000000),
                            Color(0xBB040E0A),
                            Color(0xF5040E0A)
                        )
                    )
                )
        )

        // Top Right Checkmark Badge (when selected)
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2EE6A8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF031611),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Center Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.Center
            ) {
                iconContent()
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = Color(0xFFF0FDF8),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = subtitle,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = Color(0xFF9FB5AC),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STEP 2: Exact PO Reference 05_first_win_step2.png
// Glowing Input Box, Character Counter, and 3 Example Action Cards
// ─────────────────────────────────────────────────────────────────────────────

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
    val colors = LocalAxiomColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        // Mountain Backdrop
        Image(
            painter = painterResource(R.drawable.bg_mountain_monolith),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 200.dp)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AxiomWordmarkHeader(
                    modifier = Modifier.padding(top = 10.dp),
                    fontSize = 22.sp
                )

                CinematicStepHeader(currentStep = 2, totalSteps = 4)

                // Title: "Choose one small action"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.first_win_step2_title_1).trimEnd(),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Light,
                        fontSize = 30.sp,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.first_win_step2_title_gold),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Normal,
                        fontSize = 30.sp,
                        color = Color(0xFFE5C88F),
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = stringResource(R.string.first_win_step2_subtitle),
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Light,
                    fontSize = 15.sp,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Input and Examples Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Glowing Input Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xDD071A14))
                        .border(BorderStroke(1.5.dp, Color(0xFF2EE6A8)), RoundedCornerShape(18.dp))
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Pencil Icon
                            PencilIcon(color = Color(0xFF2EE6A8))

                            // Action Input Field
                            BasicTextField(
                                value = actionTitle,
                                onValueChange = { if (it.length <= 100) onActionTitleChange(it) },
                                enabled = !isBusy,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontFamily = Outfit,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 17.sp,
                                    color = Color(0xFFF0FDF8)
                                ),
                                cursorBrush = SolidColor(Color(0xFF2EE6A8)),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("first_win_action_input"),
                                decorationBox = { innerTextField ->
                                    if (actionTitle.isEmpty()) {
                                        Text(
                                            text = stringResource(R.string.first_win_step2_input_placeholder),
                                            fontFamily = Outfit,
                                            fontWeight = FontWeight.Light,
                                            fontSize = 17.sp,
                                            color = Color(0xFF6B8A7E)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }

                        // Character counter at bottom right
                        Text(
                            text = "${actionTitle.length}/100",
                            fontFamily = Outfit,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = Color(0xFF6B8A7E),
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 14.dp)
                        )
                    }
                }

                // Section Divider: "────  Examples  ────"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color(0x334E655C))
                    )
                    Text(
                        text = stringResource(R.string.first_win_step2_examples_header),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color(0xFF8FA89E)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color(0x334E655C))
                    )
                }

                // 3 Example Cards in a row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ExampleCard(
                        text = stringResource(R.string.first_win_example_1),
                        onClick = { onActionTitleChange("Reply to one email") },
                        modifier = Modifier.weight(1f)
                    ) {
                        EmailIcon(color = Color(0xFFE5C88F))
                    }

                    ExampleCard(
                        text = stringResource(R.string.first_win_example_2),
                        onClick = { onActionTitleChange("Read 2 pages") },
                        modifier = Modifier.weight(1f)
                    ) {
                        StudyIcon(color = Color(0xFFE5C88F))
                    }

                    ExampleCard(
                        text = stringResource(R.string.first_win_example_3),
                        onClick = { onActionTitleChange("Take a 10-minute walk") },
                        modifier = Modifier.weight(1f)
                    ) {
                        WalkIcon(color = Color(0xFFE5C88F))
                    }
                }
            }

            // Bottom CTA
            AxiomPillButton(
                text = stringResource(R.string.btn_continue),
                enabled = canCreateMission && !isBusy,
                onClick = onCreateMission,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )
        }
    }
}

@Composable
private fun ExampleCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .height(108.dp)
            .clip(shape)
            .background(Color(0xCC071611))
            .border(BorderStroke(1.dp, Color(0x444E655C)), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                iconContent()
            }
            Text(
                text = text,
                fontFamily = Outfit,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Color(0xFFD4E2DC),
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
                maxLines = 2
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STEP 3: Exact PO Reference 06_first_win_step3.png
// Active Action Card with duration chip and "I finished it ->" CTA
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FirstWinDoStep(
    missionTitle: String,
    isBusy: Boolean,
    showError: Boolean,
    onComplete: () -> Unit,
) {
    val colors = LocalAxiomColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        // Mountain Backdrop
        Image(
            painter = painterResource(R.drawable.bg_mountain_monolith),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 200.dp)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AxiomWordmarkHeader(
                    modifier = Modifier.padding(top = 10.dp),
                    fontSize = 22.sp
                )

                CinematicStepHeader(currentStep = 3, totalSteps = 4)

                // Title: "Now do your small action."
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.first_win_step3_title_1).trimEnd(),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Light,
                        fontSize = 32.sp,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.first_win_step3_title_gold),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Normal,
                        fontSize = 32.sp,
                        color = Color(0xFFE5C88F),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Central Active Action Card
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xDD071C15))
                        .border(BorderStroke(1.5.dp, Color(0xFF2EE6A8)), RoundedCornerShape(24.dp))
                        .padding(horizontal = 24.dp, vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Glowing Icon
                        StudyIcon(
                            color = Color(0xFF2EE6A8),
                            modifier = Modifier.size(48.dp)
                        )

                        // Action Title
                        Text(
                            text = missionTitle,
                            fontFamily = Outfit,
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp,
                            color = Color(0xFFF0FDF8),
                            textAlign = TextAlign.Center
                        )

                        // Duration pill chip
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ClockIcon(color = Color(0xFF8FA89E))
                            Text(
                                text = stringResource(R.string.first_win_duration_estimate),
                                fontFamily = Outfit,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color(0xFF8FA89E)
                            )
                        }
                    }
                }

                // Guidance below card
                Text(
                    text = stringResource(R.string.first_win_step3_instruction),
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Light,
                    fontSize = 15.sp,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom CTA: "I finished it  →"
            AxiomPillButton(
                text = stringResource(R.string.first_win_step3_btn),
                enabled = !isBusy,
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STEP 4: Exact PO Reference 07_first_win_step4.png
// Floating Checkmark Badge, 3-Stat Summary Card, Quote, and Handoff
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FirstWinRewardHandoffStep(
    missionTitle: String,
    isBusy: Boolean,
    showError: Boolean,
    onContinue: () -> Unit,
    onBackHome: () -> Unit,
) {
    val colors = LocalAxiomColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        // Mountain Backdrop
        Image(
            painter = painterResource(R.drawable.bg_mountain_monolith),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 200.dp)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AxiomWordmarkHeader(
                    modifier = Modifier.padding(top = 10.dp),
                    fontSize = 22.sp
                )

                CinematicStepHeader(currentStep = 4, totalSteps = 4)

                // Title: "Great job! You completed your first action."
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.first_win_step4_title_gold),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Normal,
                        fontSize = 32.sp,
                        color = Color(0xFFE5C88F),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.first_win_step4_title_2),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Light,
                        fontSize = 26.sp,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )
                }
            }

            // Summary Section with Floating Badge and 3 Stats
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // Summary Container Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color(0xEE071A14))
                            .border(BorderStroke(1.dp, Color(0x662EE6A8)), RoundedCornerShape(22.dp))
                            .padding(horizontal = 20.dp, vertical = 28.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Spacer(modifier = Modifier.height(10.dp))

                            // Action Title
                            Text(
                                text = missionTitle,
                                fontFamily = Outfit,
                                fontWeight = FontWeight.Medium,
                                fontSize = 21.sp,
                                color = Color(0xFFF0FDF8),
                                textAlign = TextAlign.Center
                            )

                            // 3-Column Stats Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Stat 1: Action
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    StudyIcon(color = Color(0xFF9FB5AC), modifier = Modifier.size(22.dp))
                                    Text(
                                        text = stringResource(R.string.first_win_stat_action),
                                        fontFamily = Outfit,
                                        fontSize = 13.sp,
                                        color = Color(0xFF8FA89E)
                                    )
                                    Text(
                                        text = stringResource(R.string.first_win_stat_completed),
                                        fontFamily = Outfit,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = Color(0xFF34D399)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(1.dp)
                                        .background(Color(0x334E655C))
                                )

                                // Stat 2: Time
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ClockIcon(color = Color(0xFF9FB5AC), modifier = Modifier.size(22.dp))
                                    Text(
                                        text = stringResource(R.string.first_win_stat_time),
                                        fontFamily = Outfit,
                                        fontSize = 13.sp,
                                        color = Color(0xFF8FA89E)
                                    )
                                    Text(
                                        text = stringResource(R.string.first_win_duration_estimate),
                                        fontFamily = Outfit,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = Color(0xFFF0FDF8)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(1.dp)
                                        .background(Color(0x334E655C))
                                )

                                // Stat 3: Progress
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    BarChartIcon(color = Color(0xFF9FB5AC), modifier = Modifier.size(22.dp))
                                    Text(
                                        text = stringResource(R.string.first_win_stat_progress),
                                        fontFamily = Outfit,
                                        fontSize = 13.sp,
                                        color = Color(0xFF8FA89E)
                                    )
                                    Text(
                                        text = "1/1",
                                        fontFamily = Outfit,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = Color(0xFF34D399)
                                    )
                                }
                            }
                        }
                    }

                    // Floating Emerald Checkmark Badge
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF34D399),
                                        Color(0xFF10B981)
                                    )
                                )
                            )
                            .border(2.dp, Color(0xFF6EE7B7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Action Completed",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quote block
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.first_win_quote),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                        color = Color(0xFFD4E2DC),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.first_win_quote_author),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color(0xFF8FA89E),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bottom CTA & Back to Home text link
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AxiomPillButton(
                    text = stringResource(R.string.btn_continue),
                    enabled = !isBusy,
                    onClick = onContinue
                )

                Text(
                    text = stringResource(R.string.first_win_btn_home),
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color(0xFF8FA89E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable(onClick = onBackHome)
                        .padding(4.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REUSABLE STEPPER AND VECTOR ICONS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CinematicStepHeader(
    currentStep: Int,
    totalSteps: Int = 4,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..totalSteps) {
                val isActive = i <= currentStep
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isActive) Color(0xFF2EE6A8) else Color(0x334E655C))
                )
            }
        }
        Text(
            text = stringResource(R.string.first_win_step_indicator, currentStep, totalSteps),
            fontFamily = Outfit,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = Color(0xFF8FA89E),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WorkIcon(modifier: Modifier = Modifier, color: Color = Color(0xFFF0C860)) {
    Canvas(modifier = modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        val handle = Path().apply {
            moveTo(w * 0.35f, h * 0.25f)
            lineTo(w * 0.35f, h * 0.12f)
            lineTo(w * 0.65f, h * 0.12f)
            lineTo(w * 0.65f, h * 0.25f)
        }
        drawPath(handle, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.10f, h * 0.25f),
            size = Size(w * 0.80f, h * 0.65f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )
        drawLine(
            color = color,
            start = Offset(w * 0.10f, h * 0.52f),
            end = Offset(w * 0.90f, h * 0.52f),
            strokeWidth = 1.5.dp.toPx()
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.44f, h * 0.46f),
            size = Size(w * 0.12f, h * 0.12f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

@Composable
fun StudyIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF2EE6A8)) {
    Canvas(modifier = modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        val leftPage = Path().apply {
            moveTo(w * 0.50f, h * 0.30f)
            cubicTo(w * 0.40f, h * 0.22f, w * 0.25f, h * 0.22f, w * 0.12f, h * 0.26f)
            lineTo(w * 0.12f, h * 0.78f)
            cubicTo(w * 0.25f, h * 0.74f, w * 0.40f, h * 0.74f, w * 0.50f, h * 0.82f)
            close()
        }
        drawPath(leftPage, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        val rightPage = Path().apply {
            moveTo(w * 0.50f, h * 0.30f)
            cubicTo(w * 0.60f, h * 0.22f, w * 0.75f, h * 0.22f, w * 0.88f, h * 0.26f)
            lineTo(w * 0.88f, h * 0.78f)
            cubicTo(w * 0.75f, h * 0.74f, w * 0.60f, h * 0.74f, w * 0.50f, h * 0.82f)
            close()
        }
        drawPath(rightPage, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun HealthIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF2EE6A8)) {
    Canvas(modifier = modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        val centerPetal = Path().apply {
            moveTo(w * 0.50f, h * 0.18f)
            cubicTo(w * 0.62f, h * 0.45f, w * 0.58f, h * 0.75f, w * 0.50f, h * 0.82f)
            cubicTo(w * 0.42f, h * 0.75f, w * 0.38f, h * 0.45f, w * 0.50f, h * 0.18f)
            close()
        }
        drawPath(centerPetal, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        val leftPetal = Path().apply {
            moveTo(w * 0.50f, h * 0.82f)
            cubicTo(w * 0.25f, h * 0.75f, w * 0.15f, h * 0.45f, w * 0.25f, h * 0.32f)
            cubicTo(w * 0.32f, h * 0.45f, w * 0.42f, h * 0.65f, w * 0.50f, h * 0.82f)
        }
        drawPath(leftPetal, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        val rightPetal = Path().apply {
            moveTo(w * 0.50f, h * 0.82f)
            cubicTo(w * 0.75f, h * 0.75f, w * 0.85f, h * 0.45f, w * 0.75f, h * 0.32f)
            cubicTo(w * 0.68f, h * 0.45f, w * 0.58f, h * 0.65f, w * 0.50f, h * 0.82f)
        }
        drawPath(rightPetal, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun PersonalIcon(modifier: Modifier = Modifier, color: Color = Color(0xFFF0C860)) {
    Canvas(modifier = modifier.size(28.dp)) {
        val r = size.minDimension / 2f
        drawCircle(color = color, radius = r * 0.70f, center = center, style = Stroke(width = 2.2.dp.toPx()))
    }
}

@Composable
fun PencilIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF2EE6A8)) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val tip = Path().apply {
            moveTo(w * 0.15f, h * 0.85f)
            lineTo(w * 0.35f, h * 0.85f)
            lineTo(w * 0.85f, h * 0.35f)
            lineTo(w * 0.65f, h * 0.15f)
            lineTo(w * 0.15f, h * 0.65f)
            close()
        }
        drawPath(tip, color = color, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun EmailIcon(modifier: Modifier = Modifier, color: Color = Color(0xFFE5C88F)) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.10f, h * 0.22f),
            size = Size(w * 0.80f, h * 0.56f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = 1.8.dp.toPx())
        )
        val flap = Path().apply {
            moveTo(w * 0.12f, h * 0.25f)
            lineTo(w * 0.50f, h * 0.55f)
            lineTo(w * 0.88f, h * 0.25f)
        }
        drawPath(flap, color = color, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun WalkIcon(modifier: Modifier = Modifier, color: Color = Color(0xFFE5C88F)) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        drawCircle(color = color, radius = w * 0.12f, center = Offset(w * 0.52f, h * 0.18f))
        val body = Path().apply {
            moveTo(w * 0.50f, h * 0.32f)
            lineTo(w * 0.46f, h * 0.56f)
            lineTo(w * 0.30f, h * 0.88f)
            moveTo(w * 0.46f, h * 0.56f)
            lineTo(w * 0.68f, h * 0.88f)
            moveTo(w * 0.32f, h * 0.50f)
            lineTo(w * 0.50f, h * 0.38f)
            lineTo(w * 0.68f, h * 0.46f)
        }
        drawPath(body, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ClockIcon(modifier: Modifier = Modifier, color: Color = Color(0xFFE0E6E3)) {
    Canvas(modifier = modifier.size(16.dp)) {
        val r = size.minDimension / 2f
        drawCircle(color = color, radius = r * 0.85f, center = center, style = Stroke(width = 1.5.dp.toPx()))
        drawLine(color = color, start = center, end = Offset(center.x, center.y - r * 0.50f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color, start = center, end = Offset(center.x + r * 0.35f, center.y), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun BarChartIcon(modifier: Modifier = Modifier, color: Color = Color(0xFF34D399)) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        drawLine(color = color, start = Offset(w * 0.22f, h * 0.78f), end = Offset(w * 0.22f, h * 0.55f), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.50f, h * 0.78f), end = Offset(w * 0.50f, h * 0.35f), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.78f, h * 0.78f), end = Offset(w * 0.78f, h * 0.18f), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
    }
}
