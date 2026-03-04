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

    private val config = OcrConfig().apply {
        isRunDet = true
        isRunCls = true
        isRunRec = true
    }

    suspend fun extractTextFromUri(uri: Uri): String? = withContext(Dispatchers.IO) {
        val ocr = OCR(context)
        
        return@withContext try {
            val initResult = ocr.initModelSync(config)
            
            if (initResult.isFailure || initResult.getOrNull() == false) {
                return@withContext "Ошибка: Не удалось инициализировать PaddleOCR. ${initResult.exceptionOrNull()?.message}"
            }

            val bitmap = uriToBitmap(uri) ?: return@withContext "Ошибка: Не удалось прочитать картинку"

            val result = ocr.runSync(bitmap)

            result.simpleText
        } catch (e: Exception) {
            e.printStackTrace()
            "Ошибка во время распознавания: ${e.message}"
        } finally {
            ocr.releaseModel()
        }
    }

    @Suppress("DEPRECATION")
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    // Выделяем память в программной области, так как C++ (NDK) 
                    // часто не умеет читать напрямую из аппаратной памяти видеоускорителя (Hardware Bitmap)
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