package com.example.internetcookbook.post

import android.content.Context
import android.content.Intent
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.helper.showImagePicker
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.network.InformationStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class PostFragmentPresenter(view: BaseView): BasePresenter(view), AnkoLogger {
    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null
    val IMAGE_REQUEST = 1
    var listofImages = ArrayList<String>()
    var oid = ""

    init {
        infoStore = app.informationStore as InformationStore
    }

    fun doPostRecipe(
        postModel: PostModel,
        methodStepsArrayList: ArrayList<String>
    ) {
        doAsync {
            infoStore!!.createPost(postModel)!!
            onComplete {
                doAsync {
                    infoStore!!.uploadImages(oid,listofImages)
                }
                for(methodSteps in methodStepsArrayList) {
                    doAsync {
                        infoStore!!.putMethod(oid, methodSteps)
                    }
                }
            }
        }
    }

    fun doPutMethod(methodSteps: String, _id: String) {
        doAsync {
            infoStore!!.putMethod(_id,methodSteps)
        }
    }


    fun doSelectImage() {
        showImagePicker(view,IMAGE_REQUEST)
//        var images = ""
//        doAsync {
//            infoStore!!.getPostData()
//            onComplete {
////                listofImages.add(images)
////                view.addImages(listofImages)
//            }
//        }
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