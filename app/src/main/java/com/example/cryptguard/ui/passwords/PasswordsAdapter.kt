package com.example.cryptguard.ui.passwords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptguard.R
import kotlinx.android.synthetic.main.password_item.view.*

class PasswordsAdapter: RecyclerView.Adapter<PasswordItemViewHolder>() {
    private var data = listOf<String>("Hello", "is it me you're looking for", "I can see it in your eyes")
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
                .inflate(R.layout.password_item, parent, false)
        return PasswordItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasswordItemViewHolder, position: Int) {
        val item = data[position]
        holder.siteName.text = item
        holder.username.text = item
    }

    override fun getItemCount(): Int {
        return data.size
    }

}