package com.example.cryptguard.data

import androidx.lifecycle.MutableLiveData

class PasswordDataRepo(private val passwordDataDao: PasswordDataDao) {

    val allPassData = passwordDataDao.getAllPasswordData()

    public fun updatePasswordData(passwordData: PasswordData) {
        passwordDataDao.updatePasswordData(passwordData)
    }

    public fun removePasswordData(position: Int) {
        passwordDataDao.removePasswordDataById(position)
    }

    fun addPasswordData(passwordData: PasswordData) {
        passwordDataDao.insertPasswordData(passwordData)
    }

    fun getPasswordData(position: Int): PasswordData {
        return passwordDataDao.getPasswordDataById(position)
    }

    fun getAllPasswordData(): MutableLiveData<List<PasswordData>>{
        return MutableLiveData(passwordDataDao.getAllPasswordData())
    }
}