package com.example.cryptguard.data

import androidx.lifecycle.MutableLiveData

class DummyPasswords {

    companion object {
        val repo = DummyPasswords()
    }

    public var data = MutableLiveData<ArrayList<PasswordData>>()
    init {
        data.let {
            it.value = arrayListOf(
                PasswordData("Facebook", "johndoe", "john@doe.com", "123123123", ""),
                PasswordData("Gmail", "johndo", "john@doe.com", "111111111", "esfpoajgoia"),
                PasswordData("Twitter", "johnd", "john@doe.com", "222222222", ""),
                PasswordData("Moodle", "john", "john@doe.com", "333333333", ""),
                PasswordData("PC", "joh", "john@doe.com", "444444444", "")
            )
        }
    }


    public fun setPasswordData(position: Int, passwordData: PasswordData) {
        data.value!![position].let {
            it.siteName = passwordData.siteName
            it.email = passwordData.email
            it.username = passwordData.username
            it.password = passwordData.password
            it.additionalData = passwordData.additionalData
        }
    }

    public fun removePasswordData(position: Int) {
        data.value?.removeAt(position)
    }

    fun addPasswordData(passwordData: PasswordData) {
        val pd = PasswordData(
            passwordData.siteName,
            passwordData.username,
            passwordData.email,
            passwordData.password,
            passwordData.additionalData
        )
        data.value?.add(pd)
    }
}