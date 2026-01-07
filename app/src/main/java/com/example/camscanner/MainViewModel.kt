package com.example.camscanner


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camscanner.imagepdf.PdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MainViewModel : ViewModel(){
    private val _captureRequested = MutableStateFlow(false)
    private val _imageCount = MutableStateFlow(0)
    private val _saveRequested = MutableStateFlow(false)
    val captureRequested : StateFlow<Boolean> = _captureRequested
    val imageCount : StateFlow<Int> = _imageCount
    val saveRequested : StateFlow<Boolean> = _saveRequested

    fun requestCapture(){
        _captureRequested.value = true
    }
    fun captureHandled(){
        _captureRequested.value = false
    }

    fun requestSave(){
        _saveRequested.value = true
    }

    fun saveHandled(){
        _saveRequested.value = false
    }

    fun savePdf(context: Context){
        viewModelScope.launch {
            PdfGenerator.convertCacheImagesToPdf(context)
        }
    }
}