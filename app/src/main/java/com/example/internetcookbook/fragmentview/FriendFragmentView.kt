package com.example.internetcookbook.fragmentview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.internetcookbook.R
import com.example.internetcookbook.models.UserModel
import com.example.internetcookbook.network.InformationStore
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_friend.view.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.IOException

class FriendFragmentView : Fragment() {
    var client = OkHttpClient()
    lateinit var friendView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_friend, container, false)
        friendView = view
        val userModel = UserModel()
        view.mSubmitButton.setOnClickListener {
            userModel.name = view.mName.text.toString()
            val url = "http://52.51.34.156:3000/user/password/testpass"
            val informationStore = InformationStore()
//            informationStore.GET(url, object: Callback {
//
//                override fun onFailure(call: Call, e: java.io.IOException) {
//                    println("Request Failure.")
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    val responseData = response.body?.string()
//                    kotlin.run{
//                        try {
//                            val json = JSONObject(responseData)
//                            println("Request Successful!!")
//                            println(json)
//                            val responseObject = json.getJSONObject("response")
//                            val docs = json.getJSONArray("docs")
////                            this@MainActivity.fetchComplete()
//                        } catch (e: JSONException) {
//                            e.printStackTrace()
//                        }
//                    }                }
//            })
            run()
        }

        return view
    }

//   Needs to be moved in to its own class after demo like fireStore
    fun run() {
        val request = Request.Builder()
            .url("http://52.51.34.156:3000/user/password/${view!!.mName.text}")
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

                    val testCase: Array<UserModel> = gson.fromJson<Array<UserModel>>(body, Array<UserModel>::class.java)
                    for (i in testCase){
                        friendView.mResult.text = i.name
                    }
                }
            }
        })
    }

}
