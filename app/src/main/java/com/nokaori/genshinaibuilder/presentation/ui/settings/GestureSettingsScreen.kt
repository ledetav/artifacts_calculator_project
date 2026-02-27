package com.nokaori.genshinaibuilder.presentation.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.presentation.viewmodel.GestureSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestureSettingsScreen(
    viewModel: GestureSettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    // Состояния для отображения диалогов настройки (реализуем в Блоке 5)
    var showShakeCalibration by remember { mutableStateOf(false) }
    var showDoubleTapCalibration by remember { mutableStateOf(false) }
    var showTiltCalibration by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки жестов") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            GestureSettingItem(
                title = "Встряхивание",
                description = "Используется для быстрого сброса фильтров или других действий.",
                isChecked = settings.isShakeEnabled,
                onCheckedChange = { viewModel.setShakeEnabled(it) },
                onCalibrateClick = { showShakeCalibration = true }
            )
            
            HorizontalDivider()

            GestureSettingItem(
                title = "Двойной тап",
                description = "Двойное постукивание по устройству.",
                isChecked = settings.isDoubleTapEnabled,
                onCheckedChange = { viewModel.setDoubleTapEnabled(it) },
                onCalibrateClick = { showDoubleTapCalibration = true }
            )
            
            HorizontalDivider()

            GestureSettingItem(
                title = "Наклон устройства",
                description = "Наклон влево или вправо для переключения вкладок.",
                isChecked = settings.isTiltEnabled,
                onCheckedChange = { viewModel.setTiltEnabled(it) },
                onCalibrateClick = { showTiltCalibration = true }
            )
        }

        // Вызов диалогов 
        if (showShakeCalibration) {
            ShakeCalibrationDialog(
                currentSensitivity = settings.shakeSensitivity,
                onDismiss = { showShakeCalibration = false },
                onSave = { viewModel.setShakeSensitivity(it) }
            )
        }
        
        if (showDoubleTapCalibration) {
            DoubleTapCalibrationDialog(
                currentSensitivity = settings.doubleTapSensitivity,
                onDismiss = { showDoubleTapCalibration = false },
                onSave = { viewModel.setDoubleTapSensitivity(it) }
            )
        }

        if (showTiltCalibration) {
            TiltCalibrationDialog(
                currentSensitivity = settings.tiltSensitivity,
                onDismiss = { showTiltCalibration = false },
                onSave = { viewModel.setTiltSensitivity(it) }
            )
        }
    }
}

@Composable
fun GestureSettingItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onCalibrateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description, 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Показываем кнопку калибровки только если жест включен
        if (isChecked) {
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(
                onClick = onCalibrateClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Настроить чувствительность")
            }
        }
    }
}