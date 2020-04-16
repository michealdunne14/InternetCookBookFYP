package com.example.internetcookbook.network

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.fragmentview.validFoodItems
import com.example.internetcookbook.helper.*
import com.example.internetcookbook.models.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.create
import okio.IOException
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class InformationStore(val context: Context, val internetConnection: Boolean) {
    var client = OkHttpClient()
    var userMaster = UserMasterModel()
    var imageArrayList = ArrayList<Bitmap>()
    var postData = ArrayList<DataModel?>()
    var filterArrayList = ArrayList<DataModel?>()
    var listFoodArray = ArrayList<FoodMasterModel>()
    var userPostData = ArrayList<DataModel?>()
    var cupboardData = ArrayList<FoodMasterModel>()
    var ingredientsList = ArrayList<FoodMasterModel>()
    var basketData = ArrayList<FoodMasterModel>()
    var followingData = ArrayList<UserMasterModel>()
    var userLocalStore = mutableListOf<UserMasterModel>()
    var searchingFollow = ArrayList<UserMasterModel>()
    var searchingIngredients = ArrayList<FoodMasterModel>()
    var ingredientsArrayList = ArrayList<FoodMasterModel>()
    lateinit var emailSearchArray: Array<UserModel>
    lateinit var makeUserName: UserMasterModel
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

    fun putExpireYes(oid: String) {
        val formBody: RequestBody = FormBody.Builder().build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/food/expirationTimeReliability/$oid")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
    }

    fun putExpireNo(oid: String) {
        val formBody: RequestBody = FormBody.Builder().build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/food/removeExpirationTime/$oid")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
    }


    fun putShopNo(oid: String) {
        val formBody: RequestBody = FormBody.Builder().build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/food/removeShop/$oid")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
    }

    fun putShopYes(oid: String) {
        val formBody: RequestBody = FormBody.Builder().build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/food/shopReliability/$oid")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
    }

    fun putPriceNo(oid: String) {
        val formBody: RequestBody = FormBody.Builder().build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/food/removePrice/$oid")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
    }

    fun putPriceYes(oid: String) {
        val formBody: RequestBody = FormBody.Builder().build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/food/priceReliability/$oid")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
    }

    fun putImageNo(oid: String) {
        val formBody: RequestBody = FormBody.Builder().build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/food/removeImagePath/$oid")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
    }

    fun putImageYes(oid: String) {
        val formBody: RequestBody = FormBody.Builder().build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/food/imagePathReliability/$oid")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
    }


    fun findEmail(userModel: UserModel): UserMasterModel? {
        if (internetConnection) {
            lateinit var emailSearch: UserMasterModel

            val formBody: RequestBody = FormBody.Builder()
                .add("email", userModel.email)
                .add("password",userModel.password).build()

            val request = Request.Builder()
                .url("http://34.244.232.228:3000/user/email")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful){
                    Toast.makeText(context,"Error with database",Toast.LENGTH_LONG).show()
                    throw IOException("Unexpected code $response")
                }

                val body = response.body!!.string()
                if (body == "No User found"){
                    return null
                }else if(body == "User email or password is incorrect"){
                    return null
                } else{
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
                .url("http://34.244.232.228:3000/user/username")
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
        followingData.add(userMasterModel)
        val formBody: RequestBody = FormBody.Builder()
            .add("followingoid", userMasterModel.user.oid).build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/user/follow/${userMaster.user.oid}")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
    }

    fun unFollowUser(userMasterModel: UserMasterModel) {
        followingData.forEachIndexed { index, following ->
            if (following.user.username == userMasterModel.user.username){
                followingData.removeAt(index)
            }
        }
        val formBody: RequestBody = FormBody.Builder()
            .add("followingoid", userMasterModel.user.oid).build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/user/unfollow/${userMaster.user.oid}")
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

    fun deleteFromBasket(foodModel: FoodMasterModel) {
        basketData.forEachIndexed { index, item ->
            if(item.food.name == foodModel.food.name)
            basketData.removeAt(index)
        }
        val formBody: RequestBody = FormBody.Builder()
            .add("id", userMaster.user.oid)
            .add("basketoid",foodModel.food.oid).build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/user/deleteItemFromBasket")
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
    }

    fun deleteFromCupboard(foodModel: FoodMasterModel){
        cupboardData.forEachIndexed { index, item ->
            if(item.food.name == foodModel.food.name)
                cupboardData.removeAt(index)
        }

        val formBody: RequestBody = FormBody.Builder()
            .add("id", userMaster.user.oid)
            .add("cupboardoid",foodModel.food.oid).build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/user/deleteItemFromCupboard")
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            print(response.body!!.string())
        }
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
            .url("http://34.244.232.228:3000/user/create")
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
                .url("http://34.244.232.228:3000/user/id/${userModel.oid}")
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

    fun findIngredients(postModel: PostModel) {
        ingredientsList.clear()
        lateinit var dataArray: ListFoodModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", postModel._id).build()

            val request = Request.Builder()
                .url("http://34.244.232.228:3000/food/findId")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val body = response.body!!.string()
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()

                dataArray = gson.fromJson(body, ListFoodModel::class.java)
                for (food in dataArray.foodArray) {
                    ingredientsList.add(food)
                }
            }
        }
    }

    fun getIngredients(): ArrayList<FoodMasterModel> {
        return ingredientsList
    }

    fun createFood(foodModel: FoodModel): Boolean {
        val formBody: RequestBody = FormBody.Builder()
            .add("name", foodModel.name)
            .add("price", foodModel.price.toString())
            .add("shop", foodModel.shop)
            .add("expirationTime",foodModel.expirationTime.toString()).build()

        val request: Request = Request.Builder()
            .url("http://34.244.232.228:3000/food/create")
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
            .url("http://34.244.232.228:3000/food/shop/${shop}")
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

    fun getProfileUserData(): ArrayList<DataModel?> {
        return userPostData
    }

    fun findFoodIngredients(searchedItem: String): ArrayList<FoodMasterModel>? {
        searchingIngredients.clear()
        if (internetConnection) {
            val formBody: RequestBody = FormBody.Builder()
                .add("name", searchedItem).build()

            val request = Request.Builder()
                .url("http://34.244.232.228:3000/food/partialTextSearch")
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

    fun findItem(item: String, saveShop: String): FoodMasterModel? {
        val basketSearch: FoodMasterModel? = basketData.find { p -> ((p.food.name == item) && (p.food.shop == saveShop)) }
        if (basketSearch == null){
            val cupboardSearch: FoodMasterModel? = cupboardData.find { p -> ((p.food.name == item) && (p.food.shop == saveShop))  }
            if (cupboardSearch == null){

                val formBody: RequestBody = FormBody.Builder()
                    .add("name", item)
                    .build()

                val request = Request.Builder()
                    .url("http://34.244.232.228:3000/food/name")
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
                .url("http://34.244.232.228:3000/user/cupboard/${userMaster.user.oid}")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                for (item in validFoodItems){
                    var foundItem = false
                    for (cupboard in cupboardData){
                        if (cupboard.food.name == item.food.name){
                            foundItem = true
                            for (userfood in userMaster.user.cupboard){
                                if (userfood.cupboardoid == cupboard.food.oid){
                                    val number = userfood.foodPurchasedCounter.toInt() + 1
                                    userfood.foodPurchasedCounter = number
                                }
                            }
                        }
                    }
                    if (!foundItem){
                        cupboardData.add(item)
                    }
                }
            }
        }
    }

    fun basketAdd(dataModel: DataModel) {
        val json = JSONObject()
        val jsonArray = JSONArray()
        for (ingredients in dataModel.post.ingredients) {
            val jsonObj = JSONObject()
            jsonObj.put("basketoid", ingredients.ingredientoid)
            jsonObj.put("counter",1)
            jsonArray.put(jsonObj)
            for (basket in userMaster.user.basket){
                if (basket.basketoid == ingredients.ingredientoid){
                    val counter = basket.counter.toInt()
                    basket.counter = counter+1
                }
            }
        }
        json.put("basket", jsonArray)


        val jsonString = json.toString()
        val body = create(JSON, jsonString)


        val request: Request = Request.Builder()
            .url("http://34.244.232.228:3000/user/basket/${userMaster.user.oid}")
            .post(body)
            .build()

        client.newCall(request).execute().use { response -> if (!response.isSuccessful) throw IOException("Unexpected code $response") }
    }

    fun cupboardAddManual() {
        val addingNewItemsToCupboard: MutableList<CupboardOidModel> = mutableListOf()
        for (item in ingredientsArrayList){
            addingNewItemsToCupboard.add(CupboardOidModel(item.food.oid,item.food.itemsCounter))
        }
        if (addingNewItemsToCupboard.isNotEmpty()){
            val json = JSONObject()
            val jsonArray = JSONArray()
            for (item in addingNewItemsToCupboard) {
                val jsonObj = JSONObject()
                jsonObj.put("cupboardoid", item.cupboardoid)
                jsonObj.put("foodPurchasedCounter", 1)
                jsonArray.put(jsonObj)
            }
            json.put("cupboard", jsonArray)

            cupboardData.addAll(ingredientsArrayList)


            val jsonString = json.toString()
            val body = create(JSON, jsonString)


            val request: Request = Request.Builder()
                .url("http://34.244.232.228:3000/user/cupboard/${userMaster.user.oid}")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
            }
        }
    }

    fun basketAddManual() {
        val json = JSONObject()
        val jsonArray = JSONArray()
        for (ingredients in ingredientsArrayList) {
            val jsonObj = JSONObject()
            jsonObj.put("basketoid", ingredients.food.oid)
            jsonObj.put("counter",1)
            jsonArray.put(jsonObj)
            for (basket in userMaster.user.basket){
                if (basket.basketoid == ingredients.food.oid){
                    val counter = basket.counter.toInt()
                    basket.counter = counter+1
                }
            }
        }
        json.put("basket", jsonArray)

        basketData.addAll(ingredientsArrayList)

        val jsonString = json.toString()
        val body = create(JSON, jsonString)


        val request: Request = Request.Builder()
            .url("http://34.244.232.228:3000/user/basket/${userMaster.user.oid}")
            .post(body)
            .build()

        client.newCall(request).execute().use { response -> if (!response.isSuccessful) throw IOException("Unexpected code $response") }
    }

    fun userCreated() {
        userLocalStore.add(userMaster)
        serialize()
    }

    fun getHomeData(): ArrayList<DataModel?> {
        return postData
    }

    fun putHeart(postModel: PostModel) {
        val formBody: RequestBody = FormBody.Builder()
            .add("userId", postModel.useroid)
            .build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/post/heart/${postModel._id}")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body!!.string()
            println(body)
        }
    }

    fun doUpdateUserHeart(postModel: PostModel) {
        val formBody: RequestBody = FormBody.Builder()
            .add("userId", postModel.useroid)
            .build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/post/heart/user/${postModel._id}")
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body!!.string()
            println(body)
        }
    }

    fun removeHeart(postModel: PostModel){
        val formBody: RequestBody = FormBody.Builder()
            .add("userId", postModel.useroid)
            .build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/post/removeheart/${postModel._id}")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body!!.string()
            println(body)
        }
    }

    fun getPostData(){
        postData.clear()
        lateinit var dataArray: ListPostModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid).build()

            val request: Request = Request.Builder()
                .url("http://34.244.232.228:3000/post/id")
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
                    for (posts in dataArray.postArray) {
                        postData.add(posts)
                    }
                }
            }
        }
    }


    fun getFilterDataDifficulty(difficultyLevel: String) {
        filterArrayList.clear()
        lateinit var dataArray: ListPostModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid)
                .add("difficulty",difficultyLevel).build()

            val request: Request = Request.Builder()
                .url("http://34.244.232.228:3000/post/id")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                if (body == "No Posts Found") {
                    print("No Posts Found")
                }else if(body == "error"){
                    print("Error")
                } else {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    dataArray = gson.fromJson(body, ListPostModel::class.java)
                    for (posts in dataArray.postArray) {
                        filterArrayList.add(posts)
                    }
                }
            }
        }
    }

    fun getFilterDataTop() {
        filterArrayList.clear()
        lateinit var dataArray: ListPostModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid)
                .add("top","").build()

            val request: Request = Request.Builder()
                .url("http://34.244.232.228:3000/post/id")
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
                    for (posts in dataArray.postArray) {
                        filterArrayList.add(posts)
                    }
                }
            }
        }
    }

    fun getFilteredData(): ArrayList<DataModel?> {
        return filterArrayList
    }

    fun getMoreData(): Boolean {
        lateinit var dataArray: ListPostModel
        if (internetConnection) {
            val datesCollection = ArrayList<Date>()
            for(post in postData){
                val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                try {
                    val date: Date = format.parse(post!!.post.posttime)!!
                    datesCollection.add(date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            var postDate = datesCollection[datesCollection.size -1]

            datesCollection.forEachIndexed { index, date ->
                if (date.before(postDate)){
                    postDate = date
                }
            }

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid)
                .add("posttime", postDate.toString()).build()

            val request: Request = Request.Builder()
                .url("http://34.244.232.228:3000/post/newdata")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()

                return if(body == "no more posts") {
//                    postData.removeAt(postData.lastIndex)
                    false
                }else if(body == "no posts"){
                    false
                }else{
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    dataArray = gson.fromJson(body, ListPostModel::class.java)
                    for (posts in dataArray.postArray) {
                        postData.add(posts)
                    }
                    true
                }
            }
        }
        return false
    }

    fun getMoreDataDifficulty(): Boolean {
        lateinit var dataArray: ListPostModel
        if (internetConnection) {

            try {
                postData[postData.lastIndex]
            }catch (e: Exception){
                return false
            }
            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid)
                .add("posttime", postData[postData.lastIndex]!!.post.posttime).build()

            val request: Request = Request.Builder()
                .url("http://34.244.232.228:3000/post/newdata")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()

                return if(body == "no more posts") {
//                    postData.removeAt(postData.lastIndex)
                    false
                }else{
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    dataArray = gson.fromJson(body, ListPostModel::class.java)
                    for (posts in dataArray.postArray) {
                        postData.add(posts)
                    }
                    true
                }
            }
        }
        return false
    }

    fun getCupboardData(){
        lateinit var dataArray: ListFoodModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid).build()

            val request = Request.Builder()
                .url("http://34.244.232.228:3000/food/byIdLoad")
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
        basketData.clear()
        lateinit var dataArray: ListFoodModel
        if (internetConnection) {

            val formBody: RequestBody = FormBody.Builder()
                .add("id", userMaster.user.oid).build()

            val request = Request.Builder()
                .url("http://34.244.232.228:3000/food/byIdLoadBasket")
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
                .url("http://34.244.232.228:3000/user/following")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")


                val body = response.body!!.string()

                if (body == "No Following Data"){
                    print("No following data")
                } else {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()

                    dataArray = gson.fromJson(body, FollowListModel::class.java)
                    for (following in dataArray.userArray){
                        followingData.add(following)
                    }
                }
            }
        }
    }

    fun findFollowingData(): ArrayList<UserMasterModel> {
        return followingData
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

    fun updateItemCounterBasket(
        characterSearch: Int,
        foodModel: FoodMasterModel
    ) {
        if (internetConnection) {
            val json = JSONObject()
            json.put("basketoid", foodModel.food.oid)
            json.put("id", userMaster.user.oid)
            json.put("counter",characterSearch)

            val jsonString = json.toString()
            val formBody = create(JSON, jsonString)

            val request: Request = Request.Builder()
                .url("http://34.244.232.228:3000/user/changeCounterBasket")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                print(body)
            }
        }
    }


    fun updateItemCounterCupboard(
        characterSearch: Int,
        foodModel: FoodMasterModel
    ) {
        if (internetConnection) {
            val json = JSONObject()
            json.put("cupboardoid", foodModel.food.oid)
            json.put("id", userMaster.user.oid)
            json.put("foodPurchasedCounter",characterSearch)

            val jsonString = json.toString()
            val formBody = create(JSON, jsonString)

            val request: Request = Request.Builder()
                .url("http://34.244.232.228:3000/user/changeCounterCupboard")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                print(body)
            }
        }
    }


    fun ingredientsSearchHomeData(){
        filterArrayList.clear()
        lateinit var dataArray: ListPostModel
        if (internetConnection) {

            val json = JSONObject()
            val jsonArray = JSONArray()
            for (item in ingredientsArrayList) {
                val jsonObj = JSONObject()
                jsonObj.put("ingredientoid", item.food.oid)
                jsonArray.put(jsonObj)
            }
            json.put("ingredients", jsonArray)
            json.put("id", userMaster.user.oid)

            val jsonString = json.toString()
            val formBody = create(JSON, jsonString)

            val request: Request = Request.Builder()
                .url("http://34.244.232.228:3000/post/id")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                if (body == "no posts found") {
                    print("no posts found")
                } else {
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    dataArray = gson.fromJson(body, ListPostModel::class.java)
                    for (posts in dataArray.postArray) {
                        filterArrayList.add(posts)
                    }
                }
            }
        }
    }

    fun searchFollowing(query: CharSequence?): ArrayList<UserMasterModel> {
        val searchedPosts = ArrayList<UserMasterModel>()
        for(following in followingData){
            if (following.user.name.contains(query!!)){
                searchedPosts.add(following)
            }
        }
        return searchedPosts
    }

    fun searchItemsInitial(storedFood: ArrayList<FoodMasterModel>) {
        for (food in storedFood) {
            val basketSearch: FoodMasterModel? = basketData.find { p -> ((p.food.name == food.food.name) && (p.food.shop == food.food.shop)) }
            if (basketSearch == null) {
                val cupboardSearch: FoodMasterModel? = cupboardData.find { p ->  ((p.food.name == food.food.name) && (p.food.shop == food.food.shop)) }
                if (cupboardSearch != null) {
                    listFoodArray.add(cupboardSearch)
                }
            }else{
                listFoodArray.add(basketSearch)
            }
        }

        for (food in listFoodArray){
            storedFood.remove(food)
        }
        lateinit var listFoodModel: ListFoodModel
        if (internetConnection) {

            val json = JSONObject()
            val jsonArray = JSONArray()
            for (item in storedFood) {
                val jsonObj = JSONObject()
                jsonObj.put("name", item.food.name)
                jsonObj.put("shop", item.food.shop)
                jsonArray.put(jsonObj)
            }
            json.put("foodArray", jsonArray)

            val jsonString = json.toString()
            val body = create(JSON, jsonString)

            val request: Request = Request.Builder()
                .url("http://34.244.232.228:3000/food/foodLoad")
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
            .url("http://34.244.232.228:3000/post/comment/${dataModel.post._id}")
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
                .url("http://34.244.232.228:3000/food/shop")
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

    fun findUser(postModel: PostModel) {
        lateinit var user: UserMasterModel
        if (internetConnection) {


            val request = Request.Builder()
                .url("http://34.244.232.228:3000/user/id/${postModel.useroid}")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()

                user = gson.fromJson(body, UserMasterModel::class.java)
                makeUserName = user
            }
        }
    }

    fun getMakeName(): UserMasterModel {
        return makeUserName
    }

    private val MEDIA_TYPE_JPEG = "image/jpeg".toMediaType()

    fun uploadImagesPost(
        oid: String,
        listofImages: ArrayList<String>,
        postModel: PostModel
    ): Boolean {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        val userImages: MutableList<String?> = mutableListOf()
        var counter = 0
        for(image in listofImages) {
            val bitmap = readImageFromPath(context, image)
            val stream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            val byteArray: ByteArray = stream.toByteArray()
            val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            userImages.add(encoded)
            counter++
            builder.addFormDataPart("imageFiles",  "${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + counter}.jpeg", create(MEDIA_TYPE_JPEG, byteArray));
        }


        val requestBody: RequestBody = builder.build()

        val request = Request.Builder()
            .url("http://34.244.232.228:3000/post/upload/${oid}")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val body = response.body!!.string()
            return body == "All images posted"
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
            .url("http://34.244.232.228:3000/user/upload/${oid}")
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
            .url("http://34.244.232.228:3000/food/upload/${oid}")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body!!.string()

            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()
            val food = gson.fromJson(body,FoodMasterModel::class.java)
            validFoodItems.add(food)
            return true
        }
    }

    fun createPost(
        postModel: PostModel,
        listofImages: ArrayList<String>,
        methodStepsArrayList: ArrayList<String>,
        view: BaseView
    ) {
        val jsonArray = JSONArray()
        for (methodstep in methodStepsArrayList) {
            val jsonObj = JSONObject()
            jsonObj.put("methodStep", methodstep)
            jsonArray.put(jsonObj)
        }

        val jsonArrayIngre = JSONArray()
        for (ingredient in ingredientsArrayList) {
            val jsonObj = JSONObject()
            jsonObj.put("ingredientoid", ingredient.food.oid)
            jsonArrayIngre.put(jsonObj)
        }

        val postModelJson = JSONObject()
        postModelJson.put("title",postModel.title)
        postModelJson.put("description",postModel.description)
        postModelJson.put("difficulty",postModel.difficulty)
        postModelJson.put("completionTime",postModel.completionTime)
        postModelJson.put("method", jsonArray)
        postModelJson.put("ingredients",jsonArrayIngre)

        val jsonString = postModelJson.toString()
        val body = create(JSON, jsonString)

        val request: Request = Request.Builder()
            .url("http://34.244.232.228:3000/post/create/${userMaster.user.oid}")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")


            val bodyResponse = response.body!!.string()
            if(bodyResponse.contains("PostOid")){
                print("Success Upload now uploading images ...")
                val wordList = bodyResponse.split(" ")
                doAsync {
                    uploadImagesPost(wordList[1], listofImages,postModel)
                    onComplete {
                        view.returnToPager()
                        listofImages.clear()
                        methodStepsArrayList.clear()
                    }
                }
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
            .url("http://34.244.232.228:3000/post/create")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response -> return response.body!!.toString() }
    }

    fun logoutUser() {
        userLocalStore.clear()
        imageArrayList.clear()
        postData.clear()
        filterArrayList.clear()
        listFoodArray.clear()
        userPostData.clear()
        cupboardData.clear()
        basketData.clear()
        followingData.clear()
        userLocalStore.clear()
        searchingFollow.clear()
        searchingIngredients.clear()
        ingredientsArrayList.clear()
        serialize()
    }

    fun storeImage(bitmap: Bitmap) {
        storedImage = bitmap
    }

    fun getImage(): Bitmap? {
        return storedImage
    }
}