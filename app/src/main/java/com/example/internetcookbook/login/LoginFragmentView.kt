package com.example.internetcookbook.login

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.transition.ChangeBounds
import com.example.internetcookbook.R
import com.example.internetcookbook.adapter.SampleFragmentPagerAdapter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.fragmentview.HomeFragmentView
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.pager.PagerFragmentView
import com.example.internetcookbook.register.RegisterFragmentPresenter
import com.example.internetcookbook.register.RegisterFragmentViewDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_pager.*
import java.io.Serializable

class LoginFragmentView : BaseView() {


    interface ViewCreatedListener : Serializable {
        fun invoke()
    }

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
        handleTransition()
        loginView = view
        view.mLoginSignInButton.setOnClickListener {
            if (view.mLoginEmail.text.isNotEmpty() && view.mLoginPassword.text.isNotEmpty()) {
                userModel.email = view.mLoginEmail.text.toString()
                userModel.password = view.mLoginPassword.text.toString()
                presenter.doSignIn(userModel)
            }else{
                detailsIncorrect()
            }
        }

        view.mRegisterReturnButton.setOnClickListener {
            val action = LoginFragmentViewDirections.actionLoginFragmentViewToStartFragment()
            view.findNavController().navigate(action)
        }

        startPostponedEnterTransition()

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

    private fun handleTransition() {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition()
    }

    override fun detailsIncorrect(){
        Snackbar.make(loginView,"Fill in all fields", Snackbar.LENGTH_SHORT).show()
    }

    override fun passwordIncorrect(){
        Snackbar.make(loginView,"Password Incorrect", Snackbar.LENGTH_SHORT).show()

    }


}
