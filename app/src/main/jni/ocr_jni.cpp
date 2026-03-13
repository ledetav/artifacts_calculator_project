#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <string>
#include <vector>

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

// Подключаем нейросеть и зашитый словарь
#include "ppocrv5.h"
#include "ppocrv5_dict.h"

#include <stdint.h>

#define TAG "GenshinOcrCpp"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

static PPOCRv5* ppocr = nullptr;

extern "C" {

void __kmpc_dispatch_deinit(void* loc_ref, int32_t gtid) {
}

JNIEXPORT jboolean JNICALL
Java_com_nokaori_genshinaibuilder_domain_util_ArtifactTextRecognizer_initModel(JNIEnv *env, jobject thiz, jobject assetManager) {
    LOGD("Initializing PP-OCRv5 model...");
    if (ppocr == nullptr) {
        ppocr = new PPOCRv5();
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);

    // Настройки для серверной модели
    bool use_fp16 = false; // FP16 для server модели иногда дает NaN, поэтому false
    bool use_gpu = true;   // Используем Vulkan для ускорения

    // Вызываем load с правильными параметрами
    int ret = ppocr->load(
        mgr, 
        "PP_OCRv5_server_det.ncnn.param", 
        "PP_OCRv5_server_det.ncnn.bin", 
        "PP_OCRv5_server_rec.ncnn.param", 
        "PP_OCRv5_server_rec.ncnn.bin", 
        use_fp16, 
        use_gpu
    );

    // Оптимальный размер картинки для детекта
    ppocr->set_target_size(640);

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

    // Конвертируем Android Bitmap в OpenCV BGR
    cv::Mat rgb(info.height, info.width, CV_8UC4, indata);
    cv::Mat bgr;
    cv::cvtColor(rgb, bgr, cv::COLOR_RGBA2BGR);

    AndroidBitmap_unlockPixels(env, bitmap);

    // Вызываем детекцию
    std::vector<Object> objects;
    ppocr->detect_and_recognize(bgr, objects);

    // Собираем результаты в одну строку, используя словарь
    std::string full_text = "";
    for(size_t i = 0; i < objects.size(); i++) {
        std::string line_text;
        // Переводим ID символов в реальный текст из словаря
        for (const auto& character : objects[i].text) {
            if (character.id >= 0 && character.id < character_dict_size) {
                line_text += character_dict[character.id];
            }
        }
        full_text += line_text + "\n";
    }

    return env->NewStringUTF(full_text.c_str());
}

}
