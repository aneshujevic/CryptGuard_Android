package com.example.cryptguard.ui.passwords

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PasswordsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is passwords Fragment"
    }
    val text: LiveData<String> = _text
}