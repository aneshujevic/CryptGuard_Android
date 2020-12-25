package com.example.cryptguard.ui.passwords

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptguard.data.DummyPasswords
import com.example.cryptguard.data.DummyPasswords.Companion.repo
import com.example.cryptguard.data.PasswordData

class PasswordsViewModel : ViewModel() {

    var repo = DummyPasswords.repo.data

    fun getPasswordsDataObserver(): MutableLiveData<ArrayList<PasswordData>> {
        return repo
    }

    fun updatePasswords() {
        repo.value = DummyPasswords.repo.data.value
    }
}