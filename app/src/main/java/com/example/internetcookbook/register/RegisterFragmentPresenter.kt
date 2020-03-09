package com.example.internetcookbook.register

import android.content.Context
import android.content.Intent
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.helper.readImageFromPath
import com.example.internetcookbook.helper.showImagePicker
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class RegisterFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null
    val IMAGE_REQUEST = 1
    var profilePicture = String()
    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doRegister(userModel: UserModel) {
        var usercreated: Boolean
        doAsync {
            usercreated = infoStore!!.createUser(userModel,profilePicture)
            onComplete {
                if (usercreated){
                    infoStore!!.userCreated()
                }
                view.setRegisterResponse(usercreated)
            }
        }
    }

    fun doSelectImage() {
        showImagePicker(view,IMAGE_REQUEST)
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent?, context: Context) {
        when(requestCode){
            IMAGE_REQUEST -> {
                if (data != null){
                    val bitmap = readImageFromPath(context,data.data.toString())
                    profilePicture = data.data.toString()
                    view.setProfileImage(bitmap)
                }
            }
        }
    }
}