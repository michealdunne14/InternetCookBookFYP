package com.example.internetcookbook.findfollowing

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.UserFollowAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.UserMasterModel
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

        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)

        view.mFindFollowerRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?

        view.mReturnButton.setOnClickListener {
            followingView.findNavController().navigateUp()
        }

//      Search for user when text entered
        followingView.mFindFollowerItem.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(characterSearch: CharSequence, p1: Int, p2: Int, p3: Int) {
                if (characterSearch.length == 3) {
                    presenter.doFindFollowing(characterSearch)
                }else if (characterSearch.length > 3){
                    presenter.searchUsers(characterSearch)
                }
            }

        })

        return view
    }

    override fun showFollowList(searchFollowing: ArrayList<UserMasterModel>) {
        followingView.mFindFollowerRecyclerView.adapter =
            UserFollowAdapter(
                searchFollowing,
                presenter
            )
        followingView.mFindFollowerRecyclerView.adapter?.notifyDataSetChanged()
    }
}
