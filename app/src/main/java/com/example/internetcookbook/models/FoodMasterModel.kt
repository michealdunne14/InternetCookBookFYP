package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodMasterModel(
    val image: Array<String?> = arrayOfNulls(5),
    val food: FoodModel = FoodModel()
) : Parcelable