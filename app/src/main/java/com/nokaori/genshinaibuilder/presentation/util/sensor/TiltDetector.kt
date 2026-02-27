package com.nokaori.genshinaibuilder.presentation.util.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs
import kotlin.math.asin

class TiltDetector(
    private val onSwipeLeft: () -> Unit,
    private val onSwipeRight: () -> Unit
) : SensorEventListener {

    var tiltThresholdDegrees: Float = 45.0f

    private val COOLDOWN_MS = 800L 
    private var lastTiltTime = 0L

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Проверяем, что телефон держат более-менее вертикально, а не лежит на столе
            val isUpright = y > 3.0f && abs(z) < 8.0f
            if (!isUpright) return

            // Вычисляем угол наклона по оси X в градусах
            val normalizedX = (x / SensorManager.GRAVITY_EARTH).coerceIn(-1f, 1f)
            val angle = Math.toDegrees(asin(normalizedX.toDouble())).toFloat()

            if (abs(angle) > tiltThresholdDegrees) {
                val now = System.currentTimeMillis()
                
                if (now - lastTiltTime > COOLDOWN_MS) {
                    lastTiltTime = now

                    // Если x положительный — наклон влево, отрицательный — вправо
                    if (angle > 0) {
                        onSwipeLeft()
                    } else {
                        onSwipeRight()
                    }
                }
            }
        }
    }
}