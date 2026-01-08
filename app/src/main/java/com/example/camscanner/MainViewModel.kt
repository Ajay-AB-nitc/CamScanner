package com.example.camscanner


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.camscanner.imagepdf.PdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update



class MainViewModel : ViewModel(){
    private val _captureRequested = MutableStateFlow(false)
    private val _imageCount = MutableStateFlow(0)
    private val _saveRequested = MutableStateFlow(false)
    private val _grayscaleEnabled = MutableStateFlow(false)
    private val _torchEnabled = MutableStateFlow(false)

    val captureRequested : StateFlow<Boolean> = _captureRequested
    val imageCount : StateFlow<Int> = _imageCount
    val saveRequested : StateFlow<Boolean> = _saveRequested
    val grayscaleEnabled : StateFlow<Boolean> = _grayscaleEnabled
    val torchEnabled : StateFlow<Boolean> = _torchEnabled


    fun requestCapture(){
        _captureRequested.value = true
        Log.d("VM", "captureRequest set to ture")
    }
    fun captureHandled(){
        _captureRequested.value = false
        _imageCount.value += 1
        Log.d("VM", "captureRequest set to false")
    }

    fun requestSave(){
        if (_imageCount.value == 0){
            return
        }
        Log.d("VM", "requestSave called")
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
     suspend fun savePdf(context: Context, saveUri: Uri){
         PdfGenerator.convertCacheImagesToPdf(context, _grayscaleEnabled.value, saveUri)
    }

    fun toggleTorch() {
        _torchEnabled.value = !_torchEnabled.value
    }


}