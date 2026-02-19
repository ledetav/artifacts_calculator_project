package com.nokaori.genshinaibuilder.presentation.util.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.abs

class TiltDetector(
    private val onSwipeLeft: () -> Unit,
    private val onSwipeRight: () -> Unit
) : SensorEventListener {

    private val TILT_THRESHOLD = 3.0f 

    private val COOLDOWN_MS = 800L 
    
    private var lastTiltTime = 0L

    private var isUpright = false

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val y = event.values[1]
                val z = event.values[2]

                isUpright = y > 3.0f && abs(z) < 8.0f
            }
            
            Sensor.TYPE_GYROSCOPE -> {
                if (!isUpright) return

                val now = System.currentTimeMillis()
                if (now - lastTiltTime < COOLDOWN_MS) return

                val yRotation = event.values[1]

                if (abs(yRotation) > TILT_THRESHOLD) {
                    lastTiltTime = now

                    if (yRotation > 0) {
                        onSwipeLeft()
                    } else {
                        onSwipeRight()
                    }
                }
            }
        }
    }
}