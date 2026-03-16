package com.nokaori.genshinaibuilder.presentation.ui.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Ключ - ID тега (например, "N11280001"), Значение - Описание
typealias TagDictionary = Map<String, String>

@Composable
fun TaggedText(
    text: String,
    elementColor: Color,
    tagDictionary: TagDictionary,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogDescription by remember { mutableStateOf("") }

    // Регулярное выражение для поиска тегов вида {LINK#N11280001}Текст{/LINK}
    val linkRegex = "\\{LINK#([^}]*)\\}(.*?)\\{/LINK\\}".toRegex()

    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        val matches = linkRegex.findAll(text)

        for (match in matches) {
            // Добавляем обычный текст до совпадения
            append(text.substring(lastIndex, match.range.first))

            val tagId = match.groupValues[1]
            val tagText = match.groupValues[2]

            // Начало аннотации для ссылки с передачей tagText
            pushStringAnnotation(tag = "TAG_LINK", annotation = "$tagId|$tagText")
            
            // Настраиваем стиль для текста ссылки
            pushStyle(
                SpanStyle(
                    color = elementColor,
                    textDecoration = TextDecoration.Underline
                )
            )
            
            // Добавляем сам текст ссылки, который будет видеть пользователь
            append(tagText)
            
            pop() // Закрываем SpanStyle
            pop() // Закрываем StringAnnotation

            lastIndex = match.range.last + 1
        }
        // Добавляем оставшийся хвост текста
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = style.copy(color = textColor),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "TAG_LINK", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val (tagId, tagText) = annotation.item.split("|")
                    dialogTitle = tagText
                    dialogDescription = tagDictionary[tagId] ?: "Описание отсутствует (ID: $tagId)"
                    showDialog = true
                }
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Информация", color = elementColor) // Заглушка, позже вынести в стринги
            },
            text = {
                Text(text = dialogDescription, fontSize = 14.sp)
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Закрыть") // Заглушка, позже вынести в стринги
                }
            }
        )
    }
}