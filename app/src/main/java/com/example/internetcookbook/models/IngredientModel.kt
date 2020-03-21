package com.example.internetcookbook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IngredientModel (
    val ingredientoid: String = ""
): Parcelable