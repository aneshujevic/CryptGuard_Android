package com.example.cryptguard.ui.password_detail_item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cryptguard.data.PasswordDataRepo
import com.example.cryptguard.data.PasswordData

class PasswordDetailItemViewModel(private val passwordRepo: PasswordDataRepo) : ViewModel() {
    private val _chosenPassword = MutableLiveData<PasswordData>()

    fun setChosen(position: Int) {
        _chosenPassword.value = passwordRepo.getPasswordData(position)
    }

    fun removeChosenPasswordData(position: Int) {
        passwordRepo.removePasswordData(position)
    }

    fun addPasswordData(passwordData: PasswordData) {
        passwordRepo.addPasswordData(passwordData)
    }

    fun updatePasswordData(passwordData: PasswordData) {
        passwordRepo.updatePasswordData(passwordData)
    }

    val passwordData: LiveData<PasswordData> = _chosenPassword
}

class PasswordDetailItemViewModelFactory(private val repository: PasswordDataRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordDetailItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PasswordDetailItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
