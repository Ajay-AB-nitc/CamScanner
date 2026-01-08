package com.example.camscanner

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.AspectRatio
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camscanner.camera.CameraScreen
import com.example.camscanner.imagepdf.deleteRecursively
import com.example.camscanner.ui.theme.CamScannerTheme
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity() {

    private lateinit var cameraController: LifecycleCameraController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Initialization failed")
        }

        cameraController = LifecycleCameraController(this).apply {
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
        deleteRecursively(this.cacheDir)
        setContent {
            val viewModel: MainViewModel = viewModel()
            CamScannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ){
                        CameraScreen(cameraController, viewModel)
                    }
                }
            }
        }
    }
}

