package com.nokaori.genshinaibuilder.presentation.util.sensor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
fun DoubleTapSensorEffect(
    enabled: Boolean = true,
    onDoubleTap: () -> Unit
) {
    val context = LocalContext.current
    
    // Гарантируем, что всегда вызывается актуальная лямбда onDoubleTap
    val currentOnDoubleTap by rememberUpdatedState(onDoubleTap)
    
    val detector = remember(context) { 
        DoubleTapDetector(context) { currentOnDoubleTap() } 
    }

    DisposableEffect(enabled) {
        if (enabled) {
            detector.start()
        }
        
        onDispose {
            detector.stop()
        }
    }
}