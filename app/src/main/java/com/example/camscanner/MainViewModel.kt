package com.example.camscanner

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.camscanner.imagepdf.PdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class MainViewModel : ViewModel(){
    private val _captureRequested = MutableStateFlow(false)
    val captureRequested : StateFlow<Boolean> = _captureRequested

    fun requestCapture(){
        _captureRequested.value = true
    }

    fun captureHandled(){
        _captureRequested.value = false
    }

    fun savePdf(context: Context){
        PdfGenerator.convertCacheImagesToPdf(context){ worked, message ->
            Log.d("PDF", message)
        }
    }

}