package com.example.internetcookbook.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.DataModel
import java.io.IOException


fun showImagePicker(view: BaseView, id: Int) {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_OPEN_DOCUMENT
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    val chooser = Intent.createChooser(intent, R.string.select_image.toString())
    view.activity?.startActivityForResult(chooser, id)
}

//Reads image
fun readImage(activity: Activity, resultCode: Int, data: Intent?): Bitmap? {
    var bitmap: Bitmap? = null
    if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, data.data)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return bitmap
}

fun readBit64ImageArrayList(dataModel: DataModel): ArrayList<Bitmap> {
    val imageArrayList = ArrayList<Bitmap>()
    for (image in dataModel.data) {
        val decodedString: ByteArray = Base64.decode(image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        imageArrayList.add(decodedByte)
    }
    return imageArrayList
}

fun readBit64ImageSingle(image: String): Bitmap? {
    try {
        val decodedString: ByteArray = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }catch (e: Exception){
        e.printStackTrace()
    }
    return null
}

//Reads Image from path
fun readImageFromPath(context: Context, path : String) : Bitmap? {
    var bitmap : Bitmap? = null
    val uri = Uri.parse(path)
    if (uri != null) {
        try {
            val parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.getFileDescriptor()
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
        } catch (e: Exception) {
        }
    }
    return bitmap
}