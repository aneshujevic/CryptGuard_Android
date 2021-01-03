package com.example.cryptguard.ui.passwords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptguard.R
import com.example.cryptguard.data.PasswordData
import com.example.cryptguard.ui.password_detail_item.PasswordDetailItemFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class PasswordsAdapter(private val fab: FloatingActionButton): RecyclerView.Adapter<PasswordItemViewHolder>() {
    private var data = ArrayList<PasswordData?>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
   lateinit var parentView: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
                .inflate(R.layout.password_item, parent, false)
        parentView = parent

        return PasswordItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasswordItemViewHolder, position: Int) {
        if (data[0] == null)
            return
        val item = data[position] ?: return
        val id = item.id
        holder.siteName.text = item.siteName
        holder.username.text = item.username

        holder.showButton.setOnClickListener{
            val activity = it.context as AppCompatActivity
            val fragmentManager = activity.supportFragmentManager
            val passDetailFrag = PasswordDetailItemFragment(id, fab)
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_passwords, passDetailFrag, "details")
                .addToBackStack("details")
                .commit()
        }
    }

    override fun getItemCount() = data.size

    fun setPasswords(passwords: ArrayList<PasswordData?>) {
        this.data = passwords
    }
}