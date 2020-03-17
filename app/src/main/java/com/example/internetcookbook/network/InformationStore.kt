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
    var listFoodArray = ArrayList<FoodMasterModel>()
    var listDataModel = ListPostModel()
    var userPostData = ArrayList<DataModel?>()
    var cupboardData = ArrayList<FoodMasterModel>()
    var basketData = ArrayList<FoodMasterModel>()
    var followingData = FollowListModel()
    var userLocalStore = mutableListOf<UserMasterModel>()
    var searchingFollow = ArrayList<UserMasterModel>()
    var searchingIngredients = ArrayList<FoodMasterModel>()
    var ingredientsArrayList = ArrayList<FoodMasterModel>()
    lateinit var emailSearchArray: Array<UserModel>
    var storedImage: Bitmap? = null
    var foodCreatePage = false

    val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

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

    fun searchForFollowing(userSearch: CharSequence): ArrayList<UserMasterModel>? {
        if (internetConnection) {
            val formBody: RequestBody = FormBody.Builder()
                .add("username", userSearch.toString()).build()

            val request = Request.Builder()
                .url("http://52.51.34.156:3000/user/username")
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
                    val listUserModel =  gson.fromJson(body,ListUserModel::class.java)
                    for (user in listUserModel.userArray){
                        searchingFollow.add(user)
                    }
                    return searchingFollow
                }
            }
        } else {
            return null
        }
    }

    fun searchFollowingSearched(characterSearch: CharSequence): ArrayList<UserMasterModel> {
        val searchedPosts = ArrayList<UserMasterModel>()
        for(searchedFollower in searchingFollow){
            if (searchedFollower.user.username.contains(characterSearch)){
                searchedPosts.add(searchedFollower)
            }
        }
        return searchedPosts
    }

    fun followUser(userMasterModel: UserMasterModel) {
        val formBody: RequestBody = FormBody.Builder()
            .add("followingoid", userMasterModel.user.oid).build()

        val request = Request.Builder()
            .url("http://52.51.34.156:3000/user/follow/${userMaster.user.oid}")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
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

    fun foodCreatePageUpdate(){
        foodCreatePage = !foodCreatePage
    }

    fun foodCreatePage(): Boolean {
        return foodCreatePage
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

    fun findFoodIngredients(searchedItem: String): ArrayList<FoodMasterModel>? {
        searchingIngredients.clear()
        if (internetConnection) {
            val formBody: RequestBody = FormBody.Builder()
                .add("name", searchedItem).build()

            val request = Request.Builder()
                .url("http://52.51.34.156:3000/food/partialTextSearch")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()
                val listUserModel =  gson.fromJson(body,ListFoodModel::class.java)
                for (food in listUserModel.foodArray){
                    searchingIngredients.add(food)
                }
                return searchingIngredients
            }
        } else{
            return null
        }
    }

    fun searchIngredients(searchedItem: String): ArrayList<FoodMasterModel> {
        val searchedPosts = ArrayList<FoodMasterModel>()
        for(searchedFollower in searchingIngredients){
            if (searchedFollower.food.name.contains(searchedItem)){
                searchedPosts.add(searchedFollower)
            }
        }
        return searchedPosts
    }

    fun defaultIngredients(): ArrayList<FoodMasterModel> {
        val foodArrayList = ArrayList<FoodMasterModel>()
        foodArrayList.addAll(cupboardData)
        foodArrayList.addAll(basketData)
        return foodArrayList
    }

    fun ingredientsAddedToRecipe(): ArrayList<FoodMasterModel> {
       return ingredientsArrayList
    }

    fun findItem(item: String): FoodMasterModel? {
        val basketSearch: FoodMasterModel? = basketData.find { p -> p.food.name == item }
        if (basketSearch == null){
            val cupboardSearch: FoodMasterModel? = cupboardData.find { p -> p.food.name == item }
            if (cupboardSearch == null){

                val formBody: RequestBody = FormBody.Builder()
                    .add("name", item)
                    .build()

                val request = Request.Builder()
                    .url("http://52.51.34.156:3000/food/name")
                    .post(formBody)
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
            addingNewItemsToCupboard.add(CupboardOidModel(item.food.oid,item.food.itemsCounter))
        }
        if (addingNewItemsToCupboard.isNotEmpty()){
            val json = JSONObject()
            val jsonArray = JSONArray()
            for (item in addingNewItemsToCupboard) {
                val jsonObj = JSONObject()
                jsonObj.put("cupboardoid", item.cupboardoid)
                jsonObj.put("foodPurchasedCounter", item.foodPurchasedCounter)
                jsonArray.put(jsonObj)
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


//    fun putMethod(id: String,step: String) {
//        val formBody: RequestBody = FormBody.Builder()
//            .add("methodStep", step).build()
//
//        val request: Request = Request.Builder()
//            .url("http://52.51.34.156:3000/post/method/${id}")
//            .put(formBody)
//            .build()
//
//        client.newCall(request).execute().use { response -> print(response.body!!.toString()) }
//    }

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
                    for (posts in dataArray.postArray) {
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
        lateinit var dataArray: ListFoodModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid).build()

            val request = Request.Builder()
                .url("http://52.51.34.156:3000/food/byIdLoad")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val body = response.body!!.string()
                if (body == "no cupboard data"){
                    print("no cupboard data")
                }else {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()

                    dataArray = gson.fromJson(body, ListFoodModel::class.java)
                    for (food in dataArray.foodArray) {
                        cupboardData.add(food)
                    }
                }
            }
        }
    }


    fun getBasketData(){
        lateinit var dataArray: ListFoodModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid).build()

            val request = Request.Builder()
                .url("http://52.51.34.156:3000/food/byIdLoadBasket")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                if (body == "no basket data"){
                    print("No Basket Data")
                }else {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()

                    dataArray = gson.fromJson(body, ListFoodModel::class.java)
                    for (food in dataArray.foodArray) {
                        basketData.add(food)
                    }
                }
            }
        }
    }

    fun getFollowingData(){
        lateinit var dataArray: FollowListModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid).build()

            val request = Request.Builder()
                .url("http://52.51.34.156:3000/user/following")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")


                val body = response.body!!.string()
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()

                dataArray = gson.fromJson(body, FollowListModel::class.java)
                followingData = dataArray
            }
        }
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

    fun searchItemsInitial(storedFood: ArrayList<FoodMasterModel>) {
        lateinit var listFoodModel: ListFoodModel
        if (internetConnection) {

            val json = JSONObject()
            val jsonArray = JSONArray()
            for (item in storedFood) {
                val jsonObj = JSONObject()
                jsonObj.put("name", item.food.name)
                jsonArray.put(jsonObj)
            }
            json.put("foodArray", jsonArray)

            val jsonString = json.toString()
            val body = create(JSON, jsonString)

            val request: Request = Request.Builder()
                .url("http://52.51.34.156:3000/food/foodLoad")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body!!.string()
                if (responseBody.toLowerCase() == "no items found") {
                    print("No items found")
                } else {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    listFoodModel = gson.fromJson(responseBody, ListFoodModel::class.java)
                    for (food in listFoodModel.foodArray) {
                        listFoodArray.add(food)
                    }
                }
            }
        }
    }

    fun sendComment(
        comment: String,
        dataModel: DataModel
    ) {
        val formBody: RequestBody = FormBody.Builder()
            .add("commentString", comment)
            .add("userOid", userMaster.user.oid).build()

        dataModel.post.comments.add(CommentModel(comment,userMaster.user.oid))

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/post/comment/${dataModel.post._id}")
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            println(response.body!!.string())
        }
    }

    fun searchShop(textArrayList: ArrayList<String>): FoodModel? {
        lateinit var listFoodModel: ListFoodModel
        if (internetConnection) {
            val json = JSONObject()
            val jsonArray = JSONArray()
            for (item in textArrayList) {
                val jsonObj = JSONObject()
                jsonObj.put("shop", item)
                jsonArray.put(jsonObj)
            }
            json.put("shopArray", jsonArray)

            val jsonString = json.toString()
            val body = create(JSON, jsonString)

            val request: Request = Request.Builder()
                .url("http://52.51.34.156:3000/food/shop")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body!!.string()
                return if (responseBody == "No Shops Found") {
                    print("No Shops Found")
                    null
                } else {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    val foodModel = gson.fromJson(responseBody, FoodModel::class.java)
                    foodModel
                }
            }
        }
        return null
    }

    fun listOfFoodArray(): ArrayList<FoodMasterModel> {
        return listFoodArray
    }

    private val MEDIA_TYPE_JPEG = "image/jpeg".toMediaType()

    fun uploadImagesPost(oid: String, listofImages: ArrayList<String>) {
        for(image in listofImages) {
            val bitmap = readImageFromPath(context, image)
            val stream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            val byteArray: ByteArray = stream.toByteArray()

            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("imageFiles", "${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())}.jpeg", create(MEDIA_TYPE_JPEG, byteArray))
                .build()

            val request = Request.Builder()
                .url("http://52.51.34.156:3000/post/upload/${oid}")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")


                val body = response.body!!.string()
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()
                val post = gson.fromJson(body,DataModel::class.java)
                userPostData.add(post)
            }
        }
    }

    fun uploadImagesUser(oid: String, image: String): Boolean {
        val bitmap = readImageFromPath(context, image)
        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("profilePic", "${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())}.jpeg", create(MEDIA_TYPE_JPEG, byteArray))
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
        storedImage!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray: ByteArray = stream.toByteArray()

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("foodPic", "${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())}.jpeg", create(MEDIA_TYPE_JPEG, byteArray))
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
            val food = gson.fromJson(body,FoodMasterModel::class.java)
            newFood(food)
            return true
        }
    }
    var newFoodData = FoodMasterModel()

    fun newFood(food: FoodMasterModel) {
        newFoodData = food
    }

    fun newFoodData(): FoodMasterModel {
        return newFoodData
    }


    fun createPost(
        postModel: PostModel,
        listofImages: ArrayList<String>
    ) {
        val jsonArray = JSONArray()
        for (methodstep in postModel.method) {
            val jsonObj = JSONObject()
            jsonObj.put("methodStep", methodstep)
            jsonArray.put(jsonObj)
        }
        val jsonArrayIngre = JSONArray()
        for (methodstep in postModel.ingredients) {
            val jsonObj = JSONObject()
            jsonObj.put("ingredientoid", methodstep.food.oid)
            jsonArrayIngre.put(jsonObj)
        }

        val postModelJson = JSONObject()
        postModelJson.put("title",postModel.title)
        postModelJson.put("description",postModel.description)
        postModelJson.put("method", jsonArray)
        postModelJson.put("ingredients",jsonArrayIngre)

        val jsonString = postModelJson.toString()
        val body = create(JSON, jsonString)

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/post/create/${userMaster.user.oid}")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")


            val bodyResponse = response.body!!.string()
            if(bodyResponse.contains("PostOid")){
                print("Success Upload now uploading images ...")
                val wordList = bodyResponse.split(" ")
                uploadImagesPost(wordList[1],listofImages)
            }
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