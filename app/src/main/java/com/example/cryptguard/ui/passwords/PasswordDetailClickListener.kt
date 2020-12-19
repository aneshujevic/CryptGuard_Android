package com.example.cryptguard.ui.passwords

import android.view.ViewGroup
import android.widget.Toast

class PasswordDetailClickListener {
    fun onPasswordItemClick(parentView: ViewGroup?, id: Int) {
        // TODO: Implement replacing parent fragment to password detail fragment
        Toast.makeText(parentView?.context, id.toString(), Toast.LENGTH_SHORT).show()
    }
}