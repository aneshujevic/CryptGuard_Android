package com.example.cryptguard.data

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.text.InputType
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.example.cryptguard.ui.encrypter.Encrypter
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class PasswordDataRepository(private val encryptedDataDao: EncryptedDataDao) {
    var passphrase: String? = null

    suspend fun updatePasswordData(passwordData: PasswordData) {
        val encryptedData =
            encryptPasswordData(passwordData)?.let { EncryptedData(encryptedPasswordData = it) }
        if (encryptedData != null) {
            encryptedDataDao.updateEncryptedData(encryptedData)
        }
    }

    suspend fun removePasswordData(position: Int) = withContext(Dispatchers.IO) {
        encryptedDataDao.removeEncryptedDataById(position)
    }

    suspend fun addPasswordData(passwordData: PasswordData) {
        val encryptedData =
            encryptPasswordData(passwordData)?.let { EncryptedData(encryptedPasswordData = it) }
        if (encryptedData != null) {
            encryptedDataDao.insertEncryptedData(encryptedData)
        }
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

    private suspend fun encryptPasswordData(passwordData: PasswordData): String? =
        withContext(Dispatchers.IO) {
            val pdEncoded = Gson().toJson(passwordData)
            passphrase?.let { Encrypter.encryptStringAndGetBase64(pdEncoded, it) }
        }

    private suspend fun decryptPasswordData(
        id: Int?,
        base64encodedPasswordData: String
    ): PasswordData? = withContext(Dispatchers.IO) {
        val base64DataArray = base64encodedPasswordData.split("\n")
        val pdEncrypted = base64DataArray[0].plus(base64DataArray[1])
        val salt = base64DataArray[2]
        val iv = base64DataArray[3]

        val pd = Gson().fromJson(passphrase?.let {
            Encrypter.decryptBase64String(
                pdEncrypted,
                it, salt, iv
            )
        }, PasswordData::class.java)
        pd?.id = id
        pd
    }


    fun getDatabasePasswordDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Database password")

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.hint = "Enter your current database password"
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            passphrase = input.text.toString()
        }

        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}
