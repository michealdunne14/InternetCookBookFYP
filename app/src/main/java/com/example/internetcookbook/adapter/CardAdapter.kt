package com.example.archaeologicalfieldwork.adapter

import android.graphics.Bitmap
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.BitmapCardAdapter
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.animations.Bounce
import com.example.internetcookbook.fragmentpresenter.HomeFragPresenter
import com.example.internetcookbook.helper.readBit64ImageArrayList
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.models.IngredientModel
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.card_list.view.*

interface PostListener {
    fun onPostClick(
        hillfort: PostModel
    )
}

private var heart = false

class CardAdapter(
    private var posts: ArrayList<DataModel?>,
    private val presenter: HomeFragPresenter?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_list, parent, false))
//        else {
//            LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.loading, parent, false))
//        }
    }

    //  Item Count
    override fun getItemCount(): Int {
        return posts.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (posts[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MainHolder) {
            val postModel = posts[holder.adapterPosition]
            holder.bind(postModel!!,presenter)
        }
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var showDetails = false
        fun bind(
            dataModel: DataModel,
            presenter: HomeFragPresenter?
        ) {
            val bitmapImages = readBit64ImageArrayList(dataModel)
            doFindImages(bitmapImages)

            itemView.mCardName.text = dataModel.post.title
            itemView.mCardDescription.text = dataModel.post.description
            itemView.mCardDifficulty.text = dataModel.post.difficulty

            if (presenter != null) {
                for (hearts in dataModel.post.userhearts) {
                    if (presenter.doFindCurrentUser().user.oid == hearts.userId) {
                        itemView.mHeartButton.setImageResource(R.drawable.baseline_favorite_black_36)
                        heart = true
                    }
                }

                itemView.mHeartButton.setOnClickListener {
                    heart = !heart
                    if (heart) {
                        val myAnim = AnimationUtils.loadAnimation(itemView.context, R.anim.bounce)
                        val interpolator =
                            Bounce(0.2, 20.0)
                        myAnim.interpolator = interpolator
                        itemView.mHeartButton.startAnimation(myAnim)
                        itemView.mHeartButton.setImageResource(R.drawable.baseline_favorite_black_36)
                        presenter.doHeartData(dataModel.post._id)
                    } else {
                        val myAnim = AnimationUtils.loadAnimation(itemView.context, R.anim.bounce)
                        val interpolator =
                            Bounce(0.2, 20.0)
                        myAnim.interpolator = interpolator
                        itemView.mHeartButton.startAnimation(myAnim)
                        itemView.mHeartButton.setImageResource(R.drawable.baseline_favorite_border_black_36)
                        presenter.doRemoveHeart(dataModel.post._id)
                    }
                }

                itemView.mSendComment.setOnClickListener {
                    val comment = itemView.mCardComment.text.toString().trim()
                    presenter.doSendComment(comment,dataModel)
                    itemView.mCardComment.setText("")
                }

            }else{
                itemView.mSendComment.visibility == View.INVISIBLE
                itemView.mHeartButton.visibility == View.INVISIBLE
            }

            itemView.mAddBasketButton.setOnClickListener {
                presenter!!.doAddBasket(dataModel)
            }

            itemView.mMakeFood.setOnClickListener {
                val action = PagerFragmentViewDirections.actionPagerFragmentToMakeFragment(dataModel)
                itemView.findNavController().navigate(action)
            }

            itemView.mCommentsPage.setOnClickListener {
                val action = PagerFragmentViewDirections.actionPagerFragmentToCommentsFragment(dataModel)
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