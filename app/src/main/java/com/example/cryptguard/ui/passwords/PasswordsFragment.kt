package com.example.cryptguard.ui.passwords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptguard.R
import com.example.cryptguard.ui.password_detail_item.PasswordDetailItemFragment
import kotlinx.android.synthetic.main.fragment_passwords.view.*

class PasswordsFragment : Fragment() {

    private lateinit var passwordsViewModel: PasswordsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_passwords, container, false)
        val passwordRecycler = rootView.findViewById<RecyclerView>(R.id.passwords_recycler)
        passwordRecycler.layoutManager = LinearLayoutManager(requireActivity())
        val passAdapter = PasswordsAdapter()
        passwordRecycler.adapter = passAdapter
        passwordsViewModel = ViewModelProvider(this).get(PasswordsViewModel::class.java)
        passwordsViewModel.getPasswordsDataObserver().observe(viewLifecycleOwner, {
                passAdapter.setPasswords(it)
                passAdapter.notifyDataSetChanged()
        })
        passwordsViewModel.updatePasswords()

        rootView.add_password_item.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val fragmentManager = activity.supportFragmentManager
            val passwordDetailItem = PasswordDetailItemFragment(null)
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_passwords, passwordDetailItem, "passwords")
                .addToBackStack("passwords")
                .commit()
        }

        return rootView
    }
}