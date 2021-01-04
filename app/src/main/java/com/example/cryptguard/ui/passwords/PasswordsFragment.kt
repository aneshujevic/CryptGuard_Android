package com.example.cryptguard.ui.passwords

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptguard.R
import com.example.cryptguard.data.PasswordData
import com.example.cryptguard.data.PasswordDataDatabase
import com.example.cryptguard.ui.password_detail_item.PasswordDetailItemFragment
import kotlinx.android.synthetic.main.fragment_passwords.view.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking

@InternalCoroutinesApi
class PasswordsFragment : Fragment() {

    private lateinit var passwordsViewModel: PasswordsViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (PasswordDataDatabase.getRepository(requireContext())?.getDbPassphrase() == null) {
            Toast.makeText(requireContext(), "Please enter the passphrase to unlock your database.", Toast.LENGTH_LONG)
                .show()
            findNavController().navigate(R.id.mob_nav_database)
        }

        val rootView = inflater.inflate(R.layout.fragment_passwords, container, false)

        val passwordRecycler = rootView.findViewById<RecyclerView>(R.id.passwords_recycler)
        passwordRecycler.layoutManager = LinearLayoutManager(requireActivity())
        val passAdapter = PasswordsAdapter(rootView.add_password_floating_button)
        passwordRecycler.adapter = passAdapter

        runBlocking {
            passwordsViewModel = PasswordDataDatabase.getRepository(requireContext())?.let {
                PasswordsViewModel(
                    it,
                    requireContext()
                )
            }!!
        }
        passwordsViewModel.getPasswordsDataObserver().observe(viewLifecycleOwner, {
                passAdapter.setPasswords(it as ArrayList<PasswordData?>)
                if (passAdapter.itemCount != 0) {
                    rootView.findViewById<TextView>(R.id.empty_passwords_recycler_text).visibility = View.INVISIBLE
                }
                passAdapter.notifyDataSetChanged()
        })

        rootView.add_password_floating_button.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val fragmentManager = activity.supportFragmentManager

            val passwordDetailItem = PasswordDetailItemFragment(null, rootView.add_password_floating_button)
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_passwords, passwordDetailItem, "password_details")
                .addToBackStack("passwords")
                .commit()
        }

        return rootView
    }
}