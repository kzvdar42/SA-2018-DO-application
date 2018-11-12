package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import com.google.gson.Gson
import com.mapbox.geojson.Point


class MapViewModel(application: Application) : AndroidViewModel(application) {

    private var repository= Repository(application)
    private val sharedPref = application.getSharedPreferences("currentOrder", Context.MODE_PRIVATE)

    fun getCurrentOrder(): LiveData<OrderEntity> {
        return repository.getOrder(sharedPref.getInt("orderNum", -1))
    }

    fun saveOrderStatus(points: ArrayList<Point>) {
        if (points.size > 0) { //TODO: Rewrite to write data to the database
            val coords = DoubleArray(points.size * 2)

            var i = -1
            for (point in points) {
                coords[++i] = point.longitude()
                coords[++i] = point.latitude()
            }

            sharedPref.edit().putString("coords", Gson().toJson(coords)).apply()
        } // TODO: Implement the part there's no more points
    }

    fun getCurrentPosition(): MutableLiveData<Location> {
        return repository.getCurrentPosition()
    }

}