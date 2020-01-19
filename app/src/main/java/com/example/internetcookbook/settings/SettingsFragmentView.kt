package com.example.internetcookbook.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragmentView:BaseView() {

    lateinit var presenter: SettingsFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)
        presenter = initPresenter(
            SettingsFragmentPresenter(
                this
            )
        ) as SettingsFragmentPresenter


        view.mLogoutButton.setOnClickListener {
            presenter.doLogout()
            val action = SettingsFragmentViewDirections.actionSettingsFragmentToStartFragment()
            view.findNavController().navigate(action)
        }

        return view
    }


}
