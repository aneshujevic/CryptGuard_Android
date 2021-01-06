package com.example.cryptguard.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.cryptguard.Encrypter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class PasswordDataRepository(private val encryptedDataDao: EncryptedDataDao) {
    private var passphrase: String? = null

    fun getDbPassphrase(): String? {
        synchronized(this) {
            return passphrase
        }
    }

    fun setDbPassphrase(passphrase: String?) {
        synchronized(this) {
            this.passphrase = passphrase
        }
    }

    suspend fun addPasswordData(passwordData: PasswordData) {
        val encryptedData =
            encryptPasswordData(passwordData)?.let { EncryptedPasswordData(encryptedPasswordData = it) }
        if (encryptedData != null) {
            encryptedDataDao.insertEncryptedData(encryptedData)
        }
    }

    suspend fun updatePasswordData(id: Int, passwordData: PasswordData) {
        val encryptedData =
            encryptPasswordData(passwordData)?.let {
                EncryptedPasswordData(
                    id = id,
                    encryptedPasswordData = it
                )
            }
        if (encryptedData != null) {
            encryptedDataDao.updateEncryptedData(encryptedData)
        }
    }

    suspend fun removePasswordData(position: Int) = withContext(Dispatchers.IO) {
        encryptedDataDao.removeEncryptedDataById(position)
    }

    suspend fun getPasswordData(position: Int): PasswordData? = withContext(Dispatchers.IO) {
        val encryptedData = encryptedDataDao.getEncryptedDataById(position)
        decryptPasswordData(encryptedData.id, encryptedData.encryptedPasswordData)
    }

    suspend fun getAllPasswordData(): MutableLiveData<List<PasswordData?>> =
        withContext(Dispatchers.IO) {
            MutableLiveData(encryptedDataDao.getAllEncryptedData().map {
                decryptPasswordData(it.id, it.encryptedPasswordData)
            })
        }

    suspend fun getFirstEncryptedData(): EncryptedPasswordData? = withContext(Dispatchers.IO) {
        encryptedDataDao.getFirstEncryptedData()
    }

    suspend fun getAllEncryptedData(): List<EncryptedPasswordData?> = withContext(Dispatchers.IO) {
        encryptedDataDao.getAllEncryptedData()
    }

    suspend fun deleteAllEncryptedData() = withContext(Dispatchers.IO) {
        encryptedDataDao.truncateEncryptedData()
    }

    suspend fun addEncryptedData(encryptedData: EncryptedPasswordData) = withContext(Dispatchers.IO) {
        encryptedDataDao.insertEncryptedData(encryptedData)
    }

    suspend fun verifyPassphrase(): Boolean = withContext(Dispatchers.IO) {
        try {
            val encData = encryptedDataDao.getFirstEncryptedData()
            if (encData != null) {
                decryptPasswordData(encData.id, encData.encryptedPasswordData)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun encryptPasswordData(
        passwordData: PasswordData,
        password: String? = passphrase
    ): String? =
        withContext(Dispatchers.IO) {
            val pdEncoded = Gson().toJson(passwordData)
            password?.let { Encrypter.encryptStringAndGetBase64(pdEncoded, it) }
        }

    private suspend fun decryptPasswordData(
        id: Int?,
        base64encodedPasswordData: String,
        password: String? = null
    ): PasswordData? = withContext(Dispatchers.IO) {
        val base64DataArray = base64encodedPasswordData.split("\n")
        val pdEncrypted = base64DataArray[0]
        val salt = base64DataArray[1]
        val iv = base64DataArray[2]

        val passphrase = password ?: getDbPassphrase()

        val pd = Gson().fromJson(passphrase?.let {
            Encrypter.decryptBase64String(
                pdEncrypted,
                it, salt, iv
            )
        }, PasswordData::class.java)
        pd?.id = id
        pd
    }

    suspend fun encryptDatabase(newPassword: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val encryptedData = encryptedDataDao.getAllEncryptedData()

            for (element in encryptedData){
                element.let {
                    it.encryptedPasswordData = encryptPasswordData(
                        decryptPasswordData(
                            it.id,
                            it.encryptedPasswordData
                        )!!,
                        newPassword
                    )!!
                    encryptedDataDao.updateEncryptedData(it)
                }
            }
            true
        } catch (e: Exception) {
            e.message?.let { Log.d("bad_decrypt", it) }
            Log.d("error", e.toString())
            false
        }
    }
}
