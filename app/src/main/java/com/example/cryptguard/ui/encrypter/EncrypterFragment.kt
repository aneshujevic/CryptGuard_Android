package com.example.cryptguard.ui.encrypter

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cryptguard.R
import kotlinx.android.synthetic.main.fragment_encrypter.view.*
import java.io.File
import java.net.Authenticator


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
            val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 1)
        }

        root.decrypt_file_button.setOnClickListener {
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
}
