package com.example.camscanner.camera


import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.lifecycle.viewModelScope
import com.example.camscanner.MainViewModel
import com.example.camscanner.Thumbnail
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCaptureScreen(
    cameraController: LifecycleCameraController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val captureRequested by viewModel.captureRequested.collectAsState()
    val saveRequested by viewModel.saveRequested.collectAsState()
    val grayscaleEnabled by viewModel.grayscaleEnabled.collectAsState()
    val imgCount by viewModel.imageCount.collectAsState()
    val torchEnabled by viewModel.torchEnabled.collectAsState()



    val createPdfLauncher = DocumentPicker("application/pdf") { uri ->
        uri.let {
            viewModel.viewModelScope.launch {
                viewModel.savePdf(context, it)
            }
        }
        viewModel.saveHandled()
    }

    // Bind camera to lifecycle
    LaunchedEffect(Unit) {
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    LaunchedEffect(captureRequested) {
        if (captureRequested){
            try{
                capturePhoto(context, imgCount+1, cameraController)
            }finally {
                viewModel.captureHandled()
            }

        }
    }

    LaunchedEffect(saveRequested) {
        if (saveRequested){
            createPdfLauncher.launch("${System.currentTimeMillis()}.pdf")
        }
    }

    LaunchedEffect(torchEnabled) {
            cameraController.cameraControl?.enableTorch(torchEnabled)
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,

        ) {

        }

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

        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Thumbnail(viewModel, context)

                Button(
                    enabled = (!captureRequested),
                    onClick = {viewModel.requestCapture()}
                ) {
                    Text("Capture")
                }


                IconButton(
                    onClick = {
                        viewModel.requestSave()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save"
                    )
                }

            }
        }



        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,

            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    onClick = { viewModel.toggleGrayscale() },
                    label = {
                        Text("Grayscale")
                    },
                    selected = grayscaleEnabled
                )

                FilterChip(
                    onClick = { viewModel.toggleTorch() },
                    label = {
                        Text("Torch")
                    },
                    selected = torchEnabled
                )
            }
        }
    }
}
