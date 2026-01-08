package com.example.camscanner.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun capturePhoto(
    context: Context,
    imgNumber: Int,
    cameraController: LifecycleCameraController
): File = suspendCancellableCoroutine{ continuation ->

    val file = File(
        context.cacheDir,
        "img${imgNumber}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    cameraController.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d("Camera", "Saved: ${file.absolutePath}")
                if (continuation.isActive){
                    continuation.resume(file)
                }
            }
            override fun onError(exception: ImageCaptureException) {
                Log.e("Camera", "Capture failed", exception)
                if (continuation.isActive){
                    continuation.resumeWithException(exception)
                }
            }
        }
    )
}