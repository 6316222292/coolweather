package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by lenovo on 2018/3/30.
 */

public class City extends DataSupport {
    private int id;
    /**
     * 市名字
     */
    private String cityName;
    /**
     * 市代号
     */
    private int cityCode;
    /**
     * 市所属省的id
     */
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
