package com.example.camscanner.camera

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable



@Composable
fun DocumentPicker(
    mimeType: String,
    onUriCreated: (Uri) -> Unit
): ManagedActivityResultLauncher<String, Uri?> {

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(mimeType)
    ) { uri ->
        uri?.let(onUriCreated)
    }
}
