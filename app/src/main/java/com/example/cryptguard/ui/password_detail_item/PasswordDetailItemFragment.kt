package com.example.cryptguard.ui.password_detail_item

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cryptguard.R
import com.example.cryptguard.data.PasswordData
import com.example.cryptguard.data.PasswordDataDatabase
import com.example.cryptguard.data.PasswordDataRepo
import com.example.cryptguard.ui.passwords.PasswordsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.password_detail_item_fragment.view.*
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*


@InternalCoroutinesApi
class PasswordDetailItemFragment(private val id: Int?, private val fab: FloatingActionButton?) : Fragment() {
    private lateinit var viewModel: PasswordDetailItemViewModel
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.password_detail_item_fragment, container, false)

        val passDatabase = context?.let { PasswordDataDatabase.getDatabase(it) }
        if (passDatabase != null) {
            viewModel =
                PasswordDetailItemViewModelFactory(PasswordDataRepo(passDatabase.passwordDataDao())).create(
                    PasswordDetailItemViewModel::class.java
                )
        }

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
            root.edit_text_password_pass_detail
                ?.transformationMethod =
                if (root.edit_text_password_pass_detail.transformationMethod is PasswordTransformationMethod)
                    HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
        }

        if (id != null) {
            root.button_save_password.visibility = View.VISIBLE
            root.button_delete_password.visibility = View.VISIBLE
            root.button_add_password.visibility = View.INVISIBLE

            root.button_save_password.setOnClickListener {
                val pd = createPasswordData(id)
                if (validateCreateOrSaveChanges(pd)) {
                    showPasswordListFragment(it)
                }
            }

            root.button_delete_password.setOnClickListener {
                viewModel.removeChosenPasswordData(id)
                Toast.makeText(
                    requireContext(),
                    "Password data successfully removed.",
                    Toast.LENGTH_LONG
                )
                    .show()
                showPasswordListFragment(it)
            }

        } else {
            root.button_add_password.setOnClickListener {
                val pd = createPasswordData()
                if (validateCreateOrSaveChanges(pd)) {
                    showPasswordListFragment(it)
                }
            }
        }

        return root
    }

    @InternalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (id != null) {
            viewModel.setChosen(id)
        }

        val siteNameEditText = root.findViewById<EditText>(R.id.edit_text_site_name_pass_detail)
        val usernameEditText = root.findViewById<EditText>(R.id.edit_text_username_pass_detail)
        val emailEditText = root.findViewById<EditText>(R.id.edit_text_email_pass_detail)
        val passwordEditText = root.findViewById<EditText>(R.id.edit_text_password_pass_detail)
        val additionalDataEditText =
            root.findViewById<EditText>(R.id.edit_text_additional_data_pass_detail)

        viewModel.passwordData.observe(viewLifecycleOwner, {
            siteNameEditText.setText(it.siteName)
            usernameEditText.setText(it.username)
            emailEditText.setText(it.email)
            passwordEditText.setText(it.password)
            additionalDataEditText.setText(it.additionalData)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fab != null) {
            fab.visibility = View.VISIBLE
        }
    }

    private fun showPasswordListFragment(view: View) {
        val activity = view.context as AppCompatActivity
        val fragmentManager = activity.supportFragmentManager
        val passwordsFragment = PasswordsFragment()
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_passwords, passwordsFragment, "passwords")
            .commit()
    }

    private fun createPasswordData(id: Int? = null): PasswordData {
        return PasswordData(
            id = id,
            siteName = root.edit_text_site_name_pass_detail.text.toString(),
            username = root.edit_text_username_pass_detail.text.toString(),
            email = root.edit_text_email_pass_detail.text.toString(),
            password = root.edit_text_password_pass_detail.text.toString(),
            additionalData = root.edit_text_additional_data_pass_detail.text.toString()
        )
    }

    private fun validateCreateOrSaveChanges(passwordData: PasswordData): Boolean {
        if (!validatePasswordData(passwordData))
            return false

        if (id != null) {
            viewModel.updatePasswordData(passwordData)
            Toast.makeText(
                requireContext(),
                "Password data successfully updated.",
                Toast.LENGTH_LONG
            )
                .show()
        } else {
            viewModel.addPasswordData(passwordData)
            Toast.makeText(requireContext(), "Password data successfully added.", Toast.LENGTH_LONG)
                .show()
        }

        return true
    }

    private fun validatePasswordData(passwordData: PasswordData): Boolean {
        if (passwordData.siteName.isEmpty()) {
            root.edit_text_site_name_pass_detail.error = "Site name must not be empty."
            return false
        }

        if (passwordData.password.isEmpty()) {
            root.edit_text_password_pass_detail.error = "Password field must not be empty."
            return false
        }

        return true
    }

    private fun copyToClipboard(root: View, type: DataTypeCopying) {
        val clipboard =
            root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?

        val editText = when (type) {
            DataTypeCopying.USERNAME -> root.edit_text_username_pass_detail
            DataTypeCopying.PASSWORD -> root.edit_text_password_pass_detail
            DataTypeCopying.EMAIL -> root.edit_text_email_pass_detail
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

        Toast.makeText(
            root.context,
            "${dataTypeString.capitalize(Locale.getDefault())} successfully copied.",
            Toast.LENGTH_LONG
        )
            .show()
    }

    private enum class DataTypeCopying {
        USERNAME,
        PASSWORD,
        EMAIL
    }
}