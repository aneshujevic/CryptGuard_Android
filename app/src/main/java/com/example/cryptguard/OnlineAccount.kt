package com.example.cryptguard

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.room.Database
import com.example.cryptguard.data.DatabaseUtils
import com.example.cryptguard.data.PasswordDataDatabase
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit


class OnlineAccount : Fragment() {
    private val serverURL: String = "http://192.168.1.9:8080"
    private var tokenJWT: String = ""
    private lateinit var retrofit: Retrofit
    private lateinit var service: APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @InternalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Create Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl(serverURL)
            .build()

        // Create Service
        service = retrofit.create(APIService::class.java)

        val v = inflater.inflate(R.layout.fragment_online_account, container, false)

        val reqPassBtn = v.findViewById<Button>(R.id.requestLoginPassButton)
        reqPassBtn.setOnClickListener {
            val username = v.findViewById<EditText>(R.id.usernameLoginEditText).text

            val fields: HashMap<String?, RequestBody?> = HashMap()
            fields["username"] = username.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            formDataPostServer(fields, ApiType.REQUEST_LOGIN)
        }

        val loginBtn = v.findViewById<Button>(R.id.logInButton)
        loginBtn.setOnClickListener {              // Create JSON using JSONObject
            val username = v.findViewById<EditText>(R.id.usernameLoginEditText).text
            val password = v.findViewById<EditText>(R.id.passwordRegistrationEditText).text

            val fields: HashMap<String?, RequestBody?> = HashMap()
            fields["username"] = username.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            fields["password"] = password.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            formDataPostServer(fields, ApiType.LOGIN)
        }

        val uploadDbBtn = v.findViewById<Button>(R.id.uploadDBButton)
        uploadDbBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val passwordDataRepository = PasswordDataDatabase.getRepository(requireContext())
                var databaseOctet: String = ""
                passwordDataRepository?.getAllEncryptedData()?.forEach { encryptedData ->
                    databaseOctet = databaseOctet.plus(
                        encryptedData?.id.toString() + "," + encryptedData?.encryptedPasswordData?.replace(
                            "\n",
                            "."
                        ) + "\n"
                    )
                }

                val fields: HashMap<String?, RequestBody?> = HashMap()
                fields["file\"; filename=\"database.db.cryptguard"] =
                    databaseOctet.toRequestBody("application/octet-stream".toMediaTypeOrNull())

                formDataPostServer(fields, ApiType.UPLOAD_DB)
            }
        }

        val downloadDbBtn = v.findViewById<Button>(R.id.downloadDBButton)
        downloadDbBtn.setOnClickListener {
            getDatabaseServer()
        }

        val registerBtn = v.findViewById<Button>(R.id.registerButton)
        registerBtn.setOnClickListener {
            val username = v.findViewById<EditText>(R.id.usernameLoginEditText).text
            val email = v.findViewById<EditText>(R.id.emailRegisterEditText).text

            val fields: HashMap<String?, RequestBody?> = HashMap()
            fields["username"] = username.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            fields["email"] = email.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            formDataPostServer(fields, ApiType.REGISTER)
        }

        return v
    }

    private fun formDataPostServer(fields: HashMap<String?, RequestBody?>, type: ApiType) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = when (type) {
                ApiType.REGISTER -> service.register(fields)
                ApiType.LOGIN -> service.login(fields)
                ApiType.REQUEST_LOGIN -> service.requestLogin(fields)
                else -> service.uploadDB(tokenJWT, fields)
            }

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                        )
                    )

                    Log.d("Pretty Printed JSON :", prettyJson)
                    if (type == ApiType.LOGIN) {
                        tokenJWT = "Bearer".plus(
                            prettyJson.split(":")[1].removeSuffix("\n}").replace("\"", "")
                        )
                        Toast.makeText(
                            requireContext(),
                            "Successfully logged in!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            prettyJson.split(":")[1].removeSuffix("\n}").replace("\"", ""),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())
                    Log.e("Retrofit_ERROR_BODY", response.errorBody().toString())
                    var message: String? = null
                    try {
                        message = response.errorBody().toString().split(":")[1]
                    } catch (e: Exception) {
                    }

                    if (response.body().toString().isNotEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            message ?: response.body().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    @InternalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDatabaseServer() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.downloadDB(tokenJWT)
            // TODO: fix unlock-create bug after downloading database :)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    DatabaseUtils.importDatabaseFromHTTPResponse(response, requireContext())

                    Toast.makeText(
                        requireContext(),
                        "Successfully downloaded database!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())
                    Toast.makeText(
                        requireContext(),
                        "Error while downloading database!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnlineAccount().apply { }
    }
}

enum class ApiType {
    REGISTER,
    LOGIN,
    REQUEST_LOGIN,
    UPLOAD_DB,
    DOWNLOAD_DB,
}
