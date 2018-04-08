package com.example.coolweather.gson

import com.google.gson.annotations.SerializedName

class Suggestion {
    @SerializedName("comf")
    lateinit var comfort:Comfort
    @SerializedName("cw")
    lateinit var carWash:CarWash
    lateinit var sport:Sport

    class Sport {
        @SerializedName("txt")
        lateinit var info:String
    }

    class CarWash {
        @SerializedName("txt")
        lateinit var info:String
    }

    class Comfort {
        @SerializedName("txt")
        lateinit var info:String
    }
}