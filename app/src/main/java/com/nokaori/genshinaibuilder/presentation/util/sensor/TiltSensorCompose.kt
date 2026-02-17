package com.nokaori.genshinaibuilder.presentation.util.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberTiltSensor(
    currentRoute: String?,
    topLevelRoutes: List<String>,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val tiltDetector = remember(currentRoute) {
        TiltDetector(onSwipeLeft = onSwipeLeft, onSwipeRight = onSwipeRight)
    }

    DisposableEffect(currentRoute) {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        
        if (currentRoute in topLevelRoutes) {
            accelerometer?.let {
                sensorManager.registerListener(tiltDetector, it, SensorManager.SENSOR_DELAY_GAME)
            }
            gyroscope?.let {
                sensorManager.registerListener(tiltDetector, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }

        onDispose {
            sensorManager.unregisterListener(tiltDetector)
        }
    }
}