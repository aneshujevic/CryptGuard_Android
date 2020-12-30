package com.example.cryptguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PasswordData(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var siteName: String,
    var username: String,
    var email: String,
    var password: String,
    var additionalData: String
) { }
