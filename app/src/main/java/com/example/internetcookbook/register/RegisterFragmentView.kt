package com.example.internetcookbook.register

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.internetcookbook.activities.MainView

import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.UserModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_register.view.*
import kotlinx.android.synthetic.main.fragment_register.view.mLoginReturnButton
import java.util.*

class RegisterFragmentView : BaseView() {

    lateinit var presenter: RegisterFragmentPresenter

    lateinit var registerView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        presenter = initPresenter(RegisterFragmentPresenter(this)) as RegisterFragmentPresenter
        val view=  inflater.inflate(R.layout.fragment_register, container, false)
        registerView = view
        view.mRegisterProfilePic.setOnClickListener {
            presenter.doSelectImage()
        }

        view.mRegisterSignupButton.setOnClickListener {
            if(view.mRegisterFirstname.text.isNotEmpty() &&
                view.mFriendEmail.text.isNotEmpty() &&
                view.mPasswordRegister.text.isNotEmpty() &&
                view.mUsernameRegister.text.isNotEmpty()) {
                val currentTime: Date = Calendar.getInstance().time

                presenter.doRegister(
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
            }else{
                Snackbar.make(view,"Fill in all fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        view.mLoginReturnButton.setOnClickListener {
            view.findNavController().navigateUp()
        }
        return view
    }

    override fun setRegisterResponse(usercreated: Boolean) {
        if (usercreated) {
            startActivity(Intent(context, MainView::class.java))
            activity!!.finish()
        }else{
            Snackbar.make(registerView,"Details Incorrect", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun setProfileImage(image: Bitmap?){
        registerView.mRegisterProfilePic.setImageBitmap(image)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            presenter.doActivityResult(requestCode,resultCode,data,registerView.context)
        }
    }
}
