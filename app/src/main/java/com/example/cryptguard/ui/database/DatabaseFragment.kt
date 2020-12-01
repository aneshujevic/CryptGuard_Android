package com.example.cryptguard.ui.database

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cryptguard.R

class DatabaseFragment : Fragment() {

    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        databaseViewModel =
                ViewModelProvider(this).get(DatabaseViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_database, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        databaseViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}