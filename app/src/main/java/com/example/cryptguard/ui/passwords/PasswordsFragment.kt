package com.example.cryptguard.ui.passwords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val passAdapter = PasswordsAdapter()
        passwordRecycler.adapter = passAdapter

        val passDatabase = context?.let { PasswordDataDatabase.getDatabase(it) }
        if (passDatabase != null) {
            passwordsViewModel =  PasswordsViewModel( PasswordDataRepo(passDatabase.passwordDataDao()) )
        }

        passwordsViewModel.getPasswordsDataObserver().observe(viewLifecycleOwner, {
                passAdapter.setPasswords(it as ArrayList<PasswordData>)
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