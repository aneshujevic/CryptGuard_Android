package com.example.cryptguard.ui.password_detail_item

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptguard.data.DummyPasswords
import com.example.cryptguard.data.PasswordData

class PasswordDetailItemViewModel : ViewModel() {
    private val _chosenPassword = MutableLiveData<PasswordData>()

    fun setChosen(position: Int) {
        _chosenPassword.value = DummyPasswords().data[position]
    }

    val passwordData: LiveData<PasswordData> = _chosenPassword
}