package com.example.cryptguard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [EncryptedPasswordData::class], version = 1)
abstract class PasswordDataDatabase : RoomDatabase(){

    abstract fun passwordDataDao(): EncryptedDataDao

    companion object {
        @Volatile
        private var INSTANCE: PasswordDataDatabase? = null
        @Volatile
        private var REPO_INSTANCE: PasswordDataRepository? = null

        @InternalCoroutinesApi
        fun getDatabase(context: Context): PasswordDataDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PasswordDataDatabase::class.java,
                    "pass_db"
                ).build()
                INSTANCE = instance

                instance
            }
        }

        @InternalCoroutinesApi
        fun getRepository(context: Context): PasswordDataRepository? {
            return REPO_INSTANCE ?: synchronized(this) {
                REPO_INSTANCE = PasswordDataRepository(PasswordDataDatabase.getDatabase(context).passwordDataDao())
                REPO_INSTANCE
            }
        }

        fun destroyPasswordDatabase() {
            INSTANCE = null
        }
    }
}