package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeFilter(
    title: String,
    range: ClosedFloatingPointRange<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    keyValues: List<Int>,
    fromLabel: String,
    toLabel: String,
    onRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onManualInput: (String, String) -> Unit
) {
    var fromText by remember { mutableStateOf(range.start.roundToInt().toString()) }
    var toText by remember { mutableStateOf(range.endInclusive.roundToInt().toString()) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(range) {
        fromText = range.start.roundToInt().toString()
        toText = range.endInclusive.roundToInt().toString()
    }

    val commitChanges = {
        onManualInput(fromText, toText)
        focusManager.clearFocus()
    }

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = fromText,
                onValueChange = { fromText = it },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            commitChanges()
                        }
                    },
                label = { Text(fromLabel) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { commitChanges() })
            )

            Spacer(modifier = Modifier.width(16.dp))

            TextField(
                value = toText,
                onValueChange = { toText = it },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            commitChanges()
                        }
                    },
                label = { Text(toLabel) },
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

        RangeSlider(
            value = range,
            onValueChange = onRangeChanged,
            valueRange = valueRange,
            steps = (valueRange.endInclusive - valueRange.start).roundToInt() - 1,
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
                Canvas(modifier = Modifier.fillMaxWidth()) {
                    val trackWidthPx = size.width
                    val trackHeightCenter = center.y
                    val maxValue = valueRange.endInclusive

                    fun valueToPx(value: Int): Float {
                        return (value / maxValue) * trackWidthPx
                    }

                    drawLine(
                        color = sliderColors.inactiveTrackColor,
                        start = Offset(0f, trackHeightCenter),
                        end = Offset(trackWidthPx, trackHeightCenter),
                        strokeWidth = 5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    val activeStartPx = valueToPx(rangeSliderState.activeRangeStart.roundToInt())
                    val activeEndPx = valueToPx(rangeSliderState.activeRangeEnd.roundToInt())

                    drawLine(
                        color = activeTrackColor,
                        start = Offset(activeStartPx, trackHeightCenter),
                        end = Offset(activeEndPx, trackHeightCenter),
                        strokeWidth = 5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    keyValues.forEach { value ->
                        val position = valueToPx(value)
                        val valueAsFloat = value.toFloat()
                        val color = if (valueAsFloat >= rangeSliderState.activeRangeStart &&
                            valueAsFloat <= rangeSliderState.activeRangeEnd
                        )
                            activeTrackColor else sliderColors.inactiveTrackColor

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
