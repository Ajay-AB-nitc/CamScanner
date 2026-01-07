package com.example.camscanner.imagepdf

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import androidx.core.graphics.createBitmap


fun bitmapToMat(bitmap: Bitmap): Mat {
    val mat = Mat()
    Utils.bitmapToMat(bitmap, mat)
    return mat
}

fun enhance(mat: Mat): Mat {
    val gray = Mat()
    Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)

    // Improve contrast (histogram normalization)
    val normalized = Mat()
    Core.normalize(
        gray,
        normalized,
        0.0,
        255.0,
        Core.NORM_MINMAX
    )
    return normalized
}

fun adaptiveThreshold(gray: Mat): Mat {
    val binary = Mat()

    Imgproc.adaptiveThreshold(
        gray,
        binary,
        255.0,
        Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
        Imgproc.THRESH_BINARY,
        41,
        8.0
    )

    return binary
}

fun matToBitmap(mat: Mat): Bitmap {
    val bitmap = createBitmap(mat.cols(), mat.rows())
    Utils.matToBitmap(mat, bitmap)
    return bitmap
}

fun convertToGrayscale(bitmap: Bitmap): Bitmap {
    val matrix = bitmapToMat(bitmap)
    val enhanced = enhance(matrix)
    val grayscale = adaptiveThreshold(enhanced)
    return matToBitmap(grayscale)
}
