package com.example.cryptguard.ui.passwords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptguard.R

class PasswordsFragment : Fragment() {

    private lateinit var passwordsViewModel: PasswordsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_passwords, container, false)
        val passwordRecycler = rootView.findViewById<RecyclerView>(R.id.passwords_recycler)
        passwordRecycler.layoutManager = LinearLayoutManager(activity)
        passwordRecycler.adapter = PasswordsAdapter(PasswordDetailClickListener())
        passwordsViewModel = ViewModelProvider(this).get(PasswordsViewModel::class.java)
        return rootView
    }
}