package com.example.archaeologicalfieldwork.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.ImageAdapter
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import kotlinx.android.synthetic.main.card_list.view.*
import kotlinx.android.synthetic.main.fragment_start.*

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
            itemView.mCardName.text = postModel.title
            itemView.mCardDescription.text = postModel.description
            doFindImages(postModel.images)

            itemView.mMakeFood.setOnClickListener {
                val action = PagerFragmentViewDirections.actionPagerFragmentToMakeFragment(postModel)
                itemView.findNavController().navigate(action)
            }
        }

        fun doFindImages(images: ArrayList<String>) {
            val viewPager = itemView.findViewById<ViewPager>(R.id.mCardImageList)
                val adapter = ImageAdapter(itemView.context, images)
                viewPager.adapter = adapter
        }
    }

}