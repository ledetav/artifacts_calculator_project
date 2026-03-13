package com.nokaori.genshinaibuilder.presentation.ui.artifacts.scanner

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.presentation.util.sensor.SteadySensorEffect
import kotlinx.coroutines.delay
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private enum class CameraState { WARMUP, AIMING, CAPTURING }

@Composable
fun ArtifactCameraScreen(
    onImageCaptured: (Uri) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    
    // Состояния
    var cameraState by remember { mutableStateOf(CameraState.WARMUP) }
    var isPreviewReady by remember { mutableStateOf(false) }
    
    val imageCapture = remember { ImageCapture.Builder().build() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )

    LaunchedEffect(Unit) {
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(isPreviewReady) {
        if (isPreviewReady && cameraState == CameraState.WARMUP) {
            delay(1000L) 
            cameraState = CameraState.AIMING
        }
    }

    if (hasPermission) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            
            CameraPreview(
                imageCapture = imageCapture,
                onPreviewStarted = { isPreviewReady = true },
                modifier = Modifier.fillMaxSize()
            )

            if (cameraState == CameraState.AIMING) {
                SteadySensorEffect(
                    isActive = true,
                    onSteady = {
                        cameraState = CameraState.CAPTURING
                        takePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            onImageCaptured = onImageCaptured,
                            onError = { 
                                cameraState = CameraState.AIMING // Возвращаем в сканирование при ошибке
                                Log.e("ArtifactCameraScreen", "Capture error", it)
                            }
                        )
                    }
                )
            }

            CameraOverlay(
                cameraState = cameraState,
                onClose = onClose
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.camera_permission_required),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text(text = stringResource(R.string.grant_permission))
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onClose) {
                Text(text = stringResource(android.R.string.cancel))
            }
        }
    }
}

@Composable
private fun CameraPreview(
    imageCapture: ImageCapture,
    onPreviewStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    DisposableEffect(previewView, lifecycleOwner) {
        val observer = Observer<PreviewView.StreamState> { state ->
            if (state == PreviewView.StreamState.STREAMING) {
                onPreviewStarted()
            }
        }
        previewView.previewStreamState.observe(lifecycleOwner, observer)

        onDispose {
            previewView.previewStreamState.removeObserver(observer)
        }
    }

    LaunchedEffect(previewView) {
        val cameraProvider = context.getCameraProvider()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("CameraPreview", "Use case binding failed", e)
        }
    }

    AndroidView(
        factory = {
            previewView.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun CameraOverlay(
    cameraState: CameraState,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .statusBarsPadding()
                .background(Color.Black.copy(alpha = 0.3f), shape = androidx.compose.foundation.shape.CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Camera",
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f))
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            Text(
                text = if (cameraState == CameraState.CAPTURING) {
                    stringResource(R.string.camera_instruction_capturing)
                } else {
                    stringResource(R.string.camera_instruction_steady)
                },
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File.createTempFile("artifact_cam_", ".jpg", context.cacheDir)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onImageCaptured(savedUri)
            }

            override fun onError(exc: ImageCaptureException) {
                onError(exc)
            }
        }
    )
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    cameraProviderFuture.addListener({
        continuation.resume(cameraProviderFuture.get())
    }, ContextCompat.getMainExecutor(this))
}