package com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.presentation.ui.common.components.BaseDialog
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.components.MainStatSection
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.components.SubStatsSection
import com.nokaori.genshinaibuilder.presentation.ui.artifacts.editor.components.TopSelectionSection
import com.nokaori.genshinaibuilder.presentation.viewmodel.EditorArtifactViewModel
import com.nokaori.genshinaibuilder.presentation.util.sensor.DoubleTapSensorEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorArtifactScreen(
    onBackClick: () -> Unit,
    artifactId: String? = null,
    viewModel: EditorArtifactViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filteredSets by viewModel.filteredSets.collectAsStateWithLifecycle()
    val isEditingEnabled = state.selectedSet != null
    
    val hasErrors = state.validationErrors.isNotEmpty()
    val context = LocalContext.current

    LaunchedEffect(state.isSaveSuccess) {
        if (state.isSaveSuccess) {
            onBackClick()
        }
    }

    if (state.showBiometricPrompt) {
        val activity = context as? FragmentActivity
        if (activity != null) {
            LaunchedEffect(Unit) {
                val executor = ContextCompat.getMainExecutor(activity)
                val biometricPrompt = BiometricPrompt(activity, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            viewModel.onBiometricErrorOrCancel()
                        }
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            viewModel.onBiometricSuccess()
                        }
                    }
                )

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Сохранение артефакта")
                    .setSubtitle("Подтвердите сохранение отпечатком пальца")
                    .setNegativeButtonText("Отмена")
                    .build()

                biometricPrompt.authenticate(promptInfo)
            }
        } else {
            LaunchedEffect(Unit) {
                viewModel.onBiometricSuccess()
            }
        }
    }
    DoubleTapSensorEffect(
        enabled = !hasErrors && !state.showBiometricPrompt && !state.isSetSelectionDialogOpen
    ) {
        triggerVibration(context)
        viewModel.onDoubleTapTriggered()
    }

    if (state.isSetSelectionDialogOpen) {
        SetSelectionDialog(
            searchQuery = state.setSearchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            sets = filteredSets,
            onSetSelected = viewModel::onSetSelected,
            onDismiss = viewModel::onSetDialogDismiss
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = {
                    Text(
                        stringResource(R.string.artifact_add_button),
                        modifier = Modifier.offset(x = (-4).dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { if (!hasErrors) viewModel.onSaveClicked() },
                icon = { Icon(Icons.Default.Save, null) },
                text = { Text("Save Artifact") },
                containerColor = if (hasErrors) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                contentColor = if (hasErrors) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = if (hasErrors) 0.dp else 6.dp
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TopSelectionSection(
                selectedSet = state.selectedSet,
                availableRarities = state.availableRarities,
                selectedRarity = state.rarity,
                selectedSlot = state.slot,
                onSetClick = viewModel::onSetClicked,
                onRaritySelect = viewModel::onRarityChanged,
                onSlotSelect = viewModel::onSlotChanged,
                currentIconUrl = state.currentPieceIconUrl,
                enabled = isEditingEnabled
            )

            Spacer(modifier = Modifier.height(16.dp))

            MainStatSection(
                mainStatType = state.mainStatType,
                mainStatValue = state.mainStatValue,
                availableStats = state.availableMainStats,
                level = state.level,
                maxLevel = state.maxLevel,
                onStatSelected = viewModel::onMainStatTypeChanged,
                onLevelChanged = viewModel::onLevelChanged,
                enabled = isEditingEnabled
            )

            Spacer(modifier = Modifier.height(16.dp))

            SubStatsSection(
                subStats = state.subStats,
                artifactRarity = state.rarity,
                canAddMore = state.subStats.size < state.maxSubStatsCount,
                enabled = isEditingEnabled,
                onAddSubStat = viewModel::onAddSubStat,
                onRemoveSubStat = viewModel::onRemoveSubStat,
                onTypeChanged = viewModel::onSubStatTypeChanged,
                onRollAdded = viewModel::onSubStatRollAdded,
                onRollRemoved = viewModel::onSubStatRollRemoved,
                onManualInput = viewModel::onSubStatManualValueEntered
            )

            if (hasErrors) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Cannot save artifact:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        state.validationErrors.forEach { error ->
                            Text(
                                text = "• $error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

private fun triggerVibration(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibratorManager.defaultVibrator
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
}

@Composable
fun SetSelectionDialog(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sets: List<com.nokaori.genshinaibuilder.domain.model.ArtifactSet>,
    onSetSelected: (com.nokaori.genshinaibuilder.domain.model.ArtifactSet) -> Unit,
    onDismiss: () -> Unit
) {
    BaseDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.filter_artifact_set_choose),
        content = {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text(stringResource(R.string.artifact_search_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                sets.forEach { set ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSetSelected(set) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(set.iconUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.size(56.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = set.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 72.dp)
                    )
                }
            }
        },
        actions = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.filter_dialog_close)) }
        }
    )
}