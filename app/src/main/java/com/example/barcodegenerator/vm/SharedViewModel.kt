package com.example.barcodegenerator.vm

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> get() = _bitmap

    private var _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    fun setBitmap(newBitmap: Bitmap){
        _bitmap.value = newBitmap
    }

    fun setText(text: String){
        _text.value = text
    }

}