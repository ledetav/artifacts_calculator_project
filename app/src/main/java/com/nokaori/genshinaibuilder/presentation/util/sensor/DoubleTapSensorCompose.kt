package com.nokaori.genshinaibuilder.presentation.util.sensor

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.presentation.viewmodel.GestureSettingsViewModel

@Composable
fun DoubleTapSensorEffect(
    enabled: Boolean = true,
    viewModel: GestureSettingsViewModel = hiltViewModel(),
    onDoubleTap: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Гарантируем, что всегда вызывается актуальная лямбда onDoubleTap
    val currentOnDoubleTap by rememberUpdatedState(onDoubleTap)
    
    val detector = remember(context) { 
        DoubleTapDetector(context) { currentOnDoubleTap() } 
    }

    // Обновляем порог чувствительности внутри детектора при изменении настроек
    LaunchedEffect(settings.doubleTapSensitivity) {
        detector.tapThreshold = settings.doubleTapSensitivity
    }

    // Датчик включается, только если он не выключен пользователем в настройках 
    // И разрешен на текущем экране (переменная enabled)
    val shouldBeEnabled = settings.isDoubleTapEnabled && enabled

    DisposableEffect(shouldBeEnabled) {
        if (shouldBeEnabled) {
            detector.start()
        }
        
        onDispose {
            detector.stop()
        }
    }
}