package com.example.internetcookbook.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.internetcookbook.helper.exists
import com.example.internetcookbook.helper.read
import com.example.internetcookbook.helper.write
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.models.UserModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okio.IOException
import kotlin.collections.ArrayList


class InformationStore(val context: Context, val internetConnection: Boolean) {
    var client = OkHttpClient()
    var user = UserModel()
    var imageArrayList = ArrayList<Bitmap>()
    var postData = ArrayList<DataModel>()
    var userLocalStore = mutableListOf<UserModel>()
    lateinit var emailSearchArray: Array<UserModel>


    val JSON_FILE = "InformationStore.json"
    val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
    val listType = object : TypeToken<ArrayList<UserModel>>() {}.type

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(userLocalStore, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        userLocalStore = Gson().fromJson(jsonString, listType)
    }

    fun emailSearch(userModel: UserModel) {
        val request = Request.Builder()
            .url("http://52.51.34.156:3000/user/email/${userModel.email}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val body = response.body!!.string()
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()

                    val emailSearch: Array<UserModel> =
                        gson.fromJson<Array<UserModel>>(body, Array<UserModel>::class.java)
                    emailSearchArray = emailSearch
                }
            }
        })
    }

    fun findEmail(userModel: UserModel): UserModel? {
        if (internetConnection) {
            lateinit var emailSearch: Array<UserModel>
            val request = Request.Builder()
                .url("http://52.51.34.156:3000/user/email/${userModel.email}")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()

                emailSearch = gson.fromJson<Array<UserModel>>(body, Array<UserModel>::class.java)
            }
            if (!emailSearch.isEmpty()) {
                user = emailSearch[0]
                user.loggedIn = true
                userLocalStore.add(user)
                serialize()
                return user
            } else {
                return null
            }
        } else {
            return null
        }
    }

    fun getCurrentUser(): UserModel {
        if (userLocalStore.isNotEmpty()) {
            user = userLocalStore[0]
        }
        return user
    }

    fun createUser(userModel: UserModel): String? {
        user = userModel
        val user = findEmail(userModel)
        if (user == null) {
            val formBody: RequestBody = FormBody.Builder()
                .add("username", userModel.username)
                .add("password", userModel.password)
                .add("name", userModel.name)
                .add("email", userModel.email)
                .add("signupdate", userModel.signupdate).build()

            val request: Request = Request.Builder()
                .url("http://52.51.34.156:3000/user/create")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response -> return response.body!!.toString() }
        }else{
            return null
        }
    }

    fun userCreated() {
        userLocalStore.add(user)
        serialize()
    }

    fun getHomeData(): ArrayList<DataModel> {
        return postData
    }

    fun getPostData(){
        postData.clear()
        lateinit var dataArray: DataModel
        if (internetConnection) {
            for (post in user.posts) {
                val request = Request.Builder()
                    .url("http://52.51.34.156:3000/post/id/${post?.postoid}")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    val body = response.body!!.string()
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    dataArray = gson.fromJson(body, DataModel::class.java)
                    postData.add(dataArray)
                }
            }
        }
    }

    fun uploadImages(oid: String, listofImages: ArrayList<String>) {
        //        val file = File(listofImages[0])
//        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart("imageFiles", listofImages[0], RequestBody.create(MEDIA_TYPE_PNG, file))
//            .build()
//
//        val request = Request.Builder()
//            .url(" http://52.51.34.156:3000/post/upload/${oid}")
//            .post(requestBody)
//            .build()
//
//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//            println(response.body!!.string())
//        }

    }


    fun createPost(postModel: PostModel): String? {
        val formBody: RequestBody = FormBody.Builder()
            .add("title", postModel.title)
            .add("description", postModel.description)
            .add("useroid", user.oid).build()

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/post/create")
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return response.body!!.string()
        }
    }


    fun putPostToUser(postModel: PostModel): String? {
        val formBody: RequestBody = FormBody.Builder()
            .add("title", postModel.title)
            .add("description", postModel.description)
            .add("image", "randomimage")
            .add("useroid", user.oid).build()

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/post/create")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response -> return response.body!!.toString() }
    }

    fun logoutUser() {
        userLocalStore.clear()
        serialize()
    }
}