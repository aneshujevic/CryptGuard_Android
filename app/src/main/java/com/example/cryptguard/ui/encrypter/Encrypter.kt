package com.example.cryptguard.ui.encrypter

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.InputStream
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Encrypter {
    // Encrypt the data from stream, encode it to base64 and append salt and iv to it (base64 too)
    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptAndGetBase64(input: InputStream, password: String): String {
        println(password)
        // create the salt
        val sr = SecureRandom.getInstanceStrong()
        val passwordSalt = ByteArray(16)
        sr.nextBytes(passwordSalt)

        // create the key for aes
        val keySpec = PBEKeySpec(password.toCharArray(), passwordSalt, 1000, 32 * 8)
        val key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512").generateSecret(keySpec)

        // create the cipher and initialise it with key
        val cipher = Cipher.getInstance("AES_256/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        // read from input stream and encrypt the data
        var result: ByteArray
        input.buffered().use {
            val bytes = it.readBytes()
            result = cipher.doFinal(bytes)
        }

        // return the encrypted data in base64
        return appendCipherDataToBase64(result, passwordSalt, cipher.iv)
    }

    private fun ByteArray.toBase64(): String {
        return android.util.Base64.encodeToString(this, android.util.Base64.DEFAULT).toString()
    }

    private fun ByteArray.fromBase64(): ByteArray? {
        return android.util.Base64.decode(this, android.util.Base64.DEFAULT)
    }

    private fun appendCipherDataToBase64( cipherText: ByteArray, passwordSalt: ByteArray, initVector: ByteArray): String {
        val cipherBase64 = cipherText.toBase64()
        val passwordSaltBase64 = passwordSalt.toBase64()
        val initVectorBase64 = initVector.toBase64()

        return cipherBase64.plus('\n' + passwordSaltBase64 + '\n' + initVectorBase64)
    }

    fun decrypt(input: InputStream, password: String) {
        TODO()
    }
}