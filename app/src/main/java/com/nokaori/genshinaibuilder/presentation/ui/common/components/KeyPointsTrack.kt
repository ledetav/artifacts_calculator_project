package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyPointsTrack(
    rangeSliderState: RangeSliderState,
    valueRange: ClosedFloatingPointRange<Float>,
    keyValues: List<Int>,
    colors: SliderColors = SliderDefaults.colors(),
    trackWidth: Dp = 5.dp,
    keyPointRadius: Dp = 6.dp
) {
    Canvas(modifier = Modifier.fillMaxWidth()) {
        val trackWidthPx = size.width
        val trackHeightCenter = center.y
        val maxValue = valueRange.endInclusive

        fun valueToPx(value: Int): Float = (value / maxValue) * trackWidthPx

        drawLine(
            color = colors.inactiveTrackColor,
            start = Offset(0f, trackHeightCenter),
            end = Offset(trackWidthPx, trackHeightCenter),
            strokeWidth = trackWidth.toPx(),
            cap = StrokeCap.Round
        )

        val activeStartPx = valueToPx(rangeSliderState.activeRangeStart.roundToInt())
        val activeEndPx = valueToPx(rangeSliderState.activeRangeEnd.roundToInt())

        drawLine(
            color = colors.activeTrackColor,
            start = Offset(activeStartPx, trackHeightCenter),
            end = Offset(activeEndPx, trackHeightCenter),
            strokeWidth = trackWidth.toPx(),
            cap = StrokeCap.Round
        )

        keyValues.forEach { value ->
            val position = valueToPx(value)
            val valueAsFloat = value.toFloat()
            val color = if (valueAsFloat in rangeSliderState.activeRangeStart..rangeSliderState.activeRangeEnd)
                colors.activeTrackColor else colors.inactiveTrackColor

            drawCircle(
                color = color,
                center = Offset(x = position, y = center.y),
                radius = keyPointRadius.toPx()
            )
        }
    }
}
