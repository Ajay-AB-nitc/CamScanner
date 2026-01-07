package com.example.camscanner


import android.content.Context
import android.util.Log
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
    private val _grayscaleEnabled = MutableStateFlow(false)
    val captureRequested : StateFlow<Boolean> = _captureRequested
    val imageCount : StateFlow<Int> = _imageCount
    val saveRequested : StateFlow<Boolean> = _saveRequested
    val grayscaleEnabled : StateFlow<Boolean> = _grayscaleEnabled

    fun requestCapture(){
        _captureRequested.value = true
        Log.d("VM", "captureRequest set to ture")
    }
    fun captureHandled(){
        _captureRequested.value = false
        Log.d("VM", "captureRequest set to false")
    }

    fun requestSave(){
        _saveRequested.value = true
        Log.d("VM", "saveRequest set to true")
    }

    fun saveHandled(){
        _saveRequested.value = false
        Log.d("VM", "saveRequest set to false")
    }

    fun toggleGrayscale(){
        _grayscaleEnabled.value= !(_grayscaleEnabled.value)
    }
     suspend fun savePdf(context: Context){
         PdfGenerator.convertCacheImagesToPdf(context, _grayscaleEnabled.value)
    }
}