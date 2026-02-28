package com.nokaori.genshinaibuilder.domain.util

import android.content.Context
import android.net.Uri
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.text.MLTextAnalyzer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ArtifactTextRecognizer(private val context: Context) {

    // Инициализируем локальный распознаватель Huawei
    private val analyzer: MLTextAnalyzer = MLAnalyzerFactory.getInstance().localTextAnalyzer

    suspend fun extractTextFromUri(uri: Uri): String? {
        return try {
            // Превращаем Uri в MLFrame, понятный для Huawei
            val frame = MLFrame.fromFilePath(context, uri)
            
            // Запускаем анализ и ждем результат через нашу корутину
            val mlText = analyzer.asyncAnalyseFrame(frame).awaitHuawei()
            
            // Возвращаем всю простыню распознанного текста
            mlText.stringValue
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            // Обязательно освобождаем память
            analyzer.stop()
        }
    }

    // Extension-функция для перевода Huawei Task в Kotlin Coroutine
    private suspend fun <T> com.huawei.hmf.tasks.Task<T>.awaitHuawei(): T = suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result ->
            if (result != null) {
                cont.resume(result)
            } else {
                cont.resumeWithException(Exception("ML Kit returned null result"))
            }
        }.addOnFailureListener { e ->
            cont.resumeWithException(e)
        }
    }
}