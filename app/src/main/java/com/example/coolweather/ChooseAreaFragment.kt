package com.example.coolweather

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.coolweather.db.City
import com.example.coolweather.db.County
import com.example.coolweather.db.Province
import kotlinx.android.synthetic.main.choose_area.*

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
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater!!.inflate(R.layout.choose_area, container, false)
        adapterItem=ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,dataList)
        listView.adapter=adapterItem
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.setOnItemClickListener { parent, view, position, id ->
            if (currentLeve== LEVEL_PROVINCE){
                selectedProvince=provinceList[position]

            }else if (currentLeve== LEVEL_CITY){
                selectedCity= cityList[position]
            }
        }
        backBtn.setOnClickListener {
            if (currentLeve== LEVEL_COUNTY){

            }else if (currentLeve== LEVEL_CITY){

            }
        }

    }
}
