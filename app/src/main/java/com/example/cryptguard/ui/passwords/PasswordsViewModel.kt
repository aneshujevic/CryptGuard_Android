package com.example.cryptguard.ui.passwords

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptguard.data.PasswordDataRepo
import com.example.cryptguard.data.PasswordData

class PasswordsViewModel(private val passwordDataRepo: PasswordDataRepo) : ViewModel() {
    var repo = passwordDataRepo.getAllPasswordData()

    fun getPasswordsDataObserver(): MutableLiveData<List<PasswordData>> {
        return repo
    }

    fun updatePasswords() {
        repo = passwordDataRepo.getAllPasswordData()
    }
}