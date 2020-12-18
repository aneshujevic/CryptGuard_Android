package com.example.cryptguard.ui.password_detail_item

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cryptguard.R

class password_detail_item : Fragment() {

    companion object {
        fun newInstance() = password_detail_item()
    }

    private lateinit var viewModel: PasswordDetailItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.password_detail_item_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PasswordDetailItemViewModel::class.java)
        // TODO: Use the ViewModel
    }

}