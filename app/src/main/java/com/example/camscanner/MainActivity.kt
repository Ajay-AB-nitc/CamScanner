package com.example.camscanner

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.camscanner.ui.theme.CamScannerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File
import com.example.camscanner.imagepdf.PdfGenerator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CamScannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ){
//                        testPdf(LocalContext.current)
//                        convertCacheImagesToPdf(LocalContext.current){ worked, message -> Log.d("func", message) }
                        CameraScreen()
                    }
                }
            }
        }
    }
}


private fun deleteRecursively(file: File?) {
    if (file == null || !file.exists()) return

    if (file.isDirectory) {
        file.listFiles()?.forEach { deleteRecursively(it) }
    }
    file.delete()
}


@Composable
fun CameraCaptureScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera controller (manages preview + capture)
    val cameraController = remember {

        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )


            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(
                    AspectRatioStrategy(
                        AspectRatio.RATIO_4_3,
                        AspectRatioStrategy.FALLBACK_RULE_AUTO
                    )
                )
                .build()

            setPreviewResolutionSelector(resolutionSelector)
            setImageCaptureResolutionSelector(resolutionSelector)
        }
    }

    // Bind camera to lifecycle
    LaunchedEffect(Unit) {
        cameraController.bindToLifecycle(lifecycleOwner)

    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PreviewView(it).apply {
                        scaleType = PreviewView.ScaleType.FIT_CENTER
                        controller = cameraController
                    }

                }
            )
        }

        // Bottom space / controls
        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { capturePhoto(context, cameraController) }) {
                Text("Capture")
            }

        }

        // Bottom space / controls
        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { PdfGenerator.convertCacheImagesToPdf(context){ worked, message -> Log.d("func", message) }}) {
                Text("Save")
            }
        }


    }
}


fun capturePhoto(

    context: Context,
    cameraController: LifecycleCameraController
) {

    val file = File(
        context.cacheDir,
//        context.cacheDir,
        "photo_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    cameraController.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d("Camera", "Saved: ${file.absolutePath}")
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("Camera", "Capture failed", exception)
            }
        }
    )
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen() {
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        cameraPermission.launchPermissionRequest()
    }

    when {

        cameraPermission.status.isGranted -> {
            CameraCaptureScreen()
//            CameraPreview()
        }

        cameraPermission.status.shouldShowRationale -> {

            Column {
                Text("Camera permission denied.")
                Button(onClick = {
                    cameraPermission.launchPermissionRequest()
                }) {
                    Text("Try again")
                }
            }

        }

        else -> {
            Text("Camera permission is required.")
        }
    }
}

