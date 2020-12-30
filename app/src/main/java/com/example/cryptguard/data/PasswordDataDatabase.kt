package com.example.cryptguard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [PasswordData::class], version = 1)
abstract class PasswordDataDatabase : RoomDatabase(){

    abstract fun passwordDataDao(): PasswordDataDao

    companion object {
        @Volatile
        private var INSTANCE: PasswordDataDatabase? = null

        @InternalCoroutinesApi
        fun getDatabase(context: Context): PasswordDataDatabase? {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PasswordDataDatabase::class.java,
                    "pass_db"
                ).allowMainThreadQueries().build()
                INSTANCE = instance

                instance
            }
        }

        fun destroyPasswordDatabase() {
            INSTANCE = null
        }
    }
}