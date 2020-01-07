package com.example.internetcookbook.network

import android.content.Context
import android.widget.Toast
import com.example.internetcookbook.models.UserModel
import com.google.gson.GsonBuilder
import okhttp3.*
import okio.IOException


class InformationStore(val context: Context)  {
    var client = OkHttpClient()
    var user = UserModel()
    lateinit var emailSearchArray: Array<UserModel>

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

                    val emailSearch: Array<UserModel> = gson.fromJson<Array<UserModel>>(body, Array<UserModel>::class.java)
                    emailSearchArray = emailSearch
                }
            }
        })
    }

    fun findEmail(userModel: UserModel): UserModel? {
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

        user = emailSearch[0]
        user.loggedIn = true
        return user
    }

    fun getCurrentUser(): UserModel{
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

}