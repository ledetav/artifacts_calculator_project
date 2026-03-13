package com.nokaori.genshinaibuilder.presentation.util.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

class SteadyDetector(
    private val threshold: Float = 0.5f, // Порог ускорения (м/с^2). Чем меньше, тем строже проверка
    private val steadyDurationMillis: Long = 1200L, // Время неподвижности до срабатывания (1.2 сек)
    private val onSteady: () -> Unit
) : SensorEventListener {

    private var steadyStartTime = 0L
    private var isSteady = false
    private var hasTriggered = false
    private var gravity = FloatArray(3)

    fun start() {
        hasTriggered = false
        isSteady = false
        steadyStartTime = System.currentTimeMillis()
    }

    fun stop() {
        hasTriggered = true
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || hasTriggered) return

        var linearX = event.values[0]
        var linearY = event.values[1]
        var linearZ = event.values[2]

        // Если это обычный акселерометр, нам нужно "отфильтровать" гравитацию (9.8 м/с^2)
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val alpha = 0.8f
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

            linearX = event.values[0] - gravity[0]
            linearY = event.values[1] - gravity[1]
            linearZ = event.values[2] - gravity[2]
        }

        // Вычисляем итоговое линейное ускорение (векторную сумму)
        val acceleration = sqrt((linearX * linearX + linearY * linearY + linearZ * linearZ).toDouble()).toFloat()

        if (acceleration > threshold) {
            // Устройство движется, сбрасываем таймер
            isSteady = false
            steadyStartTime = System.currentTimeMillis()
        } else {
            // Устройство неподвижно
            if (!isSteady) {
                isSteady = true
                steadyStartTime = System.currentTimeMillis()
            } else {
                val steadyDuration = System.currentTimeMillis() - steadyStartTime
                if (steadyDuration >= steadyDurationMillis) {
                    hasTriggered = true // Гарантируем, что сработает только 1 раз
                    onSteady()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Не используется
    }
}

@Composable
fun SteadySensorEffect(
    isActive: Boolean = true,
    onSteady: () -> Unit
) {
    val context = LocalContext.current
    val currentOnSteady = rememberUpdatedState(onSteady)
    
    val sensorManager = remember { 
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager 
    }
    
    val detector = remember { 
        SteadyDetector { currentOnSteady.value() } 
    }

    DisposableEffect(isActive, sensorManager) {
        // Приоритетно используем датчик линейного ускорения (в нем уже вырезана гравитация на аппаратном уровне), 
        // но оставляем fallback до обычного акселерометра, если первого нет.
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) 
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (isActive && sensor != null) {
            detector.start()
            sensorManager.registerListener(detector, sensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            detector.stop()
            sensorManager.unregisterListener(detector)
        }
    }
}