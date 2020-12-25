package com.example.cryptguard.ui.password_detail_item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptguard.data.DummyPasswords
import com.example.cryptguard.data.PasswordData

class PasswordDetailItemViewModel : ViewModel() {
    private val _chosenPassword = MutableLiveData<PasswordData>()

    fun setChosen(position: Int) {
        _chosenPassword.value = DummyPasswords.repo.data.value!![position]
    }

    fun removeChosenPasswordData(position: Int) {
        DummyPasswords.repo.removePasswordData(position)
    }

    fun addPasswordData(passwordData: PasswordData) {
        DummyPasswords.repo.addPasswordData(passwordData)
    }

    fun changePasswordData(position: Int, passwordData: PasswordData) {
        DummyPasswords.repo.setPasswordData(position, passwordData)
    }

    val passwordData: LiveData<PasswordData> = _chosenPassword
}