package com.example.internetcookbook.fragmentview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.fragmentpresenter.FriendFragmentPresenter
import com.example.internetcookbook.models.UserModel
import kotlinx.android.synthetic.main.fragment_friend.view.*
import org.jetbrains.anko.AnkoLogger

class FriendFragmentView : BaseView(),AnkoLogger {
    lateinit var friendView: View
    lateinit var presenter: FriendFragmentPresenter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_friend, container, false)
        friendView = view
        presenter = initPresenter(FriendFragmentPresenter(this)) as FriendFragmentPresenter

        val userModel = UserModel()
        view.mSubmitButton.setOnClickListener {
            userModel.name = view.mName.text.toString()
        }

        return view
    }

}
