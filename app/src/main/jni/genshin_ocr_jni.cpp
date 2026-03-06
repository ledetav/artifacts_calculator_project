#include <jni.h>
#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <string>
#include <vector>
#include <fstream>
#include <sstream>
#include <algorithm>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "ppocrv5_full.h"

#define TAG "GenshinOCR"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

static PPOCRv5* g_ocr = nullptr;

static std::vector<std::string> load_dict_from_asset(AAssetManager* mgr, const char* filename) {
    std::vector<std::string> dict;
    
    AAsset* asset = AAssetManager_open(mgr, filename, AASSET_MODE_BUFFER);
    if (!asset) {
        LOGE("Failed to open asset: %s", filename);
        return dict;
    }
    
    size_t size = AAsset_getLength(asset);
    char* buffer = new char[size + 1];
    AAsset_read(asset, buffer, size);
    buffer[size] = '\0';
    AAsset_close(asset);
    
    std::istringstream iss(buffer);
    std::string line;
    while (std::getline(iss, line)) {
        while (!line.empty() && (line.back() == '\r' || line.back() == '\n')) {
            line.pop_back();
        }
        if (!line.empty()) {
            dict.push_back(line);
        }
    }
    
    delete[] buffer;
    return dict;
}

// Вспомогательная функция для вычисления Y-центра RotatedRect
float get_box_center_y(const cv::RotatedRect& rrect) {
    return rrect.center.y;
}

extern "C" {

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved) {
    if (g_ocr != nullptr) {
        delete g_ocr;
        g_ocr = nullptr;
    }
}

JNIEXPORT jboolean JNICALL
Java_com_nokaori_genshinaibuilder_domain_util_ArtifactTextRecognizer_initModel(
    JNIEnv* env,
    jobject thiz,
    jobject asset_manager
) {
    if (g_ocr != nullptr) {
        delete g_ocr;
    }
    
    g_ocr = new PPOCRv5();
    
    AAssetManager* mgr = AAssetManager_fromJava(env, asset_manager);
    if (!mgr) {
        LOGE("Failed to get AssetManager");
        return JNI_FALSE;
    }
    
    std::vector<std::string> dict = load_dict_from_asset(mgr, "ppocrv5_eslav_dict.txt");
    if (dict.empty()) {
        LOGE("Failed to load dictionary");
        return JNI_FALSE;
    }
    
    int ret = g_ocr->load(
        mgr,
        "PP_OCRv5_mobile_det.ncnn.param",
        "PP_OCRv5_mobile_det.ncnn.bin",
        "eslav_ppocrv5_rec.ncnn.param",
        "eslav_ppocrv5_rec.ncnn.bin",
        true,
        false
    );
    
    if (ret != 0) {
        LOGE("Failed to load models: %d", ret);
        return JNI_FALSE;
    }
    
    g_ocr->set_dictionary(dict);
    g_ocr->set_target_size(1024);
    
    LOGI("OCR model initialized successfully");
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_com_nokaori_genshinaibuilder_domain_util_ArtifactTextRecognizer_recognizeText(
    JNIEnv* env,
    jobject thiz,
    jobject bitmap
) {
    if (g_ocr == nullptr) {
        LOGE("Model not initialized");
        return env->NewStringUTF("Error: Model not initialized");
    }
    
    AndroidBitmapInfo info;
    int ret = AndroidBitmap_getInfo(env, bitmap, &info);
    if (ret != ANDROID_BITMAP_RESULT_SUCCESS) {
        LOGE("AndroidBitmap_getInfo failed: %d", ret);
        return env->NewStringUTF("Error: Failed to get bitmap info");
    }
    
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format not RGBA_8888, got: %d", info.format);
        return env->NewStringUTF("Error: Invalid bitmap format");
    }
    
    void* pixels;
    ret = AndroidBitmap_lockPixels(env, bitmap, &pixels);
    if (ret != ANDROID_BITMAP_RESULT_SUCCESS) {
        LOGE("AndroidBitmap_lockPixels failed: %d", ret);
        return env->NewStringUTF("Error: Failed to lock bitmap");
    }
    
    cv::Mat rgba(info.height, info.width, CV_8UC4, pixels);
    cv::Mat rgb;
    cv::cvtColor(rgba, rgb, cv::COLOR_RGBA2RGB);
    
    std::vector<Object> objects;
    g_ocr->detect_and_recognize(rgb, objects);
    
    AndroidBitmap_unlockPixels(env, bitmap);

    std::sort(objects.begin(), objects.end(), [](const Object& a, const Object& b) {
        float yA = get_box_center_y(a.rrect); 
        float yB = get_box_center_y(b.rrect);

        if (std::abs(yA - yB) < 10.0f) {
             return a.rrect.center.x < b.rrect.center.x;
        }
        return yA < yB;
    });
    
    std::string result;
    for (size_t i = 0; i < objects.size(); i++) {
        const Object& obj = objects[i];
        
        std::string line;
        for (size_t j = 0; j < obj.text.size(); j++) {
            const Character& ch = obj.text[j];
            const std::string& char_str = g_ocr->get_char(ch.id);
            
            if (!char_str.empty()) {
                line += char_str;
            }
        }
        
        if (!line.empty()) {
            result += line;
            if (i + 1 < objects.size()) {
                result += "\n";
            }
        }
    }
    
    LOGI("Recognition complete, result length: %zu", result.length());
    return env->NewStringUTF(result.c_str());
}

} // extern "C"