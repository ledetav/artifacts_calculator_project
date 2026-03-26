package com.nokaori.genshinaibuilder.presentation.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.SupportedLanguages
import com.nokaori.genshinaibuilder.presentation.ui.common.components.SingleSelectToggleButtonGroup
import com.nokaori.genshinaibuilder.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateToGestures: () -> Unit,
    onNavigateToDataSync: () -> Unit
) {
    val appLanguage by settingsViewModel.appLanguage.collectAsStateWithLifecycle()
    val shouldShowLanguageSyncDialog by settingsViewModel.shouldShowLanguageSyncDialog.collectAsStateWithLifecycle()

    if (shouldShowLanguageSyncDialog) {
        AlertDialog(
            onDismissRequest = { settingsViewModel.dismissLanguageSyncDialog() },
            title = { Text(stringResource(R.string.language_sync_dialog_title)) },
            text = { Text(stringResource(R.string.language_sync_dialog_message)) },
            confirmButton = {
                Button(onClick = { settingsViewModel.syncDataForCurrentLanguage() }) {
                    Text(stringResource(R.string.language_sync_dialog_download))
                }
            },
            dismissButton = {
                TextButton(onClick = { settingsViewModel.dismissLanguageSyncDialog() }) {
                    Text(stringResource(R.string.language_sync_dialog_later))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
    ) {
        SettingsSectionTitle(stringResource(R.string.settings_language_title))
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SingleSelectToggleButtonGroup(
                    title = "",
                    items = listOf(SupportedLanguages.EN, SupportedLanguages.RU),
                    selectedItem = appLanguage,
                    onItemSelect = { settingsViewModel.setAppLanguage(it) },
                    modifier = Modifier.fillMaxWidth()
                ) { lang, _ ->
                    val labelRes = if (lang == SupportedLanguages.EN) R.string.lang_english else R.string.lang_russian
                    Text(stringResource(labelRes))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsSectionTitle("Preferences")
        SettingsItemCard(
            icon = Icons.Default.TouchApp,
            title = stringResource(R.string.settings_gesture_controls),
            onClick = onNavigateToGestures
        )

        SettingsItemCard(
            icon = Icons.Default.Sync,
            title = stringResource(R.string.data_sync_title),
            onClick = onNavigateToDataSync
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItemCard(icon: ImageVector, title: String, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}