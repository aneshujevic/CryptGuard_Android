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
import androidx.lifecycle.ViewModelProvider
import com.example.cryptguard.R
import kotlinx.android.synthetic.main.fragment_encrypter.*
import kotlinx.android.synthetic.main.fragment_encrypter.view.*


class EncrypterFragment : Fragment() {

    private lateinit var encrypterViewModel: EncrypterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        encrypterViewModel =
                ViewModelProvider(this).get(EncrypterViewModel::class.java)
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
                .setAction(Intent.ACTION_OPEN_DOCUMENT_TREE)

            startActivityForResult(Intent.createChooser(intent, "Select a directory to save"), 2)
        }

        return root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // TODO: implement encrypting/decrypting and creating file
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.data?.let {
                var encryptedBase64: String = ""
                val line = context?.contentResolver?.openInputStream(it)?.buffered().use { fileStream ->
                    if (fileStream != null) {
                        encryptedBase64 = Encrypter.encryptAndGetBase64(fileStream, this.password_encrypter_edit_text.text.toString())
                    }
                }

                if (encryptedBase64 == "") {
                    Toast.makeText(context, "Failed encrypting a selected file.", Toast.LENGTH_LONG).show()
                    return
                }

                // creates values of a to be file
                val resolver = context?.contentResolver
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
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            data?.data?.let {
                // reads a file
                val line = context?.contentResolver?.openInputStream(it)?.bufferedReader().use { it!!.readText() }

                // creates a file
                val resolver = context?.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "test.txt")
                }

                val uri = resolver?.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                resolver!!.openOutputStream(uri!!).use {
                    it?.write("hello".toByteArray())
                }
            }
        }
    }

    private fun passwordEditTextValidate(): Boolean {
        val passwordText = this.password_encrypter_edit_text.text
        if (passwordText.trim().isEmpty() || passwordText.length < 8) {
            this.password_encrypter_edit_text.setError("Password length has to be more than 8 characters.")
            return false
        }
        return true
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = context?.contentResolver?.query(uri, null, null, null, null)

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
