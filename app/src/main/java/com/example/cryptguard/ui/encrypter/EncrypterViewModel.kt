package com.example.cryptguard.ui.encrypter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EncrypterViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is encrypter Fragment"
    }
    val text: LiveData<String> = _text
}