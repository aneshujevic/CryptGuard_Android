package com.example.cryptguard.ui.generator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GeneratorViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Please enter the desired length of the password."
    }
    val text: LiveData<String> = _text
}