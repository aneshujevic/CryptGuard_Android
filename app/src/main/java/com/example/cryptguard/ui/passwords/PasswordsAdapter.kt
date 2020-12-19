package com.example.cryptguard.ui.passwords

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptguard.R
import com.example.cryptguard.ui.password_detail_item.PasswordDetailItemFragment

class PasswordsAdapter(): RecyclerView.Adapter<PasswordItemViewHolder>() {
    private var data = listOf<String>(
        "Hello",
        "is it me you're looking for",
        "I can see it in your eyes",
        "Hello, world",
        "Hello",
        "Is anyone out there?",
        "Hello",
        "is it me you're looking for",
        "I can see it in your eyes",
        "Hello, world",
        "Hello",
        "Is anyone out there?"
    )
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
        holder.siteName.text = item
        holder.username.text = item
        holder.showButton.setOnClickListener{
            val activity = it.context as AppCompatActivity
            val fragmentManager = activity.supportFragmentManager
            val passDetailFrag = PasswordDetailItemFragment()
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_passwords, passDetailFrag, "details")
                .addToBackStack("details")
                .commit()
        }
    }

    override fun getItemCount() = data.size

}