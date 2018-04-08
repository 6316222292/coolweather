package com.example.coolweather

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.coolweather.gson.Weather
import com.example.coolweather.service.AutoUpdateService
import com.example.coolweather.util.HttpUtil
import com.example.coolweather.util.Utility
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.aqi.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.forecast_item.*
import kotlinx.android.synthetic.main.now.*
import kotlinx.android.synthetic.main.suggestion.*
import kotlinx.android.synthetic.main.title.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class WeatherActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        if (Build.VERSION.SDK_INT>=21){
            window.decorView.systemUiVisibility=(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.statusBarColor= Color.TRANSPARENT
        }
        navBtn.setOnClickListener(this)
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary)
        val prefs=PreferenceManager.getDefaultSharedPreferences(this)
        val weatherString =prefs.getString("weather",null)
        var weatherId:String?
        if (weatherString!=null){
            val weather=Utility.handleWeatherResponse(weatherString)
            weatherId=weather.basic.weatherId
            showWeatherInfo(weather)
        }else{
            weatherId=intent.getStringExtra("weather_id")
            weather_layout.visibility= View.INVISIBLE
            requestWeather(weatherId)
        }
        val bingPic=intent.getStringExtra("bing_pic")
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(bing_pic_img)
        }else{
            loadBingPic()
        }
        swipe_refresh.setOnRefreshListener {
            requestWeather(weatherId)
        }
    }

    private fun loadBingPic() {
        val requestBingPic="http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(requestBingPic,object:Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                e!!.printStackTrace()
            }

            override fun onResponse(call: Call?, response: Response?) {
                val bingPic=response?.body()?.string()
                val editor=PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                editor.putString("bing_pic",bingPic)
                editor.apply()
                runOnUiThread({
                    Glide.with(this@WeatherActivity).load(bingPic).into(bing_pic_img)
                })
            }

        })
    }

    /**
     * 根据天气id请求城市天气信息
     */
    fun requestWeather(weatherId: String?) {
        loadBingPic()
        val weatherUrl= "http://guolin.tech/api/weather?cityid=$weatherId&key=8dc7d87967384263830fc9cce854451c"
        HttpUtil.sendOkHttpRequest(weatherUrl,object :Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                e!!.printStackTrace()
                runOnUiThread ({
                    swipe_refresh.isRefreshing=false
                    Toast.makeText(this@WeatherActivity,"获取天气失败",Toast.LENGTH_SHORT).show()
                })
            }

            override fun onResponse(call: Call?, response: Response?) {
                val responseText=response?.body()?.string()

                val weather=Utility.handleWeatherResponse(responseText)
                runOnUiThread({
                    swipe_refresh.isRefreshing=false
                    if (weather!=null&& "ok" == weather.status){
                        val editor=PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                        editor.putString("weather",responseText)
                        editor.apply()

                        showWeatherInfo(weather)
                    }else{
                        Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show()
                    }
                })
            }

        })
    }

    /**
     * 处理并展示weather实体类中的数据
     */
    private fun showWeatherInfo(weather: Weather) {
        startService(Intent(this,AutoUpdateService::class.java))
        title_city.text=weather.basic.cityName
        title_update_time.text=weather.basic.update.updateTime.split(" ")[1]
        degree_text.text=weather.now.temperature+"℃"
        weather_info_text.text=weather.now.more.info
        forecast_layout.removeAllViews()
        for (foreacst in weather.forecastList){
            val view=LayoutInflater.from(this).inflate(R.layout.forecast_item,forecast_layout,false)
            val dataText=view.findViewById<TextView>(R.id.date_text)
            val infoText=view.findViewById<TextView>(R.id.info_text)
            val maxText=view.findViewById<TextView>(R.id.max_text)
            val minText=view.findViewById<TextView>(R.id.min_text)
            dataText.text=foreacst.date
            infoText.text=foreacst.more.info
            maxText.text=foreacst.temperature.max
            minText.text=foreacst.temperature.min
            forecast_layout.addView(view)
        }
        if (weather.aqi!=null){
            aqi_text.text=weather.aqi.city.aqi
            pm25_text.text=weather.aqi.city.pm25
        }
        comfort_text.text="舒适度："+weather.suggestion.comfort.info
        car_wash_text.text="洗车指数："+weather.suggestion.carWash.info
        sport_text.text="运动建议:"+weather.suggestion.sport.info
        weather_layout.visibility=View.VISIBLE
    }
}
