package com.example.cryptguard.ui.encrypter

import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.cryptguard.R
import kotlinx.android.synthetic.main.fragment_encrypter.*
import kotlinx.android.synthetic.main.fragment_encrypter.view.*
import javax.crypto.AEADBadTagException


class EncrypterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_encrypter, container, false)

        root.encrypt_file_button.setOnClickListener {
            if (!passwordEditTextValidate())
                return@setOnClickListener

            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 1)
        }

        root.decrypt_file_button.setOnClickListener {
            if (!passwordEditTextValidate())
                return@setOnClickListener

            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 2)
        }

        return root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.data?.let {
                var encryptedBase64: String = ""
                val line = requireContext().contentResolver?.openInputStream(it)?.buffered()
                    .use { fileStream ->
                        if (fileStream != null) {
                            encryptedBase64 = Encrypter.encryptStreamAndGetBase64(
                                fileStream,
                                this.password_encrypter_edit_text.text.toString()
                            )
                        }
                    }

                // creates values of a to be file
                val resolver = requireContext().contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, getFileName(it) + ".cgfe")
                }

                // creates the file and writes the data into it
                val uri = resolver?.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.writer().use {
                        it?.write(encryptedBase64)
                    }
                }

                this.password_encrypter_edit_text.setText("")
                Toast.makeText(context, "Successfully encrypted the file!", Toast.LENGTH_LONG)
                    .show()
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            data?.data?.let {
                var plainText: String = ""
                requireContext().contentResolver?.openInputStream(it)?.buffered()
                    .use { fileStream ->
                        if (fileStream != null) {
                            try {
                                plainText = Encrypter.decryptStream(
                                    fileStream,
                                    this.password_encrypter_edit_text.text.toString()
                                )
                            } catch (ex: AEADBadTagException) {
                                Toast.makeText(
                                    requireContext(),
                                    "Wrong password, please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return
                            }
                        }
                    }

                // creates values of a to be file
                val resolver = requireContext().contentResolver
                val contentValues = ContentValues().apply {
                    put(
                        MediaStore.MediaColumns.DISPLAY_NAME,
                        getFileName(it)?.removeSuffix(".cgfe")
                    )
                }

                // creates the file and writes the data into it
                val uri = resolver?.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.writer().use {
                        it?.write(plainText)
                    }
                }

                this.password_encrypter_edit_text.setText("")
                Toast.makeText(context, "Successfully decrypted the file!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun passwordEditTextValidate(): Boolean {
        val passwordText = this.password_encrypter_edit_text.text.trim()
        val passwordStrengthRegex =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~\$^+=<>]).{8,}\$".toRegex()

        if (passwordText.isEmpty() || passwordText.length < 8) {
            this.password_encrypter_edit_text.error =
                "Password length has to be more than 8 characters."
            return false
        }

        if (!passwordText.contains(passwordStrengthRegex)) {
            this.password_encrypter_edit_text.error =
                "Password should contain lowercase letters, uppercase letters, number and punctuation mark."
            return false
        }

        return true
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? =
                requireContext().contentResolver?.query(uri, null, null, null, null)

            cursor.use {
                if (it != null && it.moveToFirst()) {
                    if (cursor != null) {
                        result = it.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }

        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (result != null) {
                    result = cut?.plus(1)?.let { result!!.substring(it) }
                }
            }
        }
        return result
    }
}
