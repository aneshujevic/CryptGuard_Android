package com.example.cryptguard.data

import androidx.room.*

@Dao
interface EncryptedDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEncryptedData(encryptedPasswordData: EncryptedData)

    @Update
    fun updateEncryptedData(encryptedPasswordData: EncryptedData)

    @Delete
    fun deleteEncryptedData(encryptedPasswordData: EncryptedData)

    @Query("SELECT * FROM EncryptedData")
    fun getAllEncryptedData(): List<EncryptedData>

    @Query("DELETE FROM EncryptedData WHERE id == :position")
    fun removeEncryptedDataById(position: Int)

    @Query("SELECT * FROM EncryptedData WHERE id == :position")
    fun getEncryptedDataById(position: Int): EncryptedData
}