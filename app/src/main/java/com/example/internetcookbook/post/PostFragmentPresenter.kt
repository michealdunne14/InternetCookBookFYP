package com.example.internetcookbook.post

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.helper.readImageFromPath
import com.example.internetcookbook.helper.showImagePicker
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.io.ByteArrayOutputStream
import java.io.File

class PostFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null
    val IMAGE_REQUEST = 1
    var listofImages = ArrayList<String>()

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doPostRecipe(
        postModel: PostModel,
        methodStepsArrayList: ArrayList<String>
    ) {
        doAsync {
            val postData = infoStore!!.createPost(postModel)!!
            onComplete {
                doAsync {
                    infoStore!!.uploadImages(postData._id,listofImages)
                    onComplete {
                        view.returnToPager()
                    }
                }
                for(methodSteps in methodStepsArrayList) {
                    doAsync {
                        infoStore!!.putMethod(postData._id, methodSteps)
                    }
                }
            }
        }
    }

    fun doSelectImage() {
        showImagePicker(view,IMAGE_REQUEST)
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