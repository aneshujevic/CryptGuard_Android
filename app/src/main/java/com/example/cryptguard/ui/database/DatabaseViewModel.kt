package com.example.cryptguard.ui.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DatabaseViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is database Fragment"
    }
    val text: LiveData<String> = _text
}