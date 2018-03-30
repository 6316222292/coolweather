package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by lenovo on 2018/3/30.
 */

public class County extends DataSupport {
    private int id;
    /**
     * 县名字
     */
    private String countyName;
    /**
     * 县所对应的天气id
     */
    private String weatherId;
    /**
     * 县id
     */
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
