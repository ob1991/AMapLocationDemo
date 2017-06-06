package com.amap.location.demo.DB;

import com.amap.api.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hp on 2017/6/6.
 */

public class mark implements Serializable {
    public Date date ;
    public LatLng mylatlng;
    public double temperature;
    public mark(Date date, LatLng mylatlng, double temperature) {
        this.date = date;
        this.mylatlng = mylatlng;
        this.temperature = temperature;
    }
    @Override
    public String toString() {
        return "mark{" +
                "date='" + date.toString() + '\'' +
                ", mylatlng=" + mylatlng.toString() +
                ", temperature=" + temperature +
                '}';
    }
}
