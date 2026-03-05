#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <string>
#include <vector>

#include "ppocrv5.h"

#define TAG "GenshinOcrCpp"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

static PPOCRv5* ppocr = nullptr;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_nokaori_genshinaibuilder_domain_util_ArtifactTextRecognizer_initModel(JNIEnv *env, jobject thiz, jobject assetManager) {
    LOGD("Initializing PP-OCRv5 model...");
    if (ppocr == nullptr) {
        ppocr = new PPOCRv5();
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);

    // Загружаем модели. Выбираем server (они точнее для скриншотов). 
    bool has_gpu = true; // Используем Vulkan GPU для максимальной скорости!
    int ret = ppocr->load(mgr, "PP_OCRv5_server_det", "PP_OCRv5_server_rec", has_gpu); 

    if (ret == 0) { 
        LOGD("Model initialized successfully!");
        return JNI_TRUE;
    } else {
        LOGD("Failed to initialize model. Error code: %d", ret);
        return JNI_FALSE;
    }
}

JNIEXPORT jstring JNICALL
Java_com_nokaori_genshinaibuilder_domain_util_ArtifactTextRecognizer_recognizeText(JNIEnv *env, jobject thiz, jobject bitmap) {
    if (ppocr == nullptr) {
        return env->NewStringUTF("Ошибка: Модель не инициализирована");
    }

    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);
    
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return env->NewStringUTF("Ошибка: Неверный формат картинки");
    }

    void* indata;
    AndroidBitmap_lockPixels(env, bitmap, &indata);

    // Конвертируем Android Bitmap (RGBA) в OpenCV Mat (BGR)
    cv::Mat rgba(info.height, info.width, CV_8UC4, indata);
    cv::Mat bgr;
    cv::cvtColor(rgba, bgr, cv::COLOR_RGBA2BGR);

    AndroidBitmap_unlockPixels(env, bitmap);

    // Вызываем детекцию
    std::vector<PPOCRText> objects; 
    ppocr->detect(bgr, objects);

    // Собираем результаты в одну строку
    std::string full_text = "";
    for (size_t i = 0; i < objects.size(); i++) {
        full_text += objects[i].text + "\n";
    }

    return env->NewStringUTF(full_text.c_str());
}

}