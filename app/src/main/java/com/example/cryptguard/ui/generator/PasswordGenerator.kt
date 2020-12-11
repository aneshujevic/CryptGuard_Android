package com.example.cryptguard.ui.generator

import java.lang.Integer.parseInt
import java.lang.StringBuilder
import java.security.SecureRandom
import java.util.*

class PasswordGenerator {
    private val lowerCase = "abcdefghijklmnopqrstuvwxyz"
    private val uppercase =  "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val digits = "0123456789"
    private val other = """!"#$%&'()*+,-./:;<=>[]^_{|}~"""
    private val shuffleIndexGenerator = SecureRandom()
    private val indexGenerator = SecureRandom()

    private fun String.shuffle(): String {
        val sb = StringBuilder(this)
        var n = this.length

        while (n > 1) {
            val source = shuffleIndexGenerator.nextInt(n--)
            val tmp = sb[n]
            sb[n] = sb[source]
            sb[source] = tmp
        }

        return sb.toString()
    }

    fun generate(passwordLength: Int): String {
        val charsets = listOf(lowerCase, uppercase, digits, other)
        val passwordSB = StringBuilder(passwordLength)

         repeat(passwordLength - 1) {
            val charsetIndex = indexGenerator.nextInt(charsets.size)
            val charIndex = indexGenerator.nextInt(charsets[charsetIndex].length)
            passwordSB.append(charsets[charsetIndex][charIndex])
        }

        var password = passwordSB.toString()
        repeat(10) { password = password.shuffle() }

        return password
    }
}
