package com.axiom.app.presentation.dungeon

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.axiom.app.ui.DungeonViewModel
import com.axiom.app.ui.components.RarityBadge
import com.axiom.app.ui.components.TerminalTextField
import com.axiom.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDungeonScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DungeonViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedRarity by remember { mutableStateOf("Normal") }
    var totalStages by remember { mutableStateOf(5) } // 1 to 10 default 5
    var stageNames by remember(totalStages) {
        mutableStateOf(List(totalStages) { "" })
    }
    var stageNamesExpanded by remember { mutableStateOf(false) }

    var isRarityDropdownExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val rarities = listOf("Normal", "Rare", "Epic", "Legendary", "Mythic")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dungeons_create_title),
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LocalAxiomColors.current.voidBlack,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = LocalAxiomColors.current.voidBlack,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dungeon Name
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.dungeons_name_label),
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = TextDim,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("E.g. Void Realm Gate", color = TextDim) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SystemGreen,
                        unfocusedBorderColor = BorderFaint,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("field_dungeon_name")
                )
            }

            // Description
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.dungeons_desc_label),
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = TextDim,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("E.g. Conquer this sector to manifest ancient powers.", color = TextDim) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SystemGreen,
                        unfocusedBorderColor = BorderFaint,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("field_dungeon_description")
                )
            }

            // Rarity Dropdown
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.dungeons_difficulty_label),
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = TextDim,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                        .background(ShadowSurface)
                        .clickable { isRarityDropdownExpanded = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RarityBadge(rarity = selectedRarity)
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand",
                            tint = TextSecondary
                        )
                    }
                    DropdownMenu(
                        expanded = isRarityDropdownExpanded,
                        onDismissRequest = { isRarityDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(ShadowSurface)
                    ) {
                        rarities.forEach { rarity ->
                            DropdownMenuItem(
                                text = { Text(rarity, fontFamily = Inter, color = TextPrimary) },
                                onClick = {
                                    selectedRarity = rarity
                                    isRarityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Total Stages (1 to 10)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.dungeons_total_stages_label),
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = TextDim,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.dungeons_stages_count, totalStages),
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        color = SystemGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = totalStages.toFloat(),
                    onValueChange = { totalStages = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        activeTrackColor = SystemGreen,
                        thumbColor = SystemGreen,
                        inactiveTrackColor = BorderFaint
                    )
                )
            }

            // Stage names inputs collapsible section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { stageNamesExpanded = !stageNamesExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.dungeons_stage_protocols),
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = TextDim,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { stageNamesExpanded = !stageNamesExpanded }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Toggle Stage Protocols",
                            tint = TextDim,
                            modifier = Modifier.graphicsLayer {
                                rotationZ = if (stageNamesExpanded) 180f else 0f
                            }
                        )
                    }
                }

                if (stageNamesExpanded) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 0 until totalStages) {
                            val isBoss = i == totalStages - 1
                            val label = if (isBoss) "BOSS STAGE TITLE" else "STAGE ${i + 1} TITLE"
                            TerminalTextField(
                                value = stageNames[i],
                                onValueChange = { text ->
                                    stageNames = stageNames.toMutableList().apply {
                                        set(i, text.take(40))
                                    }
                                },
                                label = label,
                                placeholder = {
                                    Text(
                                        text = if (isBoss) "Defaults to 'BOSS FIGHT'" else "Defaults to 'STAGE ${i + 1}'",
                                        color = TextDim,
                                        fontFamily = JetBrainsMono
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action: BEGIN RAID Button
            val isFormValid = name.isNotBlank()
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.96f else 1.0f,
                animationSpec = tween(100),
                label = "begin_raid_scale"
            )

            Button(
                onClick = {
                    if (isFormValid) {
                        viewModel.createDungeon(
                            name = name,
                            description = description,
                            rarity = selectedRarity,
                            totalStages = totalStages,
                            stageDescriptions = stageNames.joinToString("||")
                        )
                        onBack()
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SystemGreen,
                    contentColor = VoidBlack,
                    disabledContainerColor = DimSurface,
                    disabledContentColor = TextDim
                ),
                shape = RoundedCornerShape(4.dp),
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .scale(buttonScale)
                    .testTag("btn_begin_raid")
            ) {
                Text(
                    text = stringResource(R.string.dungeons_begin_raid),
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
