package com.nokaori.genshinaibuilder.domain.util

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.cyrillic.CyrillicTextRecognizerOptions
import kotlinx.coroutines.tasks.await

class ArtifactTextRecognizer(private val context: Context) {

    // Инициализируем распознаватель с поддержкой кириллицы (и латиницы)
    private val recognizer = TextRecognition.getClient(CyrillicTextRecognizerOptions.Builder().build())

    suspend fun extractTextFromUri(uri: Uri): String? {
        return try {
            // Превращаем Uri в InputImage, понятный для ML Kit
            val image = InputImage.fromFilePath(context, uri)
            
            // Запускаем ML Kit и ждем результат через корутины (await)
            val visionText = recognizer.process(image).await()
            
            // Возвращаем весь найденный текст единой строкой
            visionText.text
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}