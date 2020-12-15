package com.example.cryptguard.ui.generator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cryptguard.R
import kotlinx.android.synthetic.main.fragment_generator.*
import kotlinx.android.synthetic.main.fragment_generator.view.*
import java.lang.Integer.parseInt

class GeneratorFragment : Fragment() {

    private lateinit var generatorViewModel: GeneratorViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        generatorViewModel =
                ViewModelProvider(this).get(GeneratorViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_generator, container, false)
        val textView: TextView = root.findViewById(R.id.text_generator)
        generatorViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        root.copyToClipboardButton.setOnClickListener {
            val clipboard = root.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val password = root.password_edit_text.text

            if (password.toString().isEmpty()) {
                root.password_edit_text.error = "Please generate a password before copying."
                return@setOnClickListener
            }

            val clip = ClipData.newPlainText("Generated password", password)
            clipboard?.setPrimaryClip(clip)

            Toast.makeText(root.context, "Password successfully copied.", Toast.LENGTH_LONG)
                    .show()
        }

        root.generate_password_button.setOnClickListener {
            val charLenEditable = root.character_length_edit_text.text ?: return@setOnClickListener
            val lengthText = charLenEditable.toString()

            if (lengthText.isEmpty()){
                root.character_length_edit_text.error = "Please enter the value ranging from 8 up to 128 [included]."
                return@setOnClickListener
            }

            val length = parseInt(lengthText)
            if (length < 8 || length > 128) {
                root.character_length_edit_text.error = "Please enter the value ranging from 8 up to 128 [included]."
                return@setOnClickListener
            }

            val password = PasswordGenerator().generate(length)

            password_edit_text.setText(password)
        }
        return root
    }
}