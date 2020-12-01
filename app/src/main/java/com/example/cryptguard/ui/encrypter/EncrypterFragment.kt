package com.example.cryptguard.ui.encrypter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cryptguard.R

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
        val textView: TextView = root.findViewById(R.id.text_encrypter)
        encrypterViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}