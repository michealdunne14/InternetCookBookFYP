package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.findfollowing.FindFollowingPresenter
import com.example.internetcookbook.helper.readBit64ImageSingle
import com.example.internetcookbook.models.UserMasterModel
import kotlinx.android.synthetic.main.following_list.view.mFollowingName
import kotlinx.android.synthetic.main.following_list.view.mFollowingPicture
import kotlinx.android.synthetic.main.user_follow.view.*

class UserFollowAdapter(
    private var user: ArrayList<UserMasterModel>,
    private var presenter: FindFollowingPresenter
) : RecyclerView.Adapter<UserFollowAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.user_follow,
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int = user.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val postModel = user[holder.adapterPosition]
        holder.bind(postModel,presenter)
    }


    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(
            userMasterModel: UserMasterModel,
            presenter: FindFollowingPresenter
        ) {
            itemView.mFollowingName.text = userMasterModel.user.name
            val bitmapImage = readBit64ImageSingle(userMasterModel.image)
            itemView.mFollowingPicture.setImageBitmap(bitmapImage)
            val followingArrayList = presenter.followingUsers()

            for (following in followingArrayList){
                if (following.user.username == userMasterModel.user.username){
                    followingUser()
                }
            }
            itemView.mFollowButton.setOnClickListener {
                if (itemView.mFollowButton.text == itemView.context.getString(R.string.following)){
                    itemView.mFollowButton.text = itemView.context.getString(R.string.follow)
                    itemView.mFollowButton.background = itemView.resources.getDrawable(
                        R.drawable.greybutton
                    )
                    itemView.mFollowButton.setTextColor(itemView.resources.getColor(
                        R.color.colorBlack
                    ))
                    presenter.unfollowUser(userMasterModel)
                }else {
                    presenter.followUser(userMasterModel)
                    followingUser()
                }
            }
        }

        fun followingUser(){
            itemView.mFollowButton.text = itemView.context.getString(R.string.following)
            itemView.mFollowButton.background = itemView.resources.getDrawable(R.drawable.bluebutton)
            itemView.mFollowButton.setTextColor(itemView.resources.getColor(R.color.colorWhite))
        }
    }
}