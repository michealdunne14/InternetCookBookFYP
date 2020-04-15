package com.example.internetcookbook.post

import android.content.Context
import android.content.Intent
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.helper.showImagePicker
import com.example.internetcookbook.models.FoodMasterModel
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.models.UserMasterModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

var listofImages = ArrayList<String>()

class PostFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null
    val IMAGE_REQUEST = 1

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doPostRecipe(
        postModel: PostModel,
        methodStepsArrayList: ArrayList<String>
    ) {
        doAsync {
            infoStore!!.createPost(postModel,listofImages,methodStepsArrayList,view)
        }
    }

    fun doSelectImage() {
        showImagePicker(view,IMAGE_REQUEST)
    }

    fun doCurrentUser(): UserMasterModel {
        return infoStore!!.getCurrentUser()
    }

    fun ingredientsAddToRecipe(): ArrayList<FoodMasterModel> {
        return infoStore!!.ingredientsAddedToRecipe()
    }

    fun listofImages(): ArrayList<String> {
        return listofImages
    }

    //  When a result comes back
    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent?, context: Context) {
        when(requestCode){
            IMAGE_REQUEST -> {
                if (data != null){
                    listofImages.add(data.data.toString())
                    view.addImages(listofImages)
                }
            }
        }
    }
}