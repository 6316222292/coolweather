package com.example.coolweather.gson

import com.google.gson.annotations.SerializedName

class Now {
    @SerializedName("tmp")
    lateinit var temperature:String
    @SerializedName("cond")
    lateinit var more:More

    class More {
        @SerializedName("txt")
        lateinit var info:String
    }
}