package com.example.camscanner.imagepdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream



object PdfGenerator {

    fun convertCacheImagesToPdf(
        context: Context,
        onComplete: (Boolean, String) -> Unit
    ) {
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
                onComplete(false, "No images found in cache directory")
                return
            }

            val pdfDocument = PdfDocument()

            imageFiles.forEachIndexed { index, file ->
                // Load the bitmap from file
                val bitmap43 = BitmapFactory.decodeFile(file.absolutePath)
                val rotate90 = Matrix().apply { postRotate(90f) }
                val bitmap = Bitmap.createBitmap(bitmap43, 0, 0, bitmap43.width, bitmap43.height, rotate90, true)

                if (bitmap != null) {
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
                }
            }
            // Save the PDF to cache directory
            val outputFile = File(

//                cacheDir,
            context.externalCacheDir,
                "cache_images_${System.currentTimeMillis()}.pdf"
            )

            FileOutputStream(outputFile).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }

            pdfDocument.close()

            onComplete(
                true,
                "PDF created with ${imageFiles.size} images\nSaved to: ${outputFile.absolutePath}"
            )

            deleteRecursively(context.cacheDir)

        } catch (e: Exception) {
            onComplete(false, "Error: ${e.message}")
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
