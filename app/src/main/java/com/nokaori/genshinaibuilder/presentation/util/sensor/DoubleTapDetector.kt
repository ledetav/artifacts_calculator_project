package com.nokaori.genshinaibuilder.presentation.util.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class DoubleTapDetector(
    context: Context,
    private val onDoubleTap: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    // Линейный акселерометр очищен от гравитации, реагирует только на физическое ускорение устройства
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    private var lastTapTime: Long = 0
    private var tapCount: Int = 0

    companion object {
        private const val TAP_THRESHOLD = 3.5f 
        private const val MIN_TIME_BETWEEN_TAPS = 150L 
        private const val MAX_TIME_BETWEEN_TAPS = 600L 
    }

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            if (acceleration > TAP_THRESHOLD) {
                val now = System.currentTimeMillis()

                if (now - lastTapTime > MAX_TIME_BETWEEN_TAPS) {
                    tapCount = 1
                    lastTapTime = now
                } else if (now - lastTapTime > MIN_TIME_BETWEEN_TAPS) {
                    tapCount++
                    lastTapTime = now

                    if (tapCount == 2) {
                        onDoubleTap()
                        tapCount = 0
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Не используется
    }
}