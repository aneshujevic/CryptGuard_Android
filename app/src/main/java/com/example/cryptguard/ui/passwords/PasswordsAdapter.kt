package com.example.cryptguard.ui.passwords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptguard.R
import com.example.cryptguard.data.PasswordData
import com.example.cryptguard.ui.password_detail_item.PasswordDetailItemFragment

class PasswordsAdapter(): RecyclerView.Adapter<PasswordItemViewHolder>() {
    private var data = ArrayList<PasswordData>()
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
        val item = data[position]
        holder.siteName.text = item.siteName
        holder.username.text = item.username

        holder.showButton.setOnClickListener{
            val activity = it.context as AppCompatActivity
            val fragmentManager = activity.supportFragmentManager
            val passDetailFrag = PasswordDetailItemFragment(position)
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_passwords, passDetailFrag, "details")
                .addToBackStack("details")
                .commit()
        }
    }

    override fun getItemCount() = data.size

    public fun setPasswords(passwords: ArrayList<PasswordData>) {
        this.data = passwords
    }
}