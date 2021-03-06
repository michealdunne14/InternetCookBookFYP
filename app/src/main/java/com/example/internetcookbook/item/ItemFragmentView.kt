package com.example.internetcookbook.item

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.FollowerAdapter
import com.example.internetcookbook.adapter.IngredientsAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.models.FoodModel
import com.example.internetcookbook.models.UserMasterModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_basket.view.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.fragment_followers.view.*
import kotlinx.android.synthetic.main.fragment_item.*

class ItemFragmentView : BaseView() {

    lateinit var presenter: ItemFragmentPresenter
    var foodModelArrayList = ArrayList<FoodModel>()
    var followArrayList = ArrayList<UserMasterModel>()


    lateinit var itemView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_item, container, false)
        presenter = initPresenter(ItemFragmentPresenter(this)) as ItemFragmentPresenter
        itemView = view

//      Show information on pages
        showFollowers(presenter.doFindFollowers())
        showBasket(presenter.doFindBasket())
        showCupboard(presenter.doFindCupboard())

        itemView.mAddItemToBasket.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToIngredientsFragment("basket")
            view.findNavController().navigate(action)
        }

        itemView.mAddItemToCupboard.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToIngredientsFragment("cupboard")
            view.findNavController().navigate(action)
        }


        itemView.mFollowerFind.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToFindFollowingView()
            view.findNavController().navigate(action)
        }

        itemView.mSearchBasketItem.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(characterSearch: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchedBasket = presenter.doSearchBasket(characterSearch)
                itemView.mBasketRecyclerView.adapter = IngredientsAdapter(
                    searchedBasket,
                    presenter.doCurrentUser(),
                    "basket",
                    presenter,
                    activity
                )
                itemView.mBasketRecyclerView.adapter?.notifyDataSetChanged()
            }
        })

        itemView.mSearchCupboardItem.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(characterSearch: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchedCupboard = presenter.doSearchCupboard(characterSearch)
                itemView.mCupboardRecyclerView.adapter = IngredientsAdapter(
                    searchedCupboard,
                    presenter.doCurrentUser(),
                    "cupboard",
                    presenter,
                    activity
                )
                notifyDataSetChanged()
            }

        })


        itemView.mSearchFollowerItem.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(characterSearch: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchedFollowers = presenter.doSearchFollowers(characterSearch)
                itemView.mFollowerRecyclerView.adapter = FollowerAdapter(searchedFollowers)
                itemView.mFollowerRecyclerView.adapter?.notifyDataSetChanged()
            }
        })

        val navView: BottomNavigationView = view.findViewById(R.id.itemBottomNav)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        return view
    }

    //  Navigating to the correct selected Item
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.mNavCupboard -> {
                fragmentCart.visibility = View.VISIBLE
                fragmentBasket.visibility = View.INVISIBLE
                fragmentFollowers.visibility = View.INVISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.mNavBasket -> {
                fragmentBasket.visibility = View.VISIBLE
                fragmentCart.visibility = View.INVISIBLE
                fragmentFollowers.visibility = View.INVISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.mNavFollowing -> {
                fragmentBasket.visibility = View.INVISIBLE
                fragmentCart.visibility = View.INVISIBLE
                fragmentFollowers.visibility = View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun showBasket(listofBasket: ArrayList<FoodMasterModel>){
        val layoutManager = LinearLayoutManager(context)
        itemView.mBasketRecyclerView.layoutManager = layoutManager
        itemView.mBasketRecyclerView.adapter = IngredientsAdapter(
            listofBasket,
            presenter.doCurrentUser(),
            "basket",
            presenter,
            activity
        )
        itemView.mBasketRecyclerView.adapter?.notifyDataSetChanged()
        if (listofBasket.isEmpty()) {
            itemView.mBasketEmpty.visibility = View.VISIBLE
        }
    }

    override fun showCupboard(listofCupboard: ArrayList<FoodMasterModel>){
        val layoutManager = LinearLayoutManager(context)
        itemView.mCupboardRecyclerView.layoutManager = layoutManager
        itemView.mCupboardRecyclerView.adapter = IngredientsAdapter(
            listofCupboard,
            presenter.doCurrentUser(),
            "cupboard",
            presenter,
            activity
        )
        itemView.mCupboardRecyclerView.adapter?.notifyDataSetChanged()
        if (listofCupboard.isEmpty()) {
            itemView.mCupboardEmpty.visibility = View.VISIBLE
        }
    }

    override fun showFollowers(userMasterModel: ArrayList<UserMasterModel>){
        val layoutManager = LinearLayoutManager(context)
        itemView.mFollowerRecyclerView.layoutManager = layoutManager
        itemView.mFollowerRecyclerView.adapter = FollowerAdapter(userMasterModel)
        itemView.mFollowerRecyclerView.adapter?.notifyDataSetChanged()
        if (userMasterModel.isEmpty()) {
            itemView.mFollowersEmpty.visibility = View.VISIBLE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        foodModelArrayList.clear()

    }


    override fun notifyDataSetChanged(){
        itemView.mCupboardRecyclerView.adapter?.notifyDataSetChanged()
        itemView.mBasketRecyclerView.adapter?.notifyDataSetChanged()
        itemView.mFollowerRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            notifyDataSetChanged()
        }
    }

}
