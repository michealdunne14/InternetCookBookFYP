package com.example.archaeologicalfieldwork.adapter

import android.graphics.Bitmap
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.BitmapCardAdapter
import com.example.internetcookbook.adapter.ImageAdapter
import com.example.internetcookbook.helper.readBit64Image
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import kotlinx.android.synthetic.main.card_list.view.*

interface PostListener {
    fun onPostClick(
        hillfort: PostModel
    )
}

class CardAdapter(
    private var posts: ArrayList<DataModel>,
    private val listener: PostListener
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
        holder.bind(postModel,listener)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var showDetails = false
        fun bind(dataModel: DataModel, listener: PostListener) {
            itemView.mCardName.text = dataModel.post.title
            itemView.mCardDescription.text = dataModel.post.description
            itemView.mCardImageList
            val bitmapImages = readBit64Image(dataModel)
            doFindImages(bitmapImages)

            itemView.mShowRecipeDetails.setOnClickListener {
//                if(showDetails) {
//                    cancelDetailsShow(itemView)
//                }else {
//                    showDetailsShow(itemView)
//                }
            }

            val foodModelArrayList = ArrayList<FoodModel>()

            // Inflate the layout for this fragment
            val layoutManager = LinearLayoutManager(itemView.context)


            itemView.mMakeFood.setOnClickListener {
                val action = PagerFragmentViewDirections.actionPagerFragmentToMakeFragment(dataModel)
                itemView.findNavController().navigate(action)
            }

            itemView.mCommentsPage.setOnClickListener {
                val action = PagerFragmentViewDirections.actionPagerFragmentToCommentsFragment(dataModel)
                itemView.findNavController().navigate(action)
            }
            itemView.mPostName.setOnClickListener {
                val action = PagerFragmentViewDirections.actionPagerFragmentToProfileFragment()
                itemView.findNavController().navigate(action)
            }
        }

        fun doFindImages(images: ArrayList<Bitmap>) {
            val viewPager = itemView.findViewById<ViewPager>(R.id.mCardImageList)
            val adapter = BitmapCardAdapter(itemView.context, images)
            viewPager.adapter = adapter
        }

        private fun showDetailsShow(itemView: View) {
            showDetails = true
            val constraintSet = ConstraintSet()
            constraintSet.clone(itemView.context, R.layout.card_list_show_details)

            val transition = ChangeBounds()
            transition.interpolator = AnticipateOvershootInterpolator(1.0f)
            transition.duration = 1000

            TransitionManager.beginDelayedTransition(itemView.homeDetailsConstraint, transition)
            constraintSet.applyTo(itemView.homeDetailsConstraint)
        }

        private fun cancelDetailsShow(itemView: View) {
            showDetails = false
            val constraintSet = ConstraintSet()
            constraintSet.clone(itemView.context, R.layout.card_list)

            val transition = ChangeBounds()
            transition.interpolator = AnticipateOvershootInterpolator(1.0f)
            transition.duration = 1000

            TransitionManager.beginDelayedTransition(itemView.homeDetailsConstraint, transition)
            constraintSet.applyTo(itemView.homeDetailsConstraint)
        }

    }

}