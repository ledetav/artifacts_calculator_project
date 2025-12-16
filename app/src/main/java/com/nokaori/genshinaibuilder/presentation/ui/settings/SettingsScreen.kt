package com.nokaori.genshinaibuilder.presentation.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel
) {
    val updateState by settingsViewModel.updateState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Настройки данных",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { settingsViewModel.updateDatabase() },
            enabled = updateState !is SettingsViewModel.UpdateState.Loading
        ) {
            Text(text = "Обновить базу персонажей")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = updateState) {
            is SettingsViewModel.UpdateState.Idle -> {
                Text("База данных готова к обновлению.", color = Color.Gray)
            }
            is SettingsViewModel.UpdateState.Loading -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Загрузка данных с Yatta...", color = MaterialTheme.colorScheme.primary)
            }
            is SettingsViewModel.UpdateState.Success -> {
                Text("✅ База данных обновлена!", color = Color(0xFF4CAF50))
            }
            is SettingsViewModel.UpdateState.CachingImages -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅ База обновлена!", color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Кэширование ${state.count} изображений...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "(Это происходит в фоне, можно выходить)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }
            }
            is SettingsViewModel.UpdateState.Error -> {
                // ВЫВОДИМ ОШИБКУ
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "❌ Ошибка обновления",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}