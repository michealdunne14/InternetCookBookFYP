package com.example.internetcookbook

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import com.example.internetcookbook.register.RegisterFragmentPresenter
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragmentView:BaseView() {

    lateinit var presenter: SettingsFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)
        presenter = initPresenter(SettingsFragmentPresenter(this)) as SettingsFragmentPresenter


        view.mLogoutButton.setOnClickListener {
            presenter.doLogout()
            val action = SettingsFragmentViewDirections.actionSettingsFragmentToStartFragment()
            view.findNavController().navigate(action)
        }

        return view
    }


}
