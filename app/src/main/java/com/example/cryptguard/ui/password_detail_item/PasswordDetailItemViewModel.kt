package com.example.cryptguard.ui.password_detail_item

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cryptguard.data.PasswordDataRepository
import com.example.cryptguard.data.PasswordData

class PasswordDetailItemViewModel(private val passwordRepo: PasswordDataRepository) : ViewModel() {
    private val _chosenPassword = MutableLiveData<PasswordData>()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun setChosen(position: Int) {
        _chosenPassword.value = passwordRepo.getPasswordData(position)
    }

    suspend fun removeChosenPasswordData(position: Int) {
        passwordRepo.removePasswordData(position)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addPasswordData(passwordData: PasswordData) {
        passwordRepo.addPasswordData(passwordData)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updatePasswordData(passwordData: PasswordData) {
        passwordRepo.updatePasswordData(passwordData)
    }

    val passwordData: LiveData<PasswordData> = _chosenPassword
}

class PasswordDetailItemViewModelFactory(private val repository: PasswordDataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordDetailItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PasswordDetailItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
