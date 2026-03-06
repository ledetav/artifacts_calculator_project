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
    val scannerState by viewModel.scannerState.collectAsStateWithLifecycle()

    val decodedUri = remember(imageUriString) {
        imageUriString?.let { Uri.parse(URLDecoder.decode(it, "UTF-8")) }
    }

    // Запуск процесса распознавания при открытии
    LaunchedEffect(decodedUri) {
        if (decodedUri != null) {
            viewModel.scanImage(decodedUri)
        }
    }

    // АВТОМАТИЧЕСКИЙ ПЕРЕХОД при успешном сканировании
    LaunchedEffect(scannerState) {
        if (scannerState is ScannerState.Success) {
            val data = (scannerState as ScannerState.Success).data
            onScanComplete(data) // Перебрасываем в редактор
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
            verticalArrangement = Arrangement.Center // Центрируем изображение
        ) {
            // Зона скриншота с лазером
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

                // Лазер бегает только пока идет сканирование
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

            // Обработка ошибки
            if (scannerState is ScannerState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (scannerState as ScannerState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = onBackClick) {
                    Text("Вернуться")
                }
            }
        }
    }
}
