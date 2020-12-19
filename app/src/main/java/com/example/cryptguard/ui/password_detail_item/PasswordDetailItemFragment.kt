package com.example.cryptguard.ui.password_detail_item

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.cryptguard.R
import kotlinx.android.synthetic.main.password_detail_item_fragment.view.*
import java.util.*

class PasswordDetailItemFragment(private val position: Int) : Fragment() {
    private lateinit var viewModel: PasswordDetailItemViewModel
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.password_detail_item_fragment, container, false)

        root.button_copy_username_to_clipboard.setOnClickListener {
            copyToClipboard(root, DataTypeCopying.USERNAME)
        }

        root.button_copy_password_to_clipboard.setOnClickListener {
            copyToClipboard(root, DataTypeCopying.PASSWORD)
        }

        root.button_copy_email_to_clipboard.setOnClickListener {
            copyToClipboard(root, DataTypeCopying.EMAIL)
        }

        root.button_show_password.setOnClickListener {
            root.edit_text_password
                ?.transformationMethod = if (root.edit_text_password.transformationMethod is PasswordTransformationMethod)
                HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PasswordDetailItemViewModel::class.java)
        viewModel.setChosen(position)
        viewModel.passwordData.value?.let { Log.d("ok", it.siteName) }

        val siteNameEditText= root.findViewById<EditText>(R.id.edit_text_site_name)
        val usernameEditText = root.findViewById<EditText>(R.id.edit_text_username)
        val emailEditText = root.findViewById<EditText>(R.id.edit_text_email)
        val passwordEditText = root.findViewById<EditText>(R.id.edit_text_password)
        val additionalDataEditText = root.findViewById<EditText>(R.id.edit_text_additional_data)

        viewModel.passwordData.observe(viewLifecycleOwner, {
            siteNameEditText.setText(it.siteName)
            usernameEditText.setText(it.username)
            emailEditText.setText(it.email)
            passwordEditText.setText(it.password)
            additionalDataEditText.setText(it.additionalData)
        })
    }

    private fun copyToClipboard(root: View, type: DataTypeCopying) {
        val clipboard = root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val editText = when (type) {
            DataTypeCopying.USERNAME -> root.edit_text_username
            DataTypeCopying.PASSWORD -> root.edit_text_password
            DataTypeCopying.EMAIL -> root.edit_text_email
        }

        val dataTypeString = when (type) {
            DataTypeCopying.USERNAME -> "username"
            DataTypeCopying.PASSWORD -> "password"
            DataTypeCopying.EMAIL -> "email"
        }

        val data = editText.text

        if (data.toString().isEmpty()) {
            editText.error = "Please enter a $dataTypeString before copying."
            return
        }

        val clip = ClipData.newPlainText("Copied data", data)
        clipboard?.setPrimaryClip(clip)

        Toast.makeText(root.context,
            "${dataTypeString.capitalize(Locale.getDefault())} successfully copied.", Toast.LENGTH_LONG)
            .show()
    }

    private enum class DataTypeCopying (val type: Int) {
        USERNAME(0),
        PASSWORD(1),
        EMAIL(2)
    }
}