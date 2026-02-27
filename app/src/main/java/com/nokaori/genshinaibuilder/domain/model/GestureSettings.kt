package com.nokaori.genshinaibuilder.domain.model

data class GestureSettings(
    val isShakeEnabled: Boolean = true,
    val shakeSensitivity: Float = 12.0f, // Дефолтное значение для встряхивания

    val isDoubleTapEnabled: Boolean = true,
    val doubleTapSensitivity: Float = 300f, // Например, время между тапами или порог силы

    val isTiltEnabled: Boolean = true,
    val tiltSensitivity: Float = 45.0f // Дефолтное значение для наклона (в градусах)
) {
    companion object {
        const val DEFAULT_SHAKE_SENSITIVITY = 12.0f
        const val DEFAULT_DOUBLE_TAP_SENSITIVITY = 300f
        const val DEFAULT_TILT_SENSITIVITY = 45.0f
    }
}