cmake_minimum_required(VERSION 3.22.1)
project(genshin_ocr)

# Устанавливаем пути к нашим библиотекам
set(ncnn_DIR ${CMAKE_SOURCE_DIR}/ncnn/${ANDROID_ABI}/lib/cmake/ncnn)
set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/opencv/sdk/native/jni)

# Подключаем ncnn
find_package(ncnn REQUIRED)

# Подключаем OpenCV (нам нужны только модули core и imgproc для обработки картинки)
find_package(OpenCV REQUIRED core imgproc)

# Указываем наш будущий C++ файл
add_library(genshin_ocr SHARED ocr_jni.cpp)

# Подключаем стандартные Android-библиотеки для работы с логами и Bitmap
find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)

# Связываем всё вместе
target_link_libraries(genshin_ocr ncnn ${OpenCV_LIBS} ${log-lib} ${jnigraphics-lib})