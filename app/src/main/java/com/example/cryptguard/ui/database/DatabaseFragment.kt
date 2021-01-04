package com.example.cryptguard.ui.database

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cryptguard.R
import com.example.cryptguard.data.PasswordDataDatabase
import com.example.cryptguard.data.PasswordDataRepository
import kotlinx.android.synthetic.main.fragment_database.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking

class DatabaseFragment : Fragment() {

    private lateinit var databaseViewModel: DatabaseViewModel
    private var passwordRepo: PasswordDataRepository? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseViewModel =
            ViewModelProvider(this).get(DatabaseViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_database, container, false)
        val textView: TextView = root.findViewById(R.id.database_settings_text_view)
        passwordRepo = PasswordDataDatabase.getRepository(requireContext())
        val currDbPassEditText = root.findViewById<EditText>(R.id.current_database_password_edit_text)
        currDbPassEditText.setText(passwordRepo?.getDbPassphrase())

        runBlocking {
            if (passwordRepo?.getFirstEncryptedData() == null) {
                    currDbPassEditText.hint = getString(R.string.enter_new_database_password_text)
            }
        }

        root.findViewById<Button>(R.id.unlock_db_button).setOnClickListener {
            if (passwordEditTextValidate()) {
                passwordRepo?.setDbPassphrase(this.current_database_password_edit_text.text.toString())
                // When we're unlocking the database we should check if the password entered
                // is actually correct
                runBlocking {
                    if (passwordRepo != null && !passwordRepo?.verifyPassphrase()!!) {
                        passwordRepo?.setDbPassphrase(null)
                        currDbPassEditText.error =
                            "Wrong password, try again please."
                    } else {
                        Toast.makeText(requireContext(), "Database successfully unlocked.", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

        /*
        databaseViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        */
        return root
    }

    private fun passwordEditTextValidate(): Boolean {
        val passwordText = this.current_database_password_edit_text.text.trim()
        val passwordStrengthRegex =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;.',?/*~\$^+=<>]).{8,}\$".toRegex()

        if (passwordText.isEmpty() || passwordText.length < 8) {
            this.current_database_password_edit_text.error =
                "Password length has to be more than 8 characters."
            return false
        }

        if (!passwordText.contains(passwordStrengthRegex)) {
            this.current_database_password_edit_text.error =
                "Password should contain lowercase letters, uppercase letters, number and punctuation mark."
            return false
        }

        return true
    }
}