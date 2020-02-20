package com.example.internetcookbook.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archaeologicalfieldwork.adapter.CardAdapter
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.helper.readBit64ImageSingle
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.register.RegisterFragmentPresenter
import com.example.internetcookbook.settings.SettingsFragmentViewDirections
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragmentView : BaseView() {

    lateinit var presenter: ProfileFragmentPresenter

    lateinit var profileView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        presenter = initPresenter(ProfileFragmentPresenter(this)) as ProfileFragmentPresenter
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)
        profileView = view
        val user= presenter.doGetUser()
        view.mProfileName.text = user.user.name
        view.mProfileEmail.text = user.user.email
        view.mProfileUsername.text = user.user.username
        val bitmapImage = readBit64ImageSingle(user.image)
        view.mProfilePicture.setImageBitmap(bitmapImage)

        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)
        view.mProfileRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?

        view.mProfileReturnButton.setOnClickListener {
            val action = ProfileFragmentViewDirections.actionProfileFragmentToSettingsFragment()
            view.findNavController().navigate(action)
        }

        profileView.mProfileRecyclerView.adapter = CardAdapter(presenter.doGetPosts(), presenter)
        profileView.mProfileRecyclerView.adapter?.notifyDataSetChanged()
        return view
    }


}
