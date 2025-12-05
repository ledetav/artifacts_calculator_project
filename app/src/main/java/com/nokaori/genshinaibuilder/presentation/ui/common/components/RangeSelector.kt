package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeSelector(
    title: String,
    range: ClosedFloatingPointRange<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    keyValues: List<Int>,
    fromLabel: String,
    toLabel: String,
    onRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onManualInput: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var fromText by remember { mutableStateOf(range.start.roundToInt().toString()) }
    var toText by remember { mutableStateOf(range.endInclusive.roundToInt().toString()) }

    LaunchedEffect(range) {
        fromText = range.start.roundToInt().toString()
        toText = range.endInclusive.roundToInt().toString()
    }

    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        RangeInputFields(
            fromValue = fromText,
            toValue = toText,
            fromLabel = fromLabel,
            toLabel = toLabel,
            onFromChanged = { fromText = it },
            onToChanged = { toText = it },
            onCommit = { onManualInput(fromText, toText) }
        )

        val sliderColors = SliderDefaults.colors()

        RangeSlider(
            value = range,
            onValueChange = onRangeChanged,
            valueRange = valueRange,
            steps = (valueRange.endInclusive - valueRange.start).roundToInt() - 1,
            colors = sliderColors,
            startThumb = { SliderThumb() },
            endThumb = { SliderThumb() },
            track = { KeyPointsTrack(it, valueRange, keyValues, sliderColors) }
        )
    }
}
