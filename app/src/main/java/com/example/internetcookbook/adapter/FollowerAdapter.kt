package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.helper.readBit64ImageSingle
import com.example.internetcookbook.models.UserMasterModel
import kotlinx.android.synthetic.main.following_list.view.*

class FollowerAdapter(private var user: ArrayList<UserMasterModel>) : RecyclerView.Adapter<FollowerAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.following_list,
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int = user.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val postModel = user[holder.adapterPosition]
        holder.bind(postModel)
    }


    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(userMasterModel: UserMasterModel) {
            itemView.mFollowingName.text = userMasterModel.user.name
//            val bitmapImage = readBit64ImageSingle(userMasterModel.image)
//            itemView.mFollowingPicture.setImageBitmap(bitmapImage)
        }
    }


}