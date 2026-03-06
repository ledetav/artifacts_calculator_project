package com.nokaori.genshinaibuilder.domain.util

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArtifactTextRecognizer(private val context: Context) {

    // Загружаем нашу скомпилированную C++ библиотеку
    init {
        System.loadLibrary("genshin_ocr")
    }

    // Объявляем нативные методы, реализация которых лежит в ocr_jni.cpp
    private external fun initModel(assetManager: AssetManager): Boolean
    private external fun recognizeText(bitmap: Bitmap): String

    private var isInitialized = false

    suspend fun extractTextFromUri(uri: Uri): String? = withContext(Dispatchers.IO) {
        if (!isInitialized) {
            isInitialized = initModel(context.assets)
            if (!isInitialized) return@withContext "Ошибка: Не удалось инициализировать OCR"
        }

        val bitmap = uriToBitmap(uri) ?: return@withContext "Ошибка: Не удалось прочитать картинку"
        return@withContext recognizeText(bitmap)
    }

    @Suppress("DEPRECATION")
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true // Bitmap должен быть изменяемым для C++
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