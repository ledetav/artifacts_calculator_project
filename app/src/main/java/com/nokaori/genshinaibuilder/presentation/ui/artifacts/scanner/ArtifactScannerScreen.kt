package com.nokaori.genshinaibuilder.presentation.ui.artifacts.scanner

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.util.ParsedArtifactData
import com.nokaori.genshinaibuilder.presentation.viewmodel.ArtifactScannerViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.ScannerResult
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactScannerScreen(
    imageUriString: String?,
    onScanComplete: (ParsedArtifactData) -> Unit,
    onBatchScanComplete: (List<ParsedArtifactData>) -> Unit,
    onBackClick: () -> Unit,
    onRetakeCameraClick: () -> Unit,
    onManualEntryClick: () -> Unit,
    viewModel: ArtifactScannerViewModel = hiltViewModel()
) {
    var actualImageSize by remember { mutableStateOf(IntSize.Zero) }
    val scannerState by viewModel.scannerState.collectAsStateWithLifecycle()

    val decodedUri = remember(imageUriString) {
        imageUriString?.let { Uri.parse(URLDecoder.decode(it, "UTF-8")) }
    }

    val displayUri = scannerState.currentImageUri ?: decodedUri

    LaunchedEffect(decodedUri) {
        if (decodedUri != null) {
            viewModel.scanImage(decodedUri)
        }
    }

    LaunchedEffect(scannerState.result) {
        when (val result = scannerState.result) {
            is ScannerResult.Success -> {
                onScanComplete(result.data)
            }
            is ScannerResult.BatchSuccess -> {
                onBatchScanComplete(result.data)
            }
            else -> {}
        }
    }

    if (scannerState.result is ScannerResult.Error) {
        AlertDialog(
            onDismissRequest = onBackClick, // Закрываем при клике вне окна
            title = { Text(stringResource(R.string.scan_failed_title)) },
            text = { Text(stringResource(R.string.scan_failed_message)) },
            confirmButton = {
                TextButton(onClick = onRetakeCameraClick) {
                    Text(stringResource(R.string.action_retake))
                }
            },
            dismissButton = {
                TextButton(onClick = onManualEntryClick) {
                    Text(stringResource(R.string.action_manual))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(stringResource(if (scannerState.isProcessing) R.string.scanner_preparing else R.string.scanner_scanning))
                        if (scannerState.totalToProcess > 0) {
                            Text(
                                text = stringResource(
                                    id = R.string.scanner_processing_batch,
                                    scannerState.currentProcessingIndex,
                                    scannerState.totalToProcess
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Black.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                if (displayUri != null) {
                    Box(
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            actualImageSize = coordinates.size
                        }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(displayUri)
                                .build(),
                            contentDescription = "Artifact Screenshot",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                        )

                        if (scannerState.isProcessing && actualImageSize.height > 0) {
                            val infiniteTransition = rememberInfiniteTransition(label = "scanner")
                            val laserY by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = actualImageSize.height.toFloat(),
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 2000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "laser_y"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .offset(y = with(LocalDensity.current) { laserY.toDp() })
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(Color(0xFFFF4081), Color(0xFFD500F9), Color(0xFF00E5FF))
                                        )
                                    )
                            )
                        }
                    }
                }
            }

            if (scannerState.totalToProcess > 0 && scannerState.isProcessing) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { scannerState.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}