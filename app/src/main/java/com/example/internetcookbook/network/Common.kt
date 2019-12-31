package com.example.internetcookbook.network


class Common {

    fun getAddressApiName(): String? {
        val baseUrl = java.lang.String.format("http://52.51.34.156:3000/user/password/testpass")
        val stringBuilder = StringBuilder(baseUrl)
        return stringBuilder.toString()
    }

}