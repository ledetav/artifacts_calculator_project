#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <string>

#define TAG "GenshinOcrCpp"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

extern "C" {

// Метод для инициализации (позже здесь мы будем загружать модели в ncnn)
JNIEXPORT jboolean JNICALL
Java_com_nokaori_genshinaibuilder_domain_util_ArtifactTextRecognizer_initModel(JNIEnv *env, jobject thiz, jobject assetManager) {
    LOGD("Init model called from Kotlin!");
    return JNI_TRUE;
}

// Метод для распознавания текста
JNIEXPORT jstring JNICALL
Java_com_nokaori_genshinaibuilder_domain_util_ArtifactTextRecognizer_recognizeText(JNIEnv *env, jobject thiz, jobject bitmap) {
    LOGD("Recognize text called from Kotlin!");
    
    // Пока что возвращаем заглушку, чтобы проверить, что JNI работает
    std::string testResult = "Привет из C++! Связь установлена.";
    return env->NewStringUTF(testResult.c_str());
}

}