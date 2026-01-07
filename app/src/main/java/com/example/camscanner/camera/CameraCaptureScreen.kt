package com.example.camscanner.camera


import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.camscanner.MainViewModel



@Composable
fun CameraCaptureScreen(
    cameraController: LifecycleCameraController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val captureRequested by viewModel.captureRequested.collectAsState()
    val saveRequested by viewModel.saveRequested.collectAsState()

    // Bind camera to lifecycle
    LaunchedEffect(Unit) {
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    LaunchedEffect(captureRequested) {
        if (captureRequested){
            try{
                capturePhoto(context, cameraController)
            }finally {
                viewModel.captureHandled()
            }

        }
    }

    LaunchedEffect(saveRequested) {
        if (saveRequested){
            viewModel.savePdf(context)
            viewModel.saveHandled()
        }
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
            Button(
                enabled = (!captureRequested),
                onClick = {viewModel.requestCapture()}
            ) {
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
            Button(
                enabled = !saveRequested,
                onClick = {viewModel.requestSave()}
            ) {
                Text("Save")
            }
        }
    }
}
