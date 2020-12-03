package com.example.cryptguard.ui.passwords

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptguard.R
import kotlinx.android.synthetic.main.password_item.view.*

class PasswordItemViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView) {
    var siteName: TextView = itemView.findViewById(R.id.siteName)
    var username: TextView = itemView.findViewById(R.id.username)
}