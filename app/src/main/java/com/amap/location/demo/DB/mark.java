package com.amap.location.demo.DB;

import com.amap.api.maps.model.LatLng;
import com.google.gson.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by hp on 2017/6/6.
 */

public class mark implements Serializable {
    public String date ;
    public LatLng mylatlng;
    public double temperature;

    public mark(String date, LatLng mylatlng, double temperature) {
        this.date = date;
        this.mylatlng = mylatlng;
        this.temperature = temperature;
    }
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);

    }
}
