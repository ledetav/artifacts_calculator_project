package com.nokaori.genshinaibuilder.domain.util

import android.content.Context
import android.net.Uri
import android.graphics.BitmapFactory
import com.equationl.paddleocr.PaddleOCR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArtifactTextRecognizer(private val context: Context) {

    private val paddleOCR = PaddleOCR()

    suspend fun extractTextFromUri(uri: Uri): String? {
        return withContext(Dispatchers.Default) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    val result = paddleOCR.getOCRResult(bitmap)
                    bitmap.recycle()
                    result.joinToString("\n") { it.text }
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
