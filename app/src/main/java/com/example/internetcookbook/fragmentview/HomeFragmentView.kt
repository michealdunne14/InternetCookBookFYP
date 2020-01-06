package com.example.internetcookbook.fragmentview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archaeologicalfieldwork.adapter.CardAdapter
import com.example.archaeologicalfieldwork.adapter.PostListener
import com.example.internetcookbook.pager.PagerFragmentView
import com.example.internetcookbook.R
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragmentView : Fragment(), PostListener {

    companion object {

        private const val CALLBACK_FUNC = "callback"

        fun newInstance(
            callback: PagerFragmentView.ViewCreatedListener
        ): HomeFragmentView {
            return HomeFragmentView().apply {
                arguments = bundleOf(
                    CALLBACK_FUNC to callback
                )
            }
        }
    }

    private lateinit var callback: PagerFragmentView.ViewCreatedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getSerializable(CALLBACK_FUNC)?.let {
            callback = it as PagerFragmentView.ViewCreatedListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_home, container, false)
        val postModelList = ArrayList<PostModel>()

        // Inflate the layout for this fragment
        val layoutManager = LinearLayoutManager(context)

        callback.invoke()

        view.mListRecyclerView.layoutManager = layoutManager as RecyclerView.LayoutManager?

        view.floatingActionButton.setOnClickListener {
            val action = PagerFragmentViewDirections.actionPagerFragmentToPostFragment2()
            view.findNavController().navigate(action)
        }


        val user = UserModel()
//        postModelList.add(PostModel("Burger","Burger","https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/RedDot_Burger.jpg/1200px-RedDot_Burger.jpg"))
//        postModelList.add(PostModel("Food","food","https://eatforum.org/content/uploads/2018/05/table_with_food_top_view_900x700.jpg"))
        view.mListRecyclerView.adapter = CardAdapter(postModelList, this, user)
        view.mListRecyclerView.adapter?.notifyDataSetChanged()
//        showPosts()C
        return view
    }


//    //  Show Hillforts
//    fun showPosts(/*postModelList: ArrayList<PostModel>, user: UserModel*/) {
//        val postModelList = ArrayList<PostModel>()
//        val user = UserModel()
//        postModel.name = "Test"
//        postModelList.add(postModel)
//        mListRecyclerView.adapter = CardAdapter(postModelList, this, user)
//        mListRecyclerView.adapter?.notifyDataSetChanged()
//    }

    override fun onPostClick(hillfort: PostModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
