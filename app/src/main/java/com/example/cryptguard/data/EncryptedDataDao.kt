package com.example.cryptguard.data

import androidx.room.*

@Dao
interface EncryptedDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEncryptedData(encryptedPasswordPasswordData: EncryptedPasswordData)

    @Update
    fun updateEncryptedData(encryptedPasswordPasswordData: EncryptedPasswordData)

    @Delete
    fun deleteEncryptedData(encryptedPasswordPasswordData: EncryptedPasswordData)

    @Query("SELECT * FROM EncryptedPasswordData")
    fun getAllEncryptedData(): List<EncryptedPasswordData>

    @Query("DELETE FROM EncryptedPasswordData WHERE id == :position")
    fun removeEncryptedDataById(position: Int)

    @Query("SELECT * FROM EncryptedPasswordData WHERE id == :position")
    fun getEncryptedDataById(position: Int): EncryptedPasswordData

    @Query("SELECT * FROM EncryptedPasswordData LIMIT 1")
    fun getFirstEncryptedData(): EncryptedPasswordData?
}