package com.example.internetcookbook.register

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController

import com.example.internetcookbook.R
import com.example.internetcookbook.fragmentview.StartFragmentDirections
import kotlinx.android.synthetic.main.fragment_register.view.*
import kotlinx.android.synthetic.main.fragment_start.view.*

class RegisterFragmentView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_register, container, false)

        view.mRegisterButton.setOnClickListener {
            val action = RegisterFragmentViewDirections.actionRegisterFragmentToPagerFragment()
            view.findNavController().navigate(action)
        }
        return view
    }

}
