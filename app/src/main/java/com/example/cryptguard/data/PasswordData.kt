package com.example.cryptguard.data

data class PasswordData(
    var id: Int?,
    var siteName: String,
    var username: String,
    var email: String,
    var password: String,
    var additionalData: String
) { }
