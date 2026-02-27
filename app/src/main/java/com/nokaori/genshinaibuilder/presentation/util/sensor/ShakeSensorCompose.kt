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
fun rememberShakeSensor(
    viewModel: GestureSettingsViewModel = hiltViewModel(),
    onShake: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Сохраняем актуальный коллбек, чтобы не пересоздавать детектор при его изменении
    val currentOnShake by rememberUpdatedState(onShake)

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val shakeDetector = remember {
        ShakeDetector(onShake = { currentOnShake() })
    }

    // Обновляем порог чувствительности внутри детектора при изменении настроек
    LaunchedEffect(settings.shakeSensitivity) {
        shakeDetector.thresholdGravity = settings.shakeSensitivity
    }

    // Подписываемся на датчик только если жест включен в настройках
    DisposableEffect(sensorManager, settings.isShakeEnabled) {
        if (settings.isShakeEnabled) {
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accelerometer != null) {
                sensorManager.registerListener(
                    shakeDetector,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }

        onDispose {
            sensorManager.unregisterListener(shakeDetector)
        }
    }
}