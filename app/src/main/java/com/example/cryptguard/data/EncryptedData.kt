package com.example.cryptguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EncryptedData(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var encryptedPasswordData: String
) { }
