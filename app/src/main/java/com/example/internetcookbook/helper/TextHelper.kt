package com.example.internetcookbook.helper

fun capitalize(str: String): String {
    return if (str.isEmpty()) {
        str
    } else {
        str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase()
    }
}