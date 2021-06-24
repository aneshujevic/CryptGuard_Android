package com.example.cryptguard.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.time.LocalDateTime

class DatabaseUtils {
    companion object {
        private var instance: DatabaseUtils? = null

        fun getInstance(): DatabaseUtils {
            if (instance == null) {
                instance = DatabaseUtils()
            }
            return instance!!
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        @InternalCoroutinesApi
        suspend fun createDatabaseBackup(context: Context) {
            // creates values of a to be file
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    LocalDateTime.now().toString() + ".cryptguard.db"
                )
            }

            // creates the file and writes the data into it
            val uri = resolver?.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                val passwordDataRepository = PasswordDataDatabase.getRepository(context)
                resolver.openOutputStream(uri)?.writer().use {
                    passwordDataRepository?.getAllEncryptedData()?.forEach { encryptedData ->
                        it?.write(encryptedData?.id.toString() + "," + encryptedData?.encryptedPasswordData?.replace("\n", ".") + "\n")
                    }
                }
            }
        }

        @InternalCoroutinesApi
        @RequiresApi(Build.VERSION_CODES.O)
        fun importDatabaseFromFile(it: Uri, context: Context) {
            runBlocking {
                val dbRepo = PasswordDataDatabase.getRepository(context)
                dbRepo?.deleteAllEncryptedData()

                try {
                    context.contentResolver.openInputStream(it)?.bufferedReader()
                        .use { buffReader ->
                            buffReader?.readLines()?.forEach { dbString ->
                                val id = dbString.split(",")[0].toInt()
                                val encData = dbString.split(",")[1].replace(".", "\n")
                                dbRepo?.addEncryptedData(EncryptedPasswordData(id, encData))
                            }
                        }
                    Toast.makeText(
                        context,
                        "Database imported successfully.",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "There was problem importing database, please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        @InternalCoroutinesApi
        @RequiresApi(Build.VERSION_CODES.O)
        fun importDatabaseFromHTTPResponse(response: Response<ResponseBody>, context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val dbRepo = PasswordDataDatabase.getRepository(context)
                dbRepo?.deleteAllEncryptedData()

                response.body()?.string()?.reader()?.readLines()?.forEach { dbString ->
                    val id = dbString.split(",")[0].toInt()
                    val encData = dbString.split(",")[1].replace(".", "\n")
                    dbRepo?.addEncryptedData(EncryptedPasswordData(id, encData))
                }
            }
        }
    }
}