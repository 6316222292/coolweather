package com.example.coolweather.gson

import com.google.gson.annotations.SerializedName

class Basic {
    @SerializedName("city")
    lateinit var cityName:String
    @SerializedName("id")
    lateinit var weatherId:String
    lateinit var update:Update

    data class Update(@SerializedName("loc") var updateTime:String)
}