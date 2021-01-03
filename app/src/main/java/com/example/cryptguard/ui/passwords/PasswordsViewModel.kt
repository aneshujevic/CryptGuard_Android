package com.example.cryptguard.ui.passwords

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptguard.data.PasswordDataRepository
import com.example.cryptguard.data.PasswordData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.O)
class PasswordsViewModel(private val passwordDataRepo: PasswordDataRepository, context: Context) : ViewModel() {
    private lateinit var repo: MutableLiveData<List<PasswordData?>>
    init {
        runBlocking {
            repo = passwordDataRepo.getAllPasswordData()
        }
    }

    fun getPasswordsDataObserver(): MutableLiveData<List<PasswordData?>> {
        return repo
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updatePasswords() {
        repo = passwordDataRepo.getAllPasswordData()
    }
}