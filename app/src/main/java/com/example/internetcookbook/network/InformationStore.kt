package com.example.internetcookbook.network

import android.os.AsyncTask
import com.example.internetcookbook.models.UserModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

interface OnInformationListener {
    fun onUserComplete(user: ArrayList<ArrayList<UserModel>>)
}

var arrayList: ArrayList<ArrayList<UserModel>> = ArrayList()

class InformationStore : OnInformationListener  {

    val common = Common()

//    GetData().execute(common.getAddressApiName())


    fun getUserData(): ArrayList<ArrayList<UserModel>> {
        return arrayList
    }

    override fun onUserComplete(user: ArrayList<ArrayList<UserModel>>) {
        arrayList = user
    }


    class GetData : AsyncTask<String?, Void?, String?>() {
        lateinit var mListener: OnInformationListener
        var user: ArrayList<UserModel> = ArrayList<UserModel>()

        override fun onPreExecute() {
            super.onPreExecute()
            try {
                //                pd.setTitle("Please wait ...");
                //                pd.show();
            } catch (e: java.lang.Exception) {
                e.cause
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            var stream: String? = null
            val url = params[0]
            val http = HttpDataHandler()
            stream = http.getHTTPData(url?.toString())
            return stream
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            //Tries need to be removed will do for now
            try {
                val gson = Gson()
                val listType: Type = object : TypeToken<List<UserModel?>?>() {}.type
                user = gson.fromJson(s, listType)
                arrayList.add(user)
                val informationStore = InformationStore()
                informationStore.getUserData()
            } catch (e: java.lang.Exception) {
                e.cause
            }
        }
    }

    class PostData(private val userModel: UserModel) : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }


        override fun doInBackground(vararg params: String): String {
            val urlString = params[0]
            val hh = HttpDataHandler()
            val data = JSONObject()
            try {
                data.put("name", userModel.name)
                data.put("email", userModel.name)
                data.put("password", userModel.name)
                data.put("username", userModel.name)
                val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val currentDateandTime: String = sdf.format(Date())
                data.put("signupdate", currentDateandTime)
                hh.PostHttpData(urlString, data)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return ""
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
        }
    }
}