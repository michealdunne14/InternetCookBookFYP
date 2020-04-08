package com.example.internetcookbook.login

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.internetcookbook.activities.MainView
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.UserModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_login.view.progressBar
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
            if (view.mLoginEmail.text.isNotEmpty() && view.mLoginPassword.text.isNotEmpty() && checkEmail(view.mLoginEmail.text.toString())) {
                userModel.email = view.mLoginEmail.text.toString()
                userModel.password = view.mLoginPassword.text.toString()
                presenter.doSignIn(userModel)
            }else{
                detailsIncorrect()
            }
        }

        view.mLoginReturnButton.setOnClickListener {
            view.findNavController().navigateUp()
        }

        startPostponedEnterTransition()

        return view
    }

    fun checkEmail(email: String):Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun getMainPageFromLoginPage(){
        startActivity(Intent(context, MainView::class.java))
        activity!!.finish()
    }

    override fun showProgress(){
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun handleTransition() {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition()
    }

    override fun detailsIncorrect(){
        Snackbar.make(loginView,"Details Incorrect", Snackbar.LENGTH_SHORT).show()
    }

    override fun passwordIncorrect(){
        Snackbar.make(loginView,"Password Incorrect", Snackbar.LENGTH_SHORT).show()

    }


}
