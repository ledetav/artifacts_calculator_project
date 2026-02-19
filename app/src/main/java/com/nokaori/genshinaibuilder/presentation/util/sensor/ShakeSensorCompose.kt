package com.nokaori.genshinaibuilder.presentation.util.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberShakeSensor(onShake: () -> Unit) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val shakeDetector = remember {
        ShakeDetector(onShake = onShake)
    }

    DisposableEffect(sensorManager) {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        if (accelerometer != null) {
            sensorManager.registerListener(
                shakeDetector,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        onDispose {
            sensorManager.unregisterListener(shakeDetector)
        }
    }
}