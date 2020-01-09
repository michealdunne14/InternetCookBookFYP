package com.example.internetcookbook.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController

import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.UserModel
import kotlinx.android.synthetic.main.fragment_register.view.*
import kotlinx.android.synthetic.main.fragment_register.view.mRegisterReturnButton
import java.util.*

class RegisterFragmentView : BaseView() {

    lateinit var presenter: RegisterFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        presenter = initPresenter(RegisterFragmentPresenter(this)) as RegisterFragmentPresenter
        val view=  inflater.inflate(R.layout.fragment_register, container, false)

        view.mRegisterSignupButton.setOnClickListener {
            if(view.mRegisterFirstname.text.isNotEmpty() &&
                view.mFriendEmail.text.isNotEmpty() &&
                view.mPasswordRegister.text.isNotEmpty() &&
                view.mUsernameRegister.text.isNotEmpty()) {
                val currentTime: Date = Calendar.getInstance().getTime()
                val createUser = presenter.doRegister(
                    UserModel(
                        "",
                        view.mRegisterFirstname.text.toString(),
                        view.mFriendEmail.text.toString(),
                        view.mPasswordRegister.text.toString(),
                        view.mUsernameRegister.text.toString(),
                        currentTime.toString(),
                        true
                    )
                )
                if (createUser != null) {
                    val action = RegisterFragmentViewDirections.actionRegisterFragmentToPagerFragment()
                    view.findNavController().navigate(action)
                }else{
                    Toast.makeText(view.context,"Email is already registered",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(view.context,"Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        view.mRegisterReturnButton.setOnClickListener {
            val action = RegisterFragmentViewDirections.actionRegisterFragmentToStartFragment()
            view.findNavController().navigate(action)
        }
        return view
    }

}
