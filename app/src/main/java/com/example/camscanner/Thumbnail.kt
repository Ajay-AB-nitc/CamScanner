package com.example.camscanner

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import java.io.File

@Composable
fun Thumbnail(viewModel: MainViewModel, context: Context) {
    val imgNumber by viewModel.imageCount.collectAsState()
    val file = File(context.cacheDir, "img$imgNumber.jpg")

    if (file != null) {
        AsyncImage(
            model = file,
            contentDescription = "Last captured image",
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.White, RoundedCornerShape(8.dp)),

            contentScale = ContentScale.Crop
        )
    }
}