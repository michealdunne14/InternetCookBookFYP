package com.example.internetcookbook.findfollowing

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.register.RegisterFragmentPresenter
import kotlinx.android.synthetic.main.fragment_find_following.view.*

class FindFollowingView : BaseView() {

    lateinit var presenter: FindFollowingPresenter
    lateinit var followingView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_find_following, container, false)
        presenter = initPresenter(FindFollowingPresenter(this)) as FindFollowingPresenter
        followingView = view


        followingView.mSearchUsername.setOnClickListener {
            presenter.doFindUser(followingView.mFindFollowerItem.text.toString())
        }
//        followingView.mFindFollowerItem.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {}
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(characterSearch: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                val searchedLandmarks = presenter.doFindUser(characterSearch as String)
//                homeView.mLandmarkList.adapter = LandmarkAdapter(searchedLandmarks, this@HomeFragment, app.fireStore)
//                homeView.mLandmarkList.adapter?.notifyDataSetChanged()
//            }
//
//        })



        return view
    }
}
