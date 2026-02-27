package com.nokaori.genshinaibuilder.presentation.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.SyncStatus
import com.nokaori.genshinaibuilder.domain.model.UiText
import com.nokaori.genshinaibuilder.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateToGestures: () -> Unit
) {
    val syncStatus by settingsViewModel.syncStatus.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToGestures,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.settings_gesture_controls))
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.settings_app_data),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { settingsViewModel.updateDatabase() },
            enabled = syncStatus !is SyncStatus.InProgress,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.settings_update_database))
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val status = syncStatus) {
            is SyncStatus.Idle -> Text(stringResource(R.string.settings_status_waiting))

            is SyncStatus.InProgress -> {
                LinearProgressIndicator(
                    progress = { status.progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(status.message.asString(), style = MaterialTheme.typography.labelLarge)

                LogConsole(logs = status.logs)
            }

            is SyncStatus.Success -> {
                Text(status.summary.asString(), color = Color.Green, style = MaterialTheme.typography.titleMedium)
                LogConsole(logs = status.fullLogs)
            }

            is SyncStatus.Error -> {
                Text(stringResource(R.string.settings_error_prefix, status.message.asString()), color = Color.Red)
            }
        }
    }
}

@Composable
fun LogConsole(logs: List<UiText>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 8.dp)
    ) {
        val scrollState = rememberScrollState()
        LaunchedEffect(logs.size) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }

        Column(modifier = Modifier.padding(8.dp).verticalScroll(scrollState)) {
            logs.forEach { log ->
                Text(
                    text = log.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    lineHeight = 12.sp
                )
            }
        }
    }
}