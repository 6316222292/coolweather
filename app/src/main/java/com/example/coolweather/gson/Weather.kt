package com.example.coolweather.gson

import com.google.gson.annotations.SerializedName

data class Weather(val status:String,val basic: Basic,val aqi: AQI,val now: Now,val suggestion: Suggestion,@SerializedName("daily_forecast") val forecastList: List<Forecast>)