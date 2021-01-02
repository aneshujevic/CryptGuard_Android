package com.example.cryptguard.ui.passwords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptguard.R
import com.example.cryptguard.data.PasswordData
import com.example.cryptguard.data.PasswordDataDatabase
import com.example.cryptguard.data.PasswordDataRepo
import com.example.cryptguard.ui.password_detail_item.PasswordDetailItemFragment
import kotlinx.android.synthetic.main.fragment_passwords.view.*
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
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
        val passAdapter = PasswordsAdapter(rootView.add_password_floating_button)
        passwordRecycler.adapter = passAdapter

        val passDatabase = context?.let { PasswordDataDatabase.getDatabase(it) }
        if (passDatabase != null) {
            passwordsViewModel =  PasswordsViewModel( PasswordDataRepo(passDatabase.passwordDataDao()) )
        }

        passwordsViewModel.getPasswordsDataObserver().observe(viewLifecycleOwner, {
                passAdapter.setPasswords(it as ArrayList<PasswordData>)
                if (passAdapter.itemCount != 0) {
                    rootView.findViewById<TextView>(R.id.empty_passwords_recycler_text).visibility = View.INVISIBLE
                }
                passAdapter.notifyDataSetChanged()
        })

        rootView.add_password_floating_button.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val fragmentManager = activity.supportFragmentManager

            // check if we're already in password detail fragment
            val addPasswordFrag = fragmentManager.findFragmentByTag("password_details")
            if (addPasswordFrag != null) {
                return@setOnClickListener
            }

            rootView.add_password_floating_button.visibility = View.INVISIBLE

            val passwordDetailItem = PasswordDetailItemFragment(null, rootView.add_password_floating_button)
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_passwords, passwordDetailItem, "password_details")
                .addToBackStack("passwords")
                .commit()
        }

        return rootView
    }
}