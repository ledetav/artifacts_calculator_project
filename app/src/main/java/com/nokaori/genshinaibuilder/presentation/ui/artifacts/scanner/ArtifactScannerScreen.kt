package com.nokaori.genshinaibuilder.presentation.ui.artifacts.scanner

import android.net.Uri
import android.util.Log
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.nokaori.genshinaibuilder.R
import java.net.URLDecoder
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactScannerScreen(
    imageUriString: String?,
    onScanComplete: (String) -> Unit, 
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var isPreparing by remember { mutableStateOf(true) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    
    // Переменная для хранения текста для проверки
    var extractedText by remember { mutableStateOf("") }

    // Декодируем URI обратно
    val decodedUri = remember(imageUriString) {
        imageUriString?.let { Uri.parse(URLDecoder.decode(it, "UTF-8")) }
    }

    // РЕАЛЬНОЕ СКАНИРОВАНИЕ
    LaunchedEffect(decodedUri) {
        if (decodedUri != null) {
            isPreparing = true
            
            // Маленькая пауза для плавности старта анимации
            delay(500) 
            
            val recognizer = ArtifactTextRecognizer(context)
            val result = recognizer.extractTextFromUri(decodedUri)
            
            if (result != null) {
                extractedText = result
                Log.d("ArtifactScanner", "Распознанный текст:\n$result")
            } else {
                extractedText = "Ошибка распознавания или текст не найден"
            }
            
            isPreparing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (isPreparing) R.string.scanner_preparing
                            else R.string.scanner_scanning
                        )
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isPreparing) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.scanner_preparing),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
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
                    // Картинка со скриншотом пользователя
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(decodedUri)
                            .build(),
                        contentDescription = "Artifact Screenshot",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Анимация бегающего лазера
                    if (imageSize.height > 0) {
                        val infiniteTransition = rememberInfiniteTransition(label = "scanner")
                        val laserY by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = imageSize.height.toFloat(),
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = 2000,
                                    easing = LinearEasing
                                ),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "laser_y"
                        )

                        // Сам лазер
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .offset(y = with(LocalDensity.current) { laserY.toDp() })
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFFF4081),
                                            Color(0xFFD500F9),
                                            Color(0xFF00E5FF)
                                        )
                                    )
                                )
                        )

                        // Мягкое свечение под лазером
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .offset(y = with(LocalDensity.current) { (laserY - 40).toDp() })
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFFD500F9).copy(alpha = 0.2f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // ВРЕМЕННО: Выводим текст, чтобы убедиться, что Huawei все читает
                        Text(
                            text = "Распознано символов: ${extractedText.length}\nПосмотри Logcat (тег ArtifactScanner)!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(onClick = { 
                            // Передаем сырой текст дальше
                            onScanComplete(extractedText) 
                        }) {
                            Text("Перейти к парсингу")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { onScanComplete("dummy_data") }) {
                    Text("Симулировать завершение сканирования")
                }
            }
        }
    }
}
