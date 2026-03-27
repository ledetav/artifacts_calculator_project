package com.nokaori.genshinaibuilder.presentation.ui.settings.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import com.nokaori.genshinaibuilder.presentation.ui.common.components.SliderThumb
import com.nokaori.genshinaibuilder.R
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseCalibrationDialog(
    title: String,
    currentValue: Float,
    defaultValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    isGestureDetected: Boolean,
    unit: String = ""
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = stringResource(R.string.calibration_instructions),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.calibration_sensitivity_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Индикатор распознавания
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGestureDetected) {
                        Text(
                            text = stringResource(R.string.calibration_gesture_detected),
                            color = Color(0xFF4CAF50), // Зеленый цвет
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = currentValue.coerceIn(valueRange),
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    thumb = { SliderThumb() }
                )
                
                Text(
                    text = stringResource(R.string.calibration_current_threshold, currentValue, unit),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(
                    onClick = { onValueChange(defaultValue) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(stringResource(R.string.calibration_reset_default, defaultValue, unit))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.calibration_done))
                }
            }
        }
    }
}

@Composable
fun ShakeCalibrationDialog(
    currentSensitivity: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var isDetected by remember { mutableStateOf(false) }
    val currentThreshold by rememberUpdatedState(currentSensitivity)
    val context = LocalContext.current

    // Скрываем надпись "Жест распознан!" через 1.5 секунды
    LaunchedEffect(isDetected) {
        if (isDetected) {
            delay(1500)
            isDetected = false
        }
    }

    // Слушатель сенсора специально для этого экрана настройки
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        val listener = object : SensorEventListener {
            private var lastShakeTime = 0L

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val gX = event.values[0] / SensorManager.GRAVITY_EARTH
                val gY = event.values[1] / SensorManager.GRAVITY_EARTH
                val gZ = event.values[2] / SensorManager.GRAVITY_EARTH

                val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

                // Используем актуальное значение ползунка
                if (gForce > currentThreshold) {
                    val now = System.currentTimeMillis()
                    if (now - lastShakeTime > 500) {
                        isDetected = true
                        lastShakeTime = now
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    BaseCalibrationDialog(
        title = stringResource(R.string.gesture_shake_title),
        currentValue = currentSensitivity,
        defaultValue = 1.5f,
        valueRange = 1.1f..4.0f,
        onValueChange = onSave,
        onDismiss = onDismiss,
        isGestureDetected = isDetected,
        unit = "G"
    )
}

@Composable
fun DoubleTapCalibrationDialog(
    currentSensitivity: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var isDetected by remember { mutableStateOf(false) }
    val currentThreshold by rememberUpdatedState(currentSensitivity)
    val context = LocalContext.current

    LaunchedEffect(isDetected) {
        if (isDetected) {
            delay(1500)
            isDetected = false
        }
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        
        val listener = object : SensorEventListener {
            private var lastTapTime = 0L
            private var tapCount = 0

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                if (acceleration > currentThreshold) {
                    val now = System.currentTimeMillis()
                    if (now - lastTapTime > 600) {
                        tapCount = 1
                        lastTapTime = now
                    } else if (now - lastTapTime > 150) {
                        tapCount++
                        lastTapTime = now
                        if (tapCount == 2) {
                            isDetected = true
                            tapCount = 0
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
        
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    BaseCalibrationDialog(
        title = stringResource(R.string.gesture_double_tap_title),
        currentValue = currentSensitivity,
        defaultValue = 3.5f,
        valueRange = 1.0f..8.0f,
        onValueChange = onSave,
        onDismiss = onDismiss,
        isGestureDetected = isDetected,
        unit = "м/с²"
    )
}

@Composable
fun TiltCalibrationDialog(
    currentSensitivity: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var isDetected by remember { mutableStateOf(false) }
    val currentThreshold by rememberUpdatedState(currentSensitivity)
    val context = LocalContext.current

    LaunchedEffect(isDetected) {
        if (isDetected) {
            delay(1500)
            isDetected = false
        }
    }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        val listener = object : SensorEventListener {
            private var lastTiltTime = 0L

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val x = event.values[0]
                
                // Расчет угла наклона (грубо, по оси X)
                val normalizedX = (x / SensorManager.GRAVITY_EARTH).coerceIn(-1f, 1f)
                val angle = Math.toDegrees(asin(normalizedX.toDouble())).toFloat()

                if (abs(angle) > currentThreshold) {
                    val now = System.currentTimeMillis()
                    if (now - lastTiltTime > 1000) {
                        isDetected = true
                        lastTiltTime = now
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    BaseCalibrationDialog(
        title = stringResource(R.string.gesture_tilt_title),
        currentValue = currentSensitivity,
        defaultValue = 45.0f,
        valueRange = 15f..75f,
        onValueChange = onSave,
        onDismiss = onDismiss,
        isGestureDetected = isDetected,
        unit = "°"
    )
}