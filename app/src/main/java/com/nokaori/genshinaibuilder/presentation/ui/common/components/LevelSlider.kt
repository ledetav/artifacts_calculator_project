package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSlider(
    value: Int,
    maxLevel: Int,
    onValueChange: (Int) -> Unit
) {
    val keyPoints = (0..maxLevel step 4).toList()
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Lv. $value", style = MaterialTheme.typography.labelLarge)
            Text("Max $maxLevel", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = 0f..maxLevel.toFloat(),
            steps = if (maxLevel > 0) maxLevel - 1 else 0,
            thumb = {
                SliderThumb() // Наш компонент из common
            },
            track = { sliderState ->
                // Рисуем трек с точками
                Canvas(modifier = Modifier.fillMaxWidth().height(10.dp)) { // Высота Canvas
                    val trackHeight = 4.dp.toPx()
                    val pointRadius = 4.dp.toPx()
                    val centerY = size.height / 2
                    val width = size.width

                    // Нормализуем значение (0..1)
                    val fraction = (sliderState.value - sliderState.valueRange.start) /
                            (sliderState.valueRange.endInclusive - sliderState.valueRange.start)

                    val activeWidth = width * fraction

                    // 1. Неактивная линия (серый фон)
                    drawLine(
                        color = surfaceVariantColor,
                        start = Offset(0f, centerY),
                        end = Offset(width, centerY),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round
                    )

                    // 2. Активная линия (цветная)
                    drawLine(
                        color = primaryColor,
                        start = Offset(0f, centerY),
                        end = Offset(activeWidth, centerY),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round
                    )

                    // 3. Точки (0, 4, 8, 12, 16, 20)
                    keyPoints.forEach { levelPoint ->
                        val pointFraction = levelPoint / maxLevel.toFloat()
                        val pointX = width * pointFraction

                        // Если точка уже пройдена ползунком - она активная
                        val isReached = levelPoint <= value

                        drawCircle(
                            color = if(isReached) primaryColor else surfaceVariantColor,
                            radius = pointRadius,
                            center = Offset(pointX, centerY)
                        )
                    }
                }
            }
        )
    }
}