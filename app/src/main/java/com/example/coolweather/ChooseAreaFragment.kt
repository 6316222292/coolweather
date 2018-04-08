package com.example.coolweather

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.LoginFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.coolweather.db.City
import com.example.coolweather.db.County
import com.example.coolweather.db.Province
import com.example.coolweather.util.HttpUtil
import com.example.coolweather.util.Utility
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.choose_area.*
import org.litepal.crud.DataSupport
import java.io.IOException

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import kotlin.math.log

/**
 * Created by lenovo on 2018/3/30.
 */

class ChooseAreaFragment : Fragment() {
    lateinit var adapterItem:ArrayAdapter<String>
    private val dataList=ArrayList<String>()
    companion object {
        val LEVEL_PROVINCE=0
        val LEVEL_CITY=1
        val LEVEL_COUNTY=2
    }

    /**
     * 省列表
     */
    lateinit var provinceList:List<Province>
    /**
     * 市列表
     */
    lateinit var cityList:List<City>
    /**
     * 县列表
     */
    lateinit var countyList:List<County>
    lateinit var selectedProvince:Province
    lateinit var selectedCity:City
    var currentLeve:Int=0
    lateinit var listView:ListView
    lateinit var progressDialog:AlertDialog
    val URL="http://guolin.tech/api/china/"
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater!!.inflate(R.layout.choose_area, container, false)
        adapterItem=ArrayAdapter(context,android.R.layout.simple_list_item_1,dataList)
        listView=view.findViewById(R.id.listView)
        listView.adapter=adapterItem
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.setOnItemClickListener { parent, view, position, id ->
            if (currentLeve== LEVEL_PROVINCE){
                selectedProvince=provinceList[position]
                queryCities()
            }else if (currentLeve== LEVEL_CITY){
                selectedCity= cityList[position]
                queryCounties()
            }else if (currentLeve== LEVEL_COUNTY){
                val weatherId= countyList[position].weatherId
                if (activity is MainActivity){
                    val inte=Intent(activity,WeatherActivity::class.java)
                    inte.putExtra("weather_id",weatherId)
                    startActivity(inte)
                    activity.finish()
                }else if (activity is WeatherActivity){
                    val activityWeat=activity as WeatherActivity
                    activityWeat.drawerLayout.closeDrawers()
                    activityWeat.swipe_refresh.isRefreshing=false
                    activityWeat.requestWeather(weatherId)
                }

            }
        }
        backBtn.setOnClickListener {
            if (currentLeve== LEVEL_COUNTY){
                queryCities()
            }else if (currentLeve== LEVEL_CITY){
                queryProvinces()
            }
        }
        queryProvinces()
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private fun queryCounties() {
        title_text.text=selectedCity.cityName
        backBtn.visibility=View.VISIBLE
        countyList=DataSupport.where("cityid=?",selectedCity.id.toString()).find(County::class.java)
        if (countyList.isNotEmpty()){
            dataList.clear()
            for (county in countyList){
                dataList.add(county.countyName)
            }
            adapterItem.notifyDataSetChanged()
            listView.setSelection(0)
            currentLeve= LEVEL_COUNTY
        }else {
            val provinceCode=selectedProvince.provinceCode
            val cityCode=selectedCity.cityCode
            val address= "$URL$provinceCode/$cityCode"
            queryFromServer(address,"county")
        }

    }

    /**
     * 查询选择省内所有的市，优先从数据库查询，如果没有查询到再去服务器查询
     */
    private fun queryCities() {
        title_text.text=selectedProvince.provinceName
        backBtn.visibility=View.VISIBLE
        cityList=DataSupport.where("provinceid = ?", selectedProvince.id.toString()).find(City::class.java)
        if(cityList.isNotEmpty()){
           dataList.clear()
            for (city in cityList){
                dataList.add(city.cityName)
            }
            adapterItem.notifyDataSetChanged()
            listView.setSelection(0)
            currentLeve= LEVEL_CITY
        }else {
            val provinceCode=selectedProvince.provinceCode
            val address = URL+provinceCode
            queryFromServer(address,"city")
        }
    }

    /**
     * 根据选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private fun queryFromServer(address: String, type: String) {
        showProgressDialog()
        HttpUtil.sendOkHttpRequest(address, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity.runOnUiThread({
                    closeProgressDialog()
                    Toast.makeText(activity,"加载失败",Toast.LENGTH_SHORT).show()
                })
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseText=response.body().string()
                Log.e("att",responseText)
                closeProgressDialog()
                var result=false
                when {
                    "province" == type -> result=Utility.handleProvinceResponse(responseText)
                    "city"==(type) -> result=Utility.handleCityResponse(responseText,selectedProvince.id)
                    "county"==(type) -> result=Utility.handleCountyResponse(responseText,selectedCity.id)
                }
                if (result){
                    activity.runOnUiThread({

                        when {
                            "province" == type -> queryProvinces()
                            "city"==(type) -> queryCities()
                            "county"==(type) ->queryCounties()
                        }
                    })
                }
            }
        })

    }

    private fun queryProvinces() {
        title_text.text="中国"
        backBtn.visibility=View.GONE
        provinceList=DataSupport.findAll(Province::class.java)
        if (provinceList.isNotEmpty()){
            dataList.clear()
            for (province in provinceList){
                dataList.add(province.provinceName)
            }
            adapterItem.notifyDataSetChanged()
            listView.setSelection(0)
            currentLeve= LEVEL_PROVINCE
        }else{
            val address=URL
            queryFromServer(address,"province")
        }

    }

    private fun showProgressDialog() {
        progressDialog= AlertDialog.Builder(activity).create()
        progressDialog.setMessage("正在加载...")
        progressDialog.setCancelable(false)

        progressDialog.show()

    }
    fun closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss()
        }
    }
}
