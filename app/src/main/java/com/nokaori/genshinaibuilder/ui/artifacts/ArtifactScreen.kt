package com.nokaori.genshinaibuilder.ui.artifacts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.nokaori.genshinaibuilder.data.ArtifactSet
import com.nokaori.genshinaibuilder.data.ArtifactSlot
import com.nokaori.genshinaibuilder.data.StatType
import com.nokaori.genshinaibuilder.viewmodel.ArtifactViewModel
import com.nokaori.genshinaibuilder.ui.artifacts.components.ArtifactItem
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactScreen(artifactViewModel: ArtifactViewModel = viewModel()) {
    val searchQuery by artifactViewModel.searchQuery.collectAsState()
    val searchedArtifacts by artifactViewModel.searchedArtifacts.collectAsState()
    val isFilterDialogShown by artifactViewModel.isFilterDialogShown.collectAsState()
    val areFiltersChanged by artifactViewModel.areFiltersChanged.collectAsState()
    val selectedArtifactSet by artifactViewModel.selectedArtifactSet.collectAsState()
    val isArtifactSetDropdownExpanded by artifactViewModel.isArtifactSetDropdownExpanded.collectAsState()
    val artifactSetSearchQuery by artifactViewModel.artifactSetSearchQuery.collectAsState()
    val filteredArtifactSets by artifactViewModel.filteredArtifactSets.collectAsState()
    val selectedArtifactLevelRange by artifactViewModel.selectedArtifactLevelRange.collectAsState()
    val selectedArtifactSlots by artifactViewModel.selectedArtifactSlots.collectAsState()
    val selectedArtifactMainStat by artifactViewModel.selectedArtifactMainStat.collectAsState()

    if(isFilterDialogShown){
        FilterDialog(
            areFiltersChanged = areFiltersChanged,
            onDismiss = artifactViewModel::onFilterDialogDismiss,
            onApply = artifactViewModel::onApplyFilters,
            onReset = artifactViewModel::onResetFilters,
            selectedArtifactSet = selectedArtifactSet,
            artifactSetSearchQuery = artifactSetSearchQuery,
            isArtifactSetDropdownExpanded = isArtifactSetDropdownExpanded,
            filteredArtifactSets = filteredArtifactSets,
            onArtifactSetSelected = { artifactViewModel.onArtifactSetSelected(it) },
            onArtifactSetSearchQueryChanged = { artifactViewModel.onArtifactSetSearchQueryChanged(it)},
            onArtifactSetFilterDropdownDismiss = artifactViewModel::onArtifactSetFilterDropdownDismiss,
            onClearSelectedArtifactSet = artifactViewModel::onClearSelectedArtifactSet,
            selectedArtifactLevelRange = selectedArtifactLevelRange,
            onArtifactLevelRangeChanged = { artifactViewModel.onLevelRangeChanged(it) },
            onLevelManualInput = { from, to -> artifactViewModel.onLevelManualInputChanged(from, to)},
            selectedArtifactSlots = selectedArtifactSlots,
            onArtifactSlotClicked = { artifactViewModel.onArtifactSlotClicked(it) },
            selectedArtifactMainStat = selectedArtifactMainStat,
            onArtifactMainStatSelected = { artifactViewModel.onArtifactMainStatSelected(it) },
            onClearSelectedArtifactMainStat = artifactViewModel::onClearSelectedArtifactMainStat
        )
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Артефакты",
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(onClick = { artifactViewModel.addDefaultaArtifact() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить артефакт"
                )
            }
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newText ->
                artifactViewModel.onSearchQueryChange(newText)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Поиск по артефактам") },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = { artifactViewModel.onFilterIconClicked() }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Фильтр артефактов"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searchedArtifacts) {
                artifact -> ArtifactItem(artifact = artifact)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    areFiltersChanged: Boolean,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    selectedArtifactSet: ArtifactSet?,
    artifactSetSearchQuery: String,
    isArtifactSetDropdownExpanded: Boolean,
    filteredArtifactSets: List<ArtifactSet>,
    onArtifactSetSelected: (ArtifactSet) -> Unit,
    onArtifactSetSearchQueryChanged: (String) -> Unit,
    onArtifactSetFilterDropdownDismiss: () -> Unit,
    onClearSelectedArtifactSet: () -> Unit,
    selectedArtifactLevelRange: ClosedFloatingPointRange<Float>,
    onArtifactLevelRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onLevelManualInput: (String, String) -> Unit,
    selectedArtifactSlots: Set<ArtifactSlot>,
    onArtifactSlotClicked: (ArtifactSlot) -> Unit,
    selectedArtifactMainStat: StatType?,
    onArtifactMainStatSelected: (StatType) -> Unit,
    onClearSelectedArtifactMainStat: () -> Unit
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(screenHeight * 0.7f)
                .padding(8.dp)
        ){
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Фильтры",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть фильтры"
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {

                    ArtifactSetFilterView(
                        selectedArtifactSet = selectedArtifactSet,
                        artifactSetSearchQuery = artifactSetSearchQuery,
                        isArtifactSetDropdownExpanded = isArtifactSetDropdownExpanded,
                        filteredArtifactSets = filteredArtifactSets,
                        onArtifactSetSelected = onArtifactSetSelected,
                        onArtifactSetSearchQueryChanged = onArtifactSetSearchQueryChanged,
                        onArtifactSetFilterDropdownDismiss = onArtifactSetFilterDropdownDismiss,
                        onClearSelectedArtifactSet = onClearSelectedArtifactSet
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ArtifactLevelFilterView(
                        artifactLevelRange = selectedArtifactLevelRange,
                        onArtifactLevelRangeChanged = onArtifactLevelRangeChanged,
                        onLevelManualInput = onLevelManualInput
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ArtifactSlotFilterView(
                        selectedArtifactSlots = selectedArtifactSlots,
                        onArtifactSlotClicked = onArtifactSlotClicked
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ArtifactMainStatFilterView(
                        selectedArtifactMainStat = selectedArtifactMainStat,
                        onArtifactMainStatSelected = onArtifactMainStatSelected,
                        onClearSelectedArtifactMainStat = onClearSelectedArtifactMainStat
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onReset) {
                        Text("Сбросить")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onApply,
                        enabled = areFiltersChanged
                    ) {
                        Text("Применить")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactSetFilterView(
    selectedArtifactSet: ArtifactSet?,
    artifactSetSearchQuery: String,
    isArtifactSetDropdownExpanded: Boolean,
    filteredArtifactSets: List<ArtifactSet>,
    onArtifactSetSelected: (ArtifactSet) -> Unit,
    onArtifactSetSearchQueryChanged: (String) -> Unit,
    onArtifactSetFilterDropdownDismiss: () -> Unit,
    onClearSelectedArtifactSet: () -> Unit
) {
    Column {
        Text(
            text = "Сет артефакта",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = isArtifactSetDropdownExpanded,
            onExpandedChange = {
                if(!it) {
                    onArtifactSetFilterDropdownDismiss()
                }
            }
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = selectedArtifactSet?.name ?: artifactSetSearchQuery,
                onValueChange = onArtifactSetSearchQueryChanged,
                label = { Text("Выберeте сет") },
                trailingIcon = {
                    if(artifactSetSearchQuery.isNotEmpty() || selectedArtifactSet != null){
                        IconButton(onClick = onClearSelectedArtifactSet) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Очистить выбор сета"
                            )
                        }
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isArtifactSetDropdownExpanded)
                    }
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = isArtifactSetDropdownExpanded,
                onDismissRequest = onArtifactSetFilterDropdownDismiss
            ) {
                if(filteredArtifactSets.isEmpty()){
                    DropdownMenuItem(
                        text = { Text("Ничего не найдено")},
                        onClick = {},
                        enabled = false
                    )
                } else {
                    filteredArtifactSets.forEach { artifactSet ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = artifactSet.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = artifactSet.name)
                                }
                            },
                            onClick = {
                                onArtifactSetSelected(artifactSet)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactLevelFilterView(
    artifactLevelRange: ClosedFloatingPointRange<Float>,
    onArtifactLevelRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onLevelManualInput: (String, String) -> Unit
) {
    var fromText by remember { mutableStateOf(artifactLevelRange.start.roundToInt().toString()) }
    var toText by remember { mutableStateOf(artifactLevelRange.endInclusive.roundToInt().toString()) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(artifactLevelRange) {
        fromText = artifactLevelRange.start.roundToInt().toString()
        toText = artifactLevelRange.endInclusive.roundToInt().toString()
    }

    val commitChanges = {
        onLevelManualInput(fromText, toText)
        focusManager.clearFocus()
    }

    Column{
        Text(
            text = "Уровень",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = fromText,
                onValueChange = { fromText = it},
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            commitChanges()
                        }
                    },
                label = { Text("От") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { commitChanges() })
            )

            Spacer(modifier = Modifier.width(16.dp))

            TextField(
                value = toText,
                onValueChange = { toText = it},
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            commitChanges()
                        }
                    },
                label = { Text("До") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { commitChanges() })
            )
        }

        val sliderColors = SliderDefaults.colors()
        val thumbColor = MaterialTheme.colorScheme.primary
        val activeTrackColor = sliderColors.activeTrackColor
        val inactiveTrackColor = sliderColors.inactiveTrackColor
        val keyLevels = listOf(0, 4, 8, 12, 16, 20)

        RangeSlider(
            value = artifactLevelRange,
            onValueChange = onArtifactLevelRangeChanged,
            valueRange = 0f..20f,
            steps = 19,
            colors = sliderColors,
            startThumb = {
                Box(
                    modifier = Modifier.size(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(thumbColor)
                    )
                }
            },

            endThumb = {
                Box(
                    modifier = Modifier.size(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(thumbColor)
                    )
                }
            },

            track = { rangeSliderState ->
                Canvas(modifier = Modifier.fillMaxWidth() ) {
                    val trackWithPx = size.width
                    val trackHeightCenter = center.y

                    fun levelToPx(level: Int): Float {
                        return (level / 20f) * trackWithPx
                    }

                    drawLine(
                        color = sliderColors.inactiveTrackColor,
                        start = Offset(0f, trackHeightCenter),
                        end = Offset(trackWithPx, trackHeightCenter),
                        strokeWidth = 5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    val activeStartPx = levelToPx(rangeSliderState.activeRangeStart.roundToInt())
                    val activeEndPx = levelToPx((rangeSliderState.activeRangeEnd.roundToInt()))

                    drawLine(
                        color = activeTrackColor,
                        start = Offset(activeStartPx, trackHeightCenter),
                        end = Offset(activeEndPx, trackHeightCenter),
                        strokeWidth = 5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    keyLevels.forEach { level ->
                        val position = levelToPx(level)
                        val levelAsFloat = level.toFloat()
                        val color = if (levelAsFloat >= rangeSliderState.activeRangeStart &&
                                    levelAsFloat <= rangeSliderState.activeRangeEnd)
                            activeTrackColor else inactiveTrackColor

                        drawCircle(
                            color = color,
                            center = Offset(x = position, y = center.y),
                            radius = 6.dp.toPx()
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun ArtifactSlotFilterView(
    selectedArtifactSlots: Set<ArtifactSlot>,
    onArtifactSlotClicked: (ArtifactSlot) -> Unit
) {
    fun getIconForSlot(slot: ArtifactSlot) = when (slot) {
        ArtifactSlot.FLOWER_OF_LIFE -> Icons.Default.LocalFlorist
        ArtifactSlot.PLUME_OF_DEATH -> Icons.Default.Edit
        ArtifactSlot.SANDS_OF_EON -> Icons.Default.HourglassEmpty
        ArtifactSlot.GOBLET_OF_EONOTHEM -> Icons.Default.WineBar
        ArtifactSlot.CIRCLET_OF_LOGOS -> Icons.Default.School
    }

    Column {
        Text(
            text = "Слот",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ArtifactSlot.entries.forEach { slot ->
                val isSelected = slot in selectedArtifactSlots

                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else
                    MaterialTheme.colorScheme.surfaceVariant
                val iconColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else
                    MaterialTheme.colorScheme.onSurfaceVariant

                Surface(
                    shape = CircleShape,
                    color = backgroundColor
                ) {
                    IconButton(onClick = {onArtifactSlotClicked(slot)}) {
                        Icon(
                            imageVector = getIconForSlot(slot),
                            contentDescription = slot.displayName,
                            tint = iconColor
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactMainStatFilterView(
    selectedArtifactMainStat: StatType?,
    onArtifactMainStatSelected: (StatType) -> Unit,
    onClearSelectedArtifactMainStat: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val allArtifactStats = remember { StatType.entries.toTypedArray() }

    Column {
        Text(
            text = "Главный стат",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { isExpanded = true}
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedArtifactMainStat?.displayName ?: "Выбрать главный стат",
                    color = if (selectedArtifactMainStat != null) MaterialTheme.colorScheme.onSurface else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (selectedArtifactMainStat != null) {
                    IconButton(
                        onClick = onClearSelectedArtifactMainStat,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Очистить выбор"
                        )
                    }
                } else {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else
                            Icons.Default.ArrowDropDown,
                        contentDescription = "открыть список"
                    )
                }
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                allArtifactStats.forEach { artifactStat ->
                    DropdownMenuItem(
                        text = { Text(text = artifactStat.displayName) },
                        onClick = {
                            onArtifactMainStatSelected(artifactStat)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}