package com.example.internetcookbook.network

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.example.internetcookbook.models.UserModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_friend.view.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



class InformationStore(val context: Context)  {
    var client = OkHttpClient()
    lateinit var emailSearchArray: Array<UserModel>

    fun emailSearch(email: String) {
        val request = Request.Builder()
            .url("http://52.51.34.156:3000/user/email/${email}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }
                    val body = response.body!!.string()
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()

                    val emailSearch: Array<UserModel> = gson.fromJson<Array<UserModel>>(body, Array<UserModel>::class.java)
                    emailSearchArray = emailSearch
                }
            }
        })
    }

    fun createUser(){
        val formBody: RequestBody = FormBody.Builder()
            .add("username", "test")
            .add("password", "test")
            .add("name", "test")
            .add("email", "test")
            .add("signindate", "test").build()

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/user/create")
            .post(formBody)
            .build()

        val call = client.newCall(request)
        val response = call.execute()

    }

}