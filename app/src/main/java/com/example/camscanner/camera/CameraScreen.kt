package com.example.camscanner.camera

import android.Manifest
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.camscanner.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale




@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    cameraController: LifecycleCameraController,
    viewModel: MainViewModel
) {
    val permission = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        permission.launchPermissionRequest()
    }


    when {
        permission.status.isGranted ->
            CameraCaptureScreen(cameraController, viewModel)

        permission.status.shouldShowRationale ->
            PermissionDeniedUI{ permission.launchPermissionRequest() }

        else -> {
            Text("Camera permission is required.")
        }
    }
}


@Composable
fun PermissionDeniedUI(callback: () -> Unit){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text("Camera permission denied.")
        Button(onClick = callback) {
            Text("Try again")
        }
    }
}