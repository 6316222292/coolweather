package com.example.coolweather.gson

class AQI{
    lateinit var city:AQICity
    class AQICity {
        lateinit var aqi:String
        lateinit var pm25:String
    }
}


