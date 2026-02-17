package com.nokaori.genshinaibuilder.presentation.util.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    private val onShake: () -> Unit
) : SensorEventListener {

    // Порог ускорения (в G). 1.5G - оптимальный порог для обнаружения встряхивания
    private val SHAKE_THRESHOLD_GRAVITY = 1.5F
    
    // Минимальное время между отдельными встряхиваниями (миллисекунды), 
    // чтобы предотвратить множественные срабатывания за один раз
    private val SHAKE_SLOP_TIME_MS = 500
    
    // Время до сброса счетчика встряхиваний
    private val SHAKE_COUNT_RESET_TIME_MS = 3000

    private var shakeTimestamp: Long = 0
    private var shakeCount: Int = 0

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // ???
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                
                if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }

                if (shakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    shakeCount = 0
                }

                shakeTimestamp = now
                shakeCount++

                if (shakeCount >= 1) { 
                    onShake()
                    shakeCount = 0
                }
            }
        }
    }
}