package com.example.internetcookbook.fragmentview

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.internetcookbook.R
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.network.Common
import com.example.internetcookbook.network.InformationStore
import kotlinx.android.synthetic.main.fragment_friend.view.*
import org.jetbrains.anko.doAsync

class FriendFragmentView : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_friend, container, false)
        val common = Common()
        val userModel = UserModel()
        view.mSubmitButton.setOnClickListener {
            userModel.name = view.mName.text.toString()
            doAsync {
                InformationStore.PostData(userModel).execute(common.getAddressPostAPI())
            }
        }

        return view
    }

}
