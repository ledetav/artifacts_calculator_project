package com.nokaori.genshinaibuilder.domain.model

data class GestureSettings(
    val isShakeEnabled: Boolean = true,
    val shakeSensitivity: Float = 1.5f, // Дефолтное значение для встряхивания

    val isDoubleTapEnabled: Boolean = true,
    val doubleTapSensitivity: Float = 3.5f, // Порог силы m/s^2

    val isTiltEnabled: Boolean = true,
    val tiltSensitivity: Float = 45.0f // Дефолтное значение для наклона (в градусах)
) {
    companion object {
        const val DEFAULT_SHAKE_SENSITIVITY = 1.5f
        const val DEFAULT_DOUBLE_TAP_SENSITIVITY = 3.5f
        const val DEFAULT_TILT_SENSITIVITY = 45.0f
    }
}