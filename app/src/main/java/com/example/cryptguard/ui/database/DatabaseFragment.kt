package com.example.cryptguard.ui.database

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cryptguard.R
import com.example.cryptguard.data.DatabaseUtils
import com.example.cryptguard.data.EncryptedPasswordData
import com.example.cryptguard.data.PasswordDataDatabase
import com.example.cryptguard.data.PasswordDataRepository
import kotlinx.android.synthetic.main.fragment_database.view.*
import kotlinx.android.synthetic.main.fragment_encrypter.*
import kotlinx.android.synthetic.main.fragment_encrypter.view.*
import kotlinx.coroutines.*

class DatabaseFragment : Fragment() {
    private lateinit var loadingView: View
    private lateinit var loadingProgressBar: ProgressBar
    private var passwordRepo: PasswordDataRepository? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_database, container, false)
        loadingView = root.findViewById(R.id.encrypting_database_view)
        loadingProgressBar = root.findViewById(R.id.encryption_progress_bar)
        val currDbPassEditText =
            root.findViewById<EditText>(R.id.current_database_password_edit_text)
        passwordRepo = PasswordDataDatabase.getRepository(requireContext())
        currDbPassEditText.setText(passwordRepo?.getDbPassphrase())

        runBlocking {
            if (passwordRepo?.getFirstEncryptedData() == null) {
                currDbPassEditText.hint = getString(R.string.enter_new_database_password_text)
                root.lock_db_button.visibility = View.INVISIBLE
                root.unlock_db_button.text = getString(R.string.create_text)
            }
        }

        root.unlock_db_button.setOnClickListener {
            setLoadingScreen()
            if (passwordEditTextValidate(currDbPassEditText)) {
                passwordRepo?.setDbPassphrase(currDbPassEditText.text.toString())
                // When we're unlocking the database we should check if the password entered
                // is actually correct
                runBlocking {
                    if (passwordRepo != null && !passwordRepo?.verifyPassphrase()!!) {
                        passwordRepo?.setDbPassphrase(null)
                        currDbPassEditText.error =
                            "Wrong password, try again please."
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Database successfully unlocked.",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        findNavController().navigate(R.id.mob_nav_passwords)
                        root.lock_db_button.visibility = View.VISIBLE
                        root.unlock_db_button.text = getString(R.string.unlock_text)
                    }
                    unsetLoadingScreen()
                }
            } else {
                unsetLoadingScreen()
            }
        }

        root.lock_db_button.setOnClickListener {
            setLoadingScreen()
            passwordRepo?.setDbPassphrase(null)
            currDbPassEditText.setText("")
            Toast.makeText(requireContext(), "Database successfully locked.", Toast.LENGTH_SHORT)
                .show()
            unsetLoadingScreen()
        }

        root.change_db_password_button.setOnClickListener {
            val newDbPassEditText =
                root.findViewById<EditText>(R.id.new_database_password_edit_text)

            setLoadingScreen()
            runBlocking {
                if (passwordRepo?.getDbPassphrase() == null) {
                    Toast.makeText(
                        requireContext(),
                        "Database is locked, try unlocking it first.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@runBlocking
                }

                if (passwordEditTextValidate(newDbPassEditText)
                    && passwordEditTextValidate(currDbPassEditText)
                    && passwordRepo != null
                    && passwordRepo?.verifyPassphrase()!!
                ) {
                    val newPassword = newDbPassEditText.text.toString()
                    if (passwordRepo!!.encryptDatabase(newPassword)) {
                        passwordRepo!!.setDbPassphrase(newPassword)
                        newDbPassEditText.setText("")
                        currDbPassEditText.setText(newPassword)
                        Toast.makeText(
                            requireContext(),
                            "Password successfully changed.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed changing password, please try again",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            unsetLoadingScreen()
        }

        root.import_db_button.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("ALERT")
            builder.setIcon(R.drawable.ic_baseline_report_24)
            val textView = TextView(requireContext())
            textView.text = getString(R.string.import_db_alert_text)
            textView.setPadding(120, 5, 25, 0)

            builder.setView(textView)
            builder.setNegativeButton("No") { _, _ -> }
            builder.setPositiveButton("Yes") { _, _ ->
                val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)

                startActivityForResult(Intent.createChooser(intent, "Select a file"), 1)
                root.lock_db_button.visibility = View.VISIBLE
                root.unlock_db_button.text = getString(R.string.unlock_text)
            }

            builder.show()
        }

        root.export_db_button.setOnClickListener {
            val dbUtil = DatabaseUtils.getInstance()
            runBlocking {
                try {
                    dbUtil.createDatabaseBackup(requireContext())
                    Toast.makeText(
                        requireContext(),
                        "Database exported to Downloads successfully.",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "There was trouble exporting database, please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        root.drop_current_db_button.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("WARNING")
            builder.setIcon(R.drawable.ic_baseline_report_24)
            val textView = TextView(requireContext())
            textView.text = getString(R.string.warning_delete_database_text)
            textView.setPadding(120, 5, 25, 0)

            builder.setView(textView)
            builder.setNegativeButton("No") { _, _ -> }
            builder.setPositiveButton("Yes") { _, _ ->
                runBlocking {
                    passwordRepo?.deleteAllEncryptedData()
                    root.lock_db_button.visibility = View.INVISIBLE
                    root.unlock_db_button.text = getString(R.string.create_text)
                }
            }

            builder.show()
        }

        return root
    }

    @InternalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                runBlocking {
                    val dbRepo = PasswordDataDatabase.getRepository(requireContext())
                    dbRepo?.deleteAllEncryptedData()

                    try {
                        requireContext().contentResolver.openInputStream(it)?.bufferedReader()
                            .use { buffReader ->
                                buffReader?.readLines()?.forEach { dbString ->
                                    val id = dbString.split(",")[0].toInt()
                                    val encData = dbString.split(",")[1].replace(".", "\n")
                                    dbRepo?.addEncryptedData(EncryptedPasswordData(id, encData))
                                }
                            }
                        Toast.makeText(
                            requireContext(),
                            "Database imported successfully.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "There was problem importing database, please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }


    private fun setLoadingScreen() {
        loadingProgressBar.visibility = View.VISIBLE
        loadingView.visibility = View.VISIBLE
    }

    private fun unsetLoadingScreen() {
        loadingProgressBar.visibility = View.INVISIBLE
        loadingView.visibility = View.INVISIBLE
    }

    private fun passwordEditTextValidate(editText: EditText): Boolean {
        val passwordText = editText.text.trim()
        val passwordStrengthRegex =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;.',?/*~\$^+=<>]).{8,}\$".toRegex()

        if (passwordText.isEmpty() || passwordText.length < 8) {
            editText.error =
                "Password length has to be more than 8 characters."
            return false
        }

        if (!passwordText.contains(passwordStrengthRegex)) {
            editText.error =
                "Password should contain lowercase letters, uppercase letters, number and punctuation mark."
            return false
        }

        return true
    }
}