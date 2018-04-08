package com.example.coolweather.gson

import com.google.gson.annotations.SerializedName

class Forecast(var date:String,@SerializedName("tmp") var temperature:Temperature,@SerializedName("cond") var more:More) {
    class Temperature(var max:String,var min:String)
    class More(@SerializedName("txt_d") var info:String)
}