package com.example.camscanner

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camscanner.imagepdf.PdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


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
        viewModelScope.launch {
            PdfGenerator.convertCacheImagesToPdf(context)
        }
    }
}