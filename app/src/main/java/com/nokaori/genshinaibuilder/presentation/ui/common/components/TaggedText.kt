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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nokaori.genshinaibuilder.R

// Ключ - ID тега (например, "N11280001"), Значение - Описание
typealias TagDictionary = Map<String, String>

@Composable
fun TaggedText(
    text: String,
    elementColor: Color,
    tagDictionary: TagDictionary,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogDescription by remember { mutableStateOf("") }

    fun formatDialogText(rawText: String, elementColor: Color): AnnotatedString = buildAnnotatedString {
        // Обрабатываем <color ...>текст</color> - делаем жирным
        val colorRegex = "<color[^>]*>(.*?)</color>".toRegex()
        // Обрабатываем {LINK#S...}текст{/LINK} - делаем цветным
        val skipLinkRegex = "\\{LINK#S[^}]*\\}(.*?)\\{/LINK\\}".toRegex()
        
        var text = rawText
        // Заменяем экранированные переносы на реальные
        text = text.replace("\\n", "\n")
        // Заменяем <color>текст</color> на маркер для жирного
        text = text.replace(colorRegex) { "<BOLD>${it.groupValues[1]}</BOLD>" }
        // Заменяем {LINK#S}текст{/LINK} на маркер для цвета
        text = text.replace(skipLinkRegex) { "<COLOR>${it.groupValues[1]}</COLOR>" }
        
        val allMarkersRegex = "<BOLD>(.*?)</BOLD>|<COLOR>(.*?)</COLOR>".toRegex()
        var lastIndex = 0
        
        for (match in allMarkersRegex.findAll(text)) {
            append(text.substring(lastIndex, match.range.first))
            
            if (match.groupValues[1].isNotEmpty()) {
                // Это BOLD
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append(match.groupValues[1])
                pop()
            } else {
                // Это COLOR
                pushStyle(SpanStyle(color = elementColor))
                append(match.groupValues[2])
                pop()
            }
            
            lastIndex = match.range.last + 1
        }
        append(text.substring(lastIndex))
    }

    // Регулярное выражение для поиска тегов вида {LINK#N11280001}Текст{/LINK}
    val linkRegex = "\\{LINK#([^}]*)\\}(.*?)\\{/LINK\\}".toRegex()
    // Регулярное выражение для удаления тегов вида {LINK#S11142}Текст{/LINK}
    val skipTagRegex = "\\{LINK#S[^}]*\\}(.*?)\\{/LINK\\}".toRegex()

    val annotatedString = buildAnnotatedString {
        // Сначала удаляем теги с S вместе с их содержимым и закрывающим тегом
        var processedText = text.replace(skipTagRegex, "$1")
        var lastIndex = 0
        val matches = linkRegex.findAll(processedText)

        for (match in matches) {
            // Добавляем обычный текст до совпадения
            append(processedText.substring(lastIndex, match.range.first))

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
        if (lastIndex < processedText.length) {
            append(processedText.substring(lastIndex))
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = style.copy(color = textColor),
        maxLines = maxLines,
        overflow = overflow,
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
                Text(text = stringResource(R.string.dialog_info_title), color = elementColor)
            },
            text = {
                Text(
                    text = formatDialogText(dialogDescription, elementColor),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.dialog_close_btn))
                }
            }
        )
    }
}
