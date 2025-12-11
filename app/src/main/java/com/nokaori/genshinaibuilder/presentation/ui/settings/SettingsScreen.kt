package com.nokaori.genshinaibuilder.presentation.ui.settings

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.nokaori.genshinaibuilder.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel
) {
    Button(onClick = {
        settingsViewModel.updateDatabase()
    }) {
        Text("Update Database")
    }
}