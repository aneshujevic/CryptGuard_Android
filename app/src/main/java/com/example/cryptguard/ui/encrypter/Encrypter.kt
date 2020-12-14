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
    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptAndGetBase64(input: InputStream, password: String): String {
        // create the salt
        val sr = SecureRandom.getInstanceStrong()
        val salt = ByteArray(16)
        sr.nextBytes(salt)

        // create the key for aes
        val keySpec = PBEKeySpec(password.toCharArray(), salt, 1000, 32 * 8)
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

        // return the data in base64
        return Base64.getEncoder().encodeToString(result)
    }

    fun decrypt(input: InputStream, password: String) {
        TODO()
    }
}