package com.example.archaeologicalfieldwork.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.ImageAdapter
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.models.UserModel
import kotlinx.android.synthetic.main.card_list.view.*

interface PostListener {
    fun onPostClick(
        hillfort: PostModel
    )
}

class CardAdapter(
    private var posts: ArrayList<PostModel>,
    private val listener: PostListener,
    private val user: UserModel
) : RecyclerView.Adapter<CardAdapter.MainHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_list,
                parent,
                false
            )
        )
    }

    //  Item Count
    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val postModel = posts[holder.adapterPosition]
        holder.bind(postModel,listener,user)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(postModel: PostModel, listener: PostListener, user: UserModel) {
            itemView.mCardName.text = postModel.name
            itemView.mCardDescription.text = postModel.description
            doFindImages(postModel.images)
        }

        fun doFindImages(images: String) {
            val viewPager = itemView.findViewById<ViewPager>(R.id.mCardImageList)
                val adapter = ImageAdapter(itemView.context, images)
                viewPager.adapter = adapter
        }
    }

}