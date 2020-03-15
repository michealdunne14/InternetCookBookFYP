package com.example.internetcookbook.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.MakeAdapter
import com.example.internetcookbook.base.BaseView
import kotlinx.android.synthetic.main.fragment_comments.view.*

class CommentsFragmentView : BaseView() {

    lateinit var commentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_comments, container, false)
        commentView = view
        val comments = CommentsFragmentViewArgs.fromBundle(arguments!!).dataModel.post.comments

        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)
        commentView.mCommentRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?
        val commentsArrayList = ArrayList<String>()
        for (comment in comments){
            commentsArrayList.add(comment!!.commentString)
        }
        commentView.mCommentRecyclerView.adapter = MakeAdapter(commentsArrayList)
        commentView.mCommentRecyclerView.adapter?.notifyDataSetChanged()
        return view
    }
}
