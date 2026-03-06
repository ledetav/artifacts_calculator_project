package com.nokaori.genshinaibuilder.presentation.ui.artifacts.scanner

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.util.ParsedArtifactData
import com.nokaori.genshinaibuilder.presentation.viewmodel.ArtifactScannerViewModel
import com.nokaori.genshinaibuilder.presentation.viewmodel.ScannerState
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactScannerScreen(
    imageUriString: String?,
    onScanComplete: (ParsedArtifactData) -> Unit, 
    onBackClick: () -> Unit,
    viewModel: ArtifactScannerViewModel = hiltViewModel()
) {
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var showRawTextDialog by remember { mutableStateOf(false) }

    val scannerState by viewModel.scannerState.collectAsStateWithLifecycle()

    val decodedUri = remember(imageUriString) {
        imageUriString?.let { Uri.parse(URLDecoder.decode(it, "UTF-8")) }
    }

    LaunchedEffect(decodedUri) {
        if (decodedUri != null) {
            viewModel.scanImage(decodedUri)
        }
    }

    val isScanning = scannerState is ScannerState.Scanning || scannerState is ScannerState.Idle

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (isScanning) R.string.scanner_preparing else R.string.scanner_scanning)) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Зона скриншота
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Black.copy(alpha = 0.05f))
                    .onGloballyPositioned { coordinates ->
                        imageSize = coordinates.size
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(decodedUri)
                        .build(),
                    contentDescription = "Artifact Screenshot",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // Лазер пока идет сканирование
                if (isScanning && imageSize.height > 0) {
                    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
                    val laserY by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = imageSize.height.toFloat(),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Блок с результатами (появляется снизу)
            AnimatedVisibility(
                visible = scannerState is ScannerState.Success,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                val data = (scannerState as? ScannerState.Success)?.data ?: ParsedArtifactData()
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                        .padding(16.dp)
                ) {
                    Text("Распознано:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Уровень: ${data.level?.let { "+$it" } ?: "Не найден"}")
                    Text("Главный стат: ${data.mainStatType?.displayName ?: "?"} (${data.mainStatValue ?: "?"})")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Подстаты:", fontWeight = FontWeight.SemiBold)
                    data.subStats.forEach { (statType, value) ->
                        Text("• ${statType.displayName} : $value")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = { showRawTextDialog = true }) {
                            Text("Сырой текст")
                        }
                        Button(onClick = { onScanComplete(data) }) {
                            Text("В Редактор")
                        }
                    }
                }
            }

            // Обработка ошибки
            if (scannerState is ScannerState.Error) {
                Text(
                    text = (scannerState as ScannerState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    // Диалог сырого текста для отладки
    if (showRawTextDialog && scannerState is ScannerState.Success) {
        val text = (scannerState as ScannerState.Success).data.rawText
        AlertDialog(
            onDismissRequest = { showRawTextDialog = false },
            title = { Text("Сырой текст из OCR") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                        .background(Color.Black.copy(alpha = 0.05f), MaterialTheme.shapes.small)
                        .padding(12.dp)
                ) {
                    Text(text = text, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(onClick = { showRawTextDialog = false }) { Text("Закрыть") }
            }
        )
    }
}