package com.example.cryptguard.ui.generator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cryptguard.R

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
        return root
    }
}