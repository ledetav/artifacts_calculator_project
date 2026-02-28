package com.nokaori.genshinaibuilder.domain.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.equationl.paddleocr4android.OCR
import com.equationl.paddleocr4android.bean.OcrConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArtifactTextRecognizer(private val context: Context) {

    // Инициализируем конфиг PaddleOCR. 
    // По умолчанию обертка сама использует зашитые в нее легковесные модели PP-OCRv4
    private val config = OcrConfig().apply {
        // Здесь можно будет покрутить настройки, если на сложном фоне будет плохо читать
        isRunDet = true
        isRunCls = true
        isRunRec = true
    }

    suspend fun extractTextFromUri(uri: Uri): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val ocr = OCR.getInstance(context)
            val initResult = ocr.initEngine(config)
            
            if (!initResult) {
                return@withContext "Ошибка: Не удалось инициализировать PaddleOCR"
            }

            // 2. Превращаем Uri в Bitmap
            val bitmap = uriToBitmap(uri) ?: return@withContext "Ошибка: Не удалось получить картинку"

            // работает синхронно, поэтому мы внутри Dispatchers.IO
            val result = ocr.runSync(bitmap)

            // Освобождаем память (важно для C++ библиотек!)
            ocr.releaseEngine()

            result.simpleText
        } catch (e: Exception) {
            e.printStackTrace()
            "Ошибка при распознавании: ${e.message}"
        }
    }

    @Suppress("DEPRECATION")
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                // Обязательно копируем в ARGB_8888, так как C++ модели обычно требуют этот формат
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }.copy(Bitmap.Config.ARGB_8888, true)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}