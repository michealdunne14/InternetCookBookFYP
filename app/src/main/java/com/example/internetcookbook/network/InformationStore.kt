package com.example.internetcookbook.network

import android.content.Context
import android.graphics.Bitmap
import com.example.internetcookbook.R
import com.example.internetcookbook.helper.exists
import com.example.internetcookbook.helper.read
import com.example.internetcookbook.helper.readImageFromPath
import com.example.internetcookbook.helper.write
import com.example.internetcookbook.models.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okio.IOException
import kotlin.collections.ArrayList
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.create
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.TimeUnit


class InformationStore(val context: Context, val internetConnection: Boolean) {
    var client = OkHttpClient()
    var userMaster = UserMasterModel()
    var imageArrayList = ArrayList<Bitmap>()
    var postData = ArrayList<DataModel?>()
    var listDataModel = ListPostModel()
    var userPostData = ArrayList<DataModel?>()
    var cupboardData = ArrayList<FoodMasterModel>()
    var basketData = ArrayList<FoodMasterModel>()
    var followingData = FollowListModel()
    var userLocalStore = mutableListOf<UserMasterModel>()
    lateinit var emailSearchArray: Array<UserModel>
    var storedImage: Bitmap? = null


    val JSON_FILE = "InformationStore.json"
    val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
    val listType = object : TypeToken<ArrayList<UserMasterModel>>() {}.type

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

    fun findEmail(userModel: UserModel): UserMasterModel? {
        if (internetConnection) {
            lateinit var emailSearch: UserMasterModel

            val formBody: RequestBody = FormBody.Builder()
                .add("email", userModel.email)
                .add("password",userModel.password).build()

            val request = Request.Builder()
                .url("http://52.51.34.156:3000/user/email")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                if (body == "No User found"){
                    return null
                }else{
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()

                    emailSearch = gson.fromJson(body,UserMasterModel::class.java)
                }
            }
            if (!emailSearch.user.email.isEmpty()) {
                userLocalStore.clear()
                userMaster = emailSearch
                userMaster.user.loggedIn = true
                userLocalStore.add(userMaster)
                serialize()
                return userMaster
            } else {
                return null
            }
        } else {
            return null
        }
    }

    fun getCurrentUser(): UserMasterModel {
        if (userLocalStore.isNotEmpty()) {
            userMaster = userLocalStore[0]
        }
        return userMaster
    }

    fun clearCurrentUser(){
        userLocalStore.clear()
        serialize()
    }

    fun createUser(
        userModel: UserModel,
        profilePicture: String
    ): Boolean {
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

        lateinit var user:UserModel
        client.newCall(request).execute().use { response ->
            val body = response.body!!.string()
            if (body.contains("MongoError")){
                return false
            }
            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()
            user = gson.fromJson(body,UserModel::class.java)
        }
        return uploadImagesUser(user.oid,profilePicture)
    }

    fun updateUserInfo(userModel: UserModel): UserMasterModel? {
        if (internetConnection) {
            lateinit var emailSearch: UserMasterModel
            val request = Request.Builder()
                .url("http://52.51.34.156:3000/user/id/${userModel.oid}")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    logoutUser()
                    throw IOException("Unexpected code $response")
                }

                val body = response.body!!.string()
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()

                emailSearch = gson.fromJson(body,UserMasterModel::class.java)
            }
            if (!emailSearch.user.email.isEmpty()) {
                userLocalStore.clear()
                userMaster = emailSearch
                userMaster.user.loggedIn = true
                userLocalStore.add(userMaster)
                serialize()
                return userMaster
            } else {
                return null
            }
        } else {
            return null
        }
    }

    fun createFood(foodModel: FoodModel): Boolean {
        val formBody: RequestBody = FormBody.Builder()
            .add("name", foodModel.name)
            .add("price", foodModel.price.toString())
            .add("shop", foodModel.shop).build()

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/food/create")
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->

            val body = response.body!!.string()
            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()
            val food = gson.fromJson(body,FoodModel::class.java)


            return uploadImagesFood(food.oid)
        }
    }

    fun findShop(shop: String): String? {
        lateinit var foodModel: Array<FoodModel>
        val request = Request.Builder()
            .url("http://52.51.34.156:3000/food/shop/${shop}")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body!!.string()
            if(body == "[]"){
                return context.getString(R.string.huh_no_shop)
            }else {
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()

                foodModel = gson.fromJson<Array<FoodModel>>(body, Array<FoodModel>::class.java)
            }
        }
        return if (foodModel.isNotEmpty()) {
            foodModel[0].shop
        } else {
            context.getString(R.string.huh_no_shop)
        }
    }

    fun getUserData() {
        userPostData.clear()
        lateinit var dataArray: DataModel
        if (internetConnection) {
//            for (post in userMaster.user.posts) {
                val request = Request.Builder()
                    .url("http://52.51.34.156:3000/user/id/5e664afc5d40c06ff2ba67cc")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    val body = response.body!!.string()
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    dataArray = gson.fromJson(body, DataModel::class.java)
                    userPostData.add(dataArray)
                }
//            }
        }
    }

    fun getProfileUserData(): ArrayList<DataModel?> {
        return userPostData
    }

    fun findItem(item: String): FoodMasterModel? {
        val basketSearch: FoodMasterModel? = basketData.find { p -> p.food.name == item }
        if (basketSearch == null){
            val cupboardSearch: FoodMasterModel? = cupboardData.find { p -> p.food.name == item }
            if (cupboardSearch == null){
                val request = Request.Builder()
                    .url("http://52.51.34.156:3000/food/name/${item}")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val body = response.body!!.string()
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    return gson.fromJson(body, FoodMasterModel::class.java)
                }
            }else{
                return cupboardSearch
            }
        }else{
            return basketSearch
        }
    }

    fun cupboardAdd(validFoodItems: ArrayList<FoodMasterModel>) {
        val addingNewItemsToCupboard: MutableList<CupboardOidModel> = mutableListOf()
        for (item in validFoodItems){
            addingNewItemsToCupboard.add(CupboardOidModel(item.food.oid,1))
        }
        if (addingNewItemsToCupboard.isNotEmpty()){
            val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

            val json = JSONObject()
            val jsonArray = JSONArray()
            for (item in addingNewItemsToCupboard) {
                jsonArray.put(CupboardOidModel(item.cupboardoid,item.foodPurchasedCounter))
            }
            json.put("cupboard", jsonArray)


            val jsonString = json.toString()
            val body = create(JSON, jsonString)


            val request: Request = Request.Builder()
                .url("http://52.51.34.156:3000/user/cupboard/${userMaster.user.oid}")
                .post(body)
                .build()

            client.newCall(request).execute().use { response -> if (!response.isSuccessful) throw IOException("Unexpected code $response") }
        }
    }

    fun userCreated() {
        userLocalStore.add(userMaster)
        serialize()
    }

    fun getHomeData(): ArrayList<DataModel?> {
        return postData
    }

    fun putHeart(id: String) {

        val formBody: RequestBody = FormBody.Builder().build()

        val request = Request.Builder()
            .url("http://52.51.34.156:3000/post/heart/${id}")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

           print(response.body!!.string())
        }
    }


    fun putMethod(id: String,step: String) {
        val formBody: RequestBody = FormBody.Builder()
            .add("methodStep", step).build()

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/post/method/${id}")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response -> print(response.body!!.toString()) }
    }

    fun getPostData(){
        postData.clear()
        lateinit var dataArray: ListPostModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid).build()

            val request: Request = Request.Builder()
                .url("http://52.51.34.156:3000/post/id")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                if (body == "No Posts Found") {
                    print("No Posts Found")
                } else {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    dataArray = gson.fromJson(body, ListPostModel::class.java)
                    listDataModel = dataArray
                    for (posts in listDataModel.postArray) {
                        postData.add(posts!!)
                    }
                }
            }
        }
    }

    fun getMoreData(){
        lateinit var dataArray: ListPostModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid).build()

            val request: Request = Request.Builder()
                .url("http://52.51.34.156:3000/post/id")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()
                dataArray = gson.fromJson(body, ListPostModel::class.java)
                listDataModel = dataArray
                postData.removeAt(postData.size - 1)
                for (posts in listDataModel.postArray){
                    postData.add(posts!!)
                }
            }
        }
    }

    fun getCupboardData(){
//        cupboardData.clear()
//        lateinit var dataArray: FoodMasterModel
//        if (internetConnection) {
//            for (cupboard in userMaster.user.cupboard) {
//                val request = Request.Builder()
//                    .url("http://52.51.34.156:3000/food/foodId/${cupboard.cupboardoid}")
//                    .build()
//
//                client.newCall(request).execute().use { response ->
//                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//
//                    val body = response.body!!.string()
//                    val gsonBuilder = GsonBuilder()
//                    val gson = gsonBuilder.create()
//
//                    dataArray = gson.fromJson(body, FoodMasterModel::class.java)
//                    cupboardData.add(dataArray)
//                }
//            }
//        }
    }


    fun getBasketData(){
//        basketData.clear()
//        lateinit var dataArray: FoodMasterModel
//        if (internetConnection) {
//            for (basket in userMaster.user.basket) {
//                val request = Request.Builder()
//                    .url("http://52.51.34.156:3000/food/foodId/${basket.basketoid}")
//                    .build()
//
//                client.newCall(request).execute().use { response ->
//                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                    val body = response.body!!.string()
//                    val gsonBuilder = GsonBuilder()
//                    val gson = gsonBuilder.create()
//
//                    dataArray = gson.fromJson(body, FoodMasterModel::class.java)
//                    basketData.add(dataArray)
//                }
//            }
//        }
    }

    fun getFollowingData(){
//        lateinit var dataArray: FollowListModel
//        if (internetConnection) {
//
//            val formBody: RequestBody = FormBody.Builder()
//                .add("id", userMaster.user.oid).build()
//
//            val request = Request.Builder()
//                .url("http://52.51.34.156:3000/user/following")
//                .post(formBody)
//                .build()
//
//            client.newCall(request).execute().use { response ->
//                if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//
//                val body = response.body!!.string()
//                val gsonBuilder = GsonBuilder()
//                val gson = gsonBuilder.create()
//
//                dataArray = gson.fromJson(body, FollowListModel::class.java)
//                followingData = dataArray
//            }
//        }
    }

    fun findFollowingData(): ArrayList<UserMasterModel> {
        return followingData.userArray as ArrayList<UserMasterModel>
    }

    fun findBasketData(): ArrayList<FoodMasterModel> {
        return basketData
    }

    fun findCupboardData(): ArrayList<FoodMasterModel> {
        return cupboardData
    }

    fun searchCupboard(query: CharSequence?): ArrayList<FoodMasterModel> {
        val searchedPosts = ArrayList<FoodMasterModel>()
        for(marks in cupboardData){
            if (marks.food.name.contains(query!!)){
                searchedPosts.add(marks)
            }
        }
        return searchedPosts
    }


    fun searchBasket(query: CharSequence?): ArrayList<FoodMasterModel> {
        val searchedPosts = ArrayList<FoodMasterModel>()
        for(marks in basketData){
            if (marks.food.name.contains(query!!)){
                searchedPosts.add(marks)
            }
        }
        return searchedPosts
    }

    fun searchFollowing(query: CharSequence?): ArrayList<UserMasterModel> {
        val searchedPosts = ArrayList<UserMasterModel>()
        for(marks in followingData.userArray){
            if (marks.user.name.contains(query!!)){
                searchedPosts.add(marks)
            }
        }
        return searchedPosts
    }

    private val MEDIA_TYPE_PNG = "image/png".toMediaType()

    fun uploadImagesPost(oid: String, listofImages: ArrayList<String>) {
        val file = File(listofImages[0])

        for(i in listofImages) {
            val bitmap = readImageFromPath(context, i)
            val stream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()

            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("imageFiles", "${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())}.png", create(MEDIA_TYPE_PNG, byteArray))
                .build()

            val request = Request.Builder()
                .url("http://52.51.34.156:3000/post/upload/${oid}")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                println(response.body!!.string())
            }
        }
    }

    fun uploadImagesUser(oid: String, image: String): Boolean {
        val bitmap = readImageFromPath(context, image)
        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("profilePic", "${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())}.png", create(MEDIA_TYPE_PNG, byteArray))
            .build()

        val request = Request.Builder()
            .url("http://52.51.34.156:3000/user/upload/${oid}")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body!!.string()

            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()
            val user = gson.fromJson(body,UserModel::class.java)
            userMaster.user = user
            userMaster.user.loggedIn = true
            return true
        }
    }

    fun uploadImagesFood(oid: String): Boolean {
        val stream = ByteArrayOutputStream()
        storedImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("foodPic", "${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())}.png", create(MEDIA_TYPE_PNG, byteArray))
            .build()

        val request = Request.Builder()
            .url("http://52.51.34.156:3000/food/upload/${oid}")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body!!.string()

            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()
//            val food = gson.fromJson(body,FoodMasterModel::class.java)
            return true
        }
    }


    fun createPost(postModel: PostModel): PostModel? {
        val formBody: RequestBody = FormBody.Builder()
            .add("title", postModel.title)
            .add("description", postModel.description).build()

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/post/create/${userMaster.user.oid}")
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")


            val body = response.body!!.string()
            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()

            val data = gson.fromJson(body, PostModel::class.java)
            return data

        }
    }


    fun putPostToUser(postModel: PostModel): String? {
        val formBody: RequestBody = FormBody.Builder()
            .add("title", postModel.title)
            .add("description", postModel.description)
            .add("image", "randomimage")
            .add("useroid", userMaster.user.oid).build()

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

    fun storeImage(bitmap: Bitmap) {
        storedImage = bitmap
    }

    fun getImage(): Bitmap? {
        return storedImage
    }
}