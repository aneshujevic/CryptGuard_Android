package com.example.cryptguard.data

import androidx.room.*

@Dao
interface PasswordDataDao {
    @Query("SELECT * FROM PasswordData WHERE siteName == :siteName")
    fun getPasswordDataFromSiteName(siteName: String): PasswordData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPasswordData(passwordData: PasswordData)

    @Update
    fun updatePasswordData(passwordData: PasswordData)

    @Delete
    fun deletePasswordData(passwordData: PasswordData)

    @Query("SELECT * FROM PasswordData")
    fun getAllPasswordData(): List<PasswordData>

    @Query("DELETE FROM PasswordData WHERE id == :position")
    fun removePasswordDataById(position: Int)

    @Query("SELECT * FROM PasswordData WHERE id == :position")
    fun getPasswordDataById(position: Int): PasswordData
}