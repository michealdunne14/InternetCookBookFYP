package com.example.internetcookbook.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.register.RegisterFragmentPresenter
import com.example.internetcookbook.register.RegisterFragmentViewDirections
import kotlinx.android.synthetic.main.fragment_post.view.*

class PostFragmentView : BaseView() {

    lateinit var presenter: PostFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        presenter = initPresenter(PostFragmentPresenter(this)) as PostFragmentPresenter
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        view.mPostButton.setOnClickListener {
            presenter.doPostRecipe(PostModel(view.mPostTitle.text.toString(),view.mPostDescription.text.toString(),""))
            val action = PostFragmentViewDirections.actionPostFragment2ToPagerFragment()
            view.findNavController().navigate(action)
        }
        return view
    }
}
