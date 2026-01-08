package com.example.camscanner.imagepdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.graphics.Matrix
import android.hardware.biometrics.BiometricManager
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.painter.BitmapPainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream



object PdfGenerator {

    suspend fun convertCacheImagesToPdf(context: Context, grayscaleEnabled: Boolean, saveUri: Uri) {
        withContext(Dispatchers.IO){
            try {
                val cacheDir = context.cacheDir

                // Debug: Log cache directory path
                println("Cache dir path: ${cacheDir?.absolutePath}")

                // Debug: List ALL files in cache
                val allFiles = cacheDir?.listFiles()
                println("Total files in cache: ${allFiles?.size}")
                allFiles?.forEach {
                    println("File: ${it.name}, Extension: ${it.extension}")
                }

                // Get all image files from cache directory
                val imageFiles = cacheDir?.listFiles { file ->
                    file.isFile && file.extension.lowercase() in listOf("jpg", "jpeg", "png")
                }?.sortedBy { it.name } ?: emptyList()


                if (imageFiles.isEmpty()) {
                    Log.e("PDF", "No images found in cache directory")
                    return@withContext
                }

                val pdfDocument = PdfDocument()

                imageFiles.forEachIndexed { index, file ->
                    // Calculating scaling
                    val metaData = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeFile(file.absolutePath, metaData)
                    val targetWidth = 1240
                    val scaleFactor = (metaData.outWidth/ targetWidth).coerceAtLeast(1)
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = scaleFactor
                        inPreferredConfig = Bitmap.Config.RGB_565
                    }



                    val bitmap43 = BitmapFactory.decodeFile(file.absolutePath, options)
                    val rotate90 = Matrix().apply { postRotate(90f) }
                    var bitmap = Bitmap.createBitmap(bitmap43, 0, 0, bitmap43.width, bitmap43.height, rotate90, true)

                    if (grayscaleEnabled){
                        bitmap = convertToGrayscale(bitmap)
                    }

                        // Create a page with the bitmap dimensions
                    val pageInfo = PdfDocument.PageInfo.Builder(
                        bitmap.width,
                        bitmap.height,
                        index + 1
                    ).create()

                    val page = pdfDocument.startPage(pageInfo)

                    // Draw the bitmap on the page
                    page.canvas.drawBitmap(bitmap, 0f, 0f, null)

                    pdfDocument.finishPage(page)
                    bitmap.recycle()

                    Log.d("PDF", "Drew page no ${index+1}")
                }
                // Save the PDF to cache directory
//                val outputFile = File(
//
////                cacheDir,
//                    context.externalCacheDir,
//                    "cache_images_${System.currentTimeMillis()}.pdf"
//                )
//
//                FileOutputStream(outputFile).use { outputStream ->
//                    pdfDocument.writeTo(outputStream)
//                }

                context.contentResolver
                    .openOutputStream(saveUri)
                    ?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                
                pdfDocument.close()


                Log.d("PDF","PDF created with ${imageFiles.size} images\nSaved to: $saveUri")

                deleteRecursively(context.cacheDir)

            } catch (e: Exception) {
                Log.e("PDF", "Error: ${e.message}")
            }
        }
        }

}



fun deleteRecursively(file: File?) {
    if (file == null || !file.exists()) return

    if (file.isDirectory) {
        file.listFiles()?.forEach { deleteRecursively(it) }
    }
    file.delete()
}
