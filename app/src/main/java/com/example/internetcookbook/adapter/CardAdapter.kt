package com.example.archaeologicalfieldwork.adapter

import android.graphics.Bitmap
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateOvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.Bounce
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.BitmapCardAdapter
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.fragmentpresenter.HomeFragPresenter
import com.example.internetcookbook.helper.readBit64ImageArrayList
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.camera_show.view.*
import kotlinx.android.synthetic.main.card_list.view.*

interface PostListener {
    fun onPostClick(
        hillfort: PostModel
    )
}

private var heart = false

class CardAdapter(
    private var posts: ArrayList<DataModel>,
    private val presenter: BasePresenter
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
        holder.bind(postModel,presenter)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var showDetails = false
        fun bind(
            dataModel: DataModel,
            presenter: BasePresenter
        ) {

            val bitmapImages = readBit64ImageArrayList(dataModel)
            doFindImages(bitmapImages)

            itemView.mCardName.text = dataModel.post.title
            itemView.mCardDescription.text = dataModel.post.description

            itemView.mHeartButton.setOnClickListener {
                heart = !heart
                if (heart) {
                    val myAnim = AnimationUtils.loadAnimation(itemView.context, R.anim.bounce)
                    val interpolator = Bounce(0.2, 20.0)
                    myAnim.interpolator = interpolator
                    itemView.mHeartButton.startAnimation(myAnim)
                    itemView.mHeartButton.setImageResource(R.drawable.baseline_favorite_black_36)
                    presenter.doHeartData(dataModel.post._id)
                }else{
                    val myAnim = AnimationUtils.loadAnimation(itemView.context, R.anim.bounce)
                    val interpolator = Bounce(0.2, 20.0)
                    myAnim.interpolator = interpolator
                    itemView.mHeartButton.startAnimation(myAnim)
                    itemView.mHeartButton.setImageResource(R.drawable.baseline_favorite_border_black_36)

                }
            }

            itemView.mShowRecipeDetails.setOnClickListener {
                if(showDetails) {
                    cancelDetailsShow(itemView)
                }else {
                    showDetailsShow(itemView)
                }
            }


            itemView.mSaveRecipe.setOnClickListener {
                Snackbar.make(itemView,"Saved on Profile Page", Snackbar.LENGTH_SHORT).show()
            }

            // Inflate the layout for this fragment
            val layoutManager = LinearLayoutManager(itemView.context)

            itemView.mCardIngredients.layoutManager = layoutManager as RecyclerView.LayoutManager?

            itemView.mMakeFood.setOnClickListener {
                val action = PagerFragmentViewDirections.actionPagerFragmentToMakeFragment(dataModel)
                itemView.findNavController().navigate(action)
            }

            itemView.mCommentsPage.setOnClickListener {
                val action = PagerFragmentViewDirections.actionPagerFragmentToCommentsFragment(dataModel)
                itemView.findNavController().navigate(action)
            }
            itemView.mPostName.setOnClickListener {
//                val action = PagerFragmentViewDirections.actionPagerFragmentToProfileFragment()
//                itemView.findNavController().navigate(action)
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