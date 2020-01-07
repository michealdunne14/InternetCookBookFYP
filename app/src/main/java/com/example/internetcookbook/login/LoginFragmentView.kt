package com.example.internetcookbook.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.register.RegisterFragmentPresenter
import com.example.internetcookbook.register.RegisterFragmentViewDirections
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragmentView : BaseView() {

    lateinit var presenter: LoginFragmentPresenter
    var userModel = UserModel()
    lateinit var loginView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        presenter = initPresenter(LoginFragmentPresenter(this)) as LoginFragmentPresenter

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)
        loginView = view
        view.mLoginSignInButton.setOnClickListener {
            if (view.mLoginEmail.text.isNotEmpty() && view.mLoginPassword.text.isNotEmpty()) {
                userModel.email = view.mLoginEmail.text.toString()
                userModel.password = view.mLoginPassword.text.toString()
                presenter.doSignIn(userModel)
            }else{
                Toast.makeText(view.context,"Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        view.mRegisterReturnButton.setOnClickListener {
            val action = LoginFragmentViewDirections.actionLoginFragmentViewToStartFragment()
            view.findNavController().navigate(action)
        }

        return view
    }

    override fun getMainPageFromLoginPage(){
        val action = LoginFragmentViewDirections.actionLoginFragmentViewToPagerFragment()
        loginView.findNavController().navigate(action)
    }

    override fun showProgress(){

    }

    override fun hideProgress() {

    }

    override fun detailsIncorrect(){
        Toast.makeText(loginView.context,"Fill in all fields", Toast.LENGTH_SHORT).show()
    }


}
