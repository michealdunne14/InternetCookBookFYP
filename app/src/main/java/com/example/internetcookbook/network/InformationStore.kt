package com.example.internetcookbook.network

import android.os.AsyncTask
import com.example.internetcookbook.models.UserModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

interface OnInformationListener {
    fun onUserComplete(user: ArrayList<UserModel>)
}

class InformationStore : OnInformationListener  {

    var arrayList: ArrayList<UserModel> = ArrayList()
    val common = Common()

//    GetData().execute(common.getAddressApiName())


    fun getUserData(): ArrayList<UserModel> {
        return arrayList
    }

    override fun onUserComplete(user: ArrayList<UserModel>) {
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
                mListener.onUserComplete(user)
            } catch (e: java.lang.Exception) {
                e.cause
            }
        }
    }
}