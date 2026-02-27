package com.nokaori.genshinaibuilder.presentation.util.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.presentation.viewmodel.GestureSettingsViewModel

@Composable
fun rememberTiltSensor(
    currentRoute: String?,
    topLevelRoutes: List<String>,
    viewModel: GestureSettingsViewModel = hiltViewModel(),
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Сохраняем актуальные коллбеки
    val currentOnSwipeLeft by rememberUpdatedState(onSwipeLeft)
    val currentOnSwipeRight by rememberUpdatedState(onSwipeRight)

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val tiltDetector = remember {
        TiltDetector(
            onSwipeLeft = { currentOnSwipeLeft() }, 
            onSwipeRight = { currentOnSwipeRight() }
        )
    }

    // Обновляем чувствительность при изменении в настройках
    LaunchedEffect(settings.tiltSensitivity) {
        tiltDetector.tiltThresholdDegrees = settings.tiltSensitivity
    }

    // Жест работает только если он включен в настройках И мы на главном экране
    val isEnabled = settings.isTiltEnabled && (currentRoute in topLevelRoutes)

    DisposableEffect(isEnabled) {
        if (isEnabled) {
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            accelerometer?.let {
                sensorManager.registerListener(tiltDetector, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }

        onDispose {
            sensorManager.unregisterListener(tiltDetector)
        }
    }
}