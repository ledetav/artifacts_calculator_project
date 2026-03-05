cmake_minimum_required(VERSION 3.22.1)
project(genshin_ocr)

set(ncnn_DIR ${CMAKE_SOURCE_DIR}/ncnn/${ANDROID_ABI}/lib/cmake/ncnn)
set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/opencv/sdk/native/jni)

find_package(ncnn REQUIRED)
find_package(OpenCV REQUIRED core imgproc)
find_package(OpenMP REQUIRED)

add_library(genshin_ocr SHARED ocr_jni.cpp ppocrv5.cpp)

find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)

target_compile_options(genshin_ocr PRIVATE -fopenmp)
target_link_options(genshin_ocr PRIVATE -fopenmp)
target_link_libraries(genshin_ocr PRIVATE ncnn ${OpenCV_LIBS} ${log-lib} ${jnigraphics-lib})