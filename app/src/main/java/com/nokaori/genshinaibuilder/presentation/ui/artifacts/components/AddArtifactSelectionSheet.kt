package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddArtifactSelectionSheet(
    onDismissRequest: () -> Unit,
    onManualEntrySelected: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onMultipleImagesSelected: (List<Uri>) -> Unit,
    onCameraClick: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    
    var showSourceDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onImageSelected(uri)
                onDismissRequest()
            }
        }
    )

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 15),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                onMultipleImagesSelected(uris)
                onDismissRequest()
            }
        }
    )

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text(text = stringResource(id = R.string.choose_image_source)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSourceDialog = false
                        onDismissRequest()
                        onCameraClick()
                    }
                ) {
                    Text(text = stringResource(id = R.string.source_camera))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSourceDialog = false
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text(text = stringResource(id = R.string.source_gallery))
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_artifact_method_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SelectionOptionCard(
                title = stringResource(id = R.string.add_artifact_manual),
                icon = Icons.Default.Create,
                onClick = {
                    onManualEntrySelected()
                    onDismissRequest()
                }
            )

            SelectionOptionCard(
                title = stringResource(id = R.string.add_artifact_scan),
                subtitle = stringResource(id = R.string.add_artifact_scan_desc),
                icon = Icons.Default.DocumentScanner,
                onClick = {
                    showSourceDialog = true
                }
            )

            SelectionOptionCard(
                title = stringResource(id = R.string.add_artifact_scan_multiple),
                subtitle = stringResource(id = R.string.add_artifact_scan_multiple_desc),
                icon = Icons.Default.DocumentScanner,
                onClick = {
                    multiplePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
    }
}

@Composable
private fun SelectionOptionCard(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}