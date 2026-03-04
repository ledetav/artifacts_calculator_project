package com.nokaori.genshinaibuilder.domain.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.baidu.paddle.fastdeploy.LitePowerMode
import com.equationl.fastdeployocr.OCR
import com.equationl.fastdeployocr.OcrConfig
import com.equationl.fastdeployocr.RunType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArtifactTextRecognizer(private val context: Context) {

    private val config = OcrConfig().apply {
        modelPath = "models"
        
        detModelFileName = "det"
        recModelFileName = "rec"
        labelPath = "dict.txt" 
        
        // ВАЖНО: Используем WithDet вместо All. 
        // Это отключает классификатор переворота (cls), из-за которого текст часто читается "вверх ногами"
        runType = RunType.WithDet 
        
        cpuPowerMode = LitePowerMode.LITE_POWER_FULL
    }

    suspend fun extractTextFromUri(uri: Uri): String? = withContext(Dispatchers.IO) {
        val ocr = OCR(context)
        
        return@withContext try {
            val initResult = ocr.initModelSync(config)
            val isInitialized = initResult.getOrNull() ?: false
            
            if (!isInitialized) {
                val error = initResult.exceptionOrNull()?.message ?: "Unknown error"
                Log.e("ArtifactTextRecognizer", "Init failed: $error")
                return@withContext "Ошибка: Не удалось инициализировать PaddleOCR. $error"
            }

            val originalBitmap = uriToBitmap(uri) ?: return@withContext "Ошибка: Не удалось прочитать картинку"
            
            // Сжимаем скриншот для лучшего распознавания
            val scaledBitmap = scaleBitmapDown(originalBitmap, 1024)

            val runResult = ocr.runSync(scaledBitmap)
            val result = runResult.getOrNull() ?: run {
                val error = runResult.exceptionOrNull()?.message ?: "Unknown error"
                Log.e("ArtifactTextRecognizer", "Run failed: $error")
                return@withContext "Ошибка: Не удалось распознать текст. $error"
            }

            result.simpleText
        } catch (e: Exception) {
            e.printStackTrace()
            "Ошибка во время распознавания: ${e.message}"
        } finally {
            ocr.releaseModel()
        }
    }

    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var newWidth = originalWidth
        var newHeight = originalHeight

        if (originalWidth > maxDimension || originalHeight > maxDimension) {
            if (originalWidth > originalHeight) {
                newWidth = maxDimension
                newHeight = (newWidth * originalHeight) / originalWidth
            } else {
                newHeight = maxDimension
                newWidth = (newHeight * originalWidth) / originalHeight
            }
        }
        
        return if (newWidth != originalWidth || newHeight != originalHeight) {
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
    }

    @Suppress("DEPRECATION")
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
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
