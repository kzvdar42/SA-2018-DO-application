package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import com.google.gson.Gson
import com.mapbox.geojson.Point
import io.reactivex.disposables.Disposable


class MapViewModel(application: Application) : AndroidViewModel(application) {

    private var repository= Repository(application)
    private val sharedPref = application.getSharedPreferences("currentOrder", Context.MODE_PRIVATE)

    fun getCurrentOrder(): LiveData<OrderEntity> {
        return repository.getOrder(sharedPref.getInt("orderNum", -1))
    }

    fun saveOrderStatus(orderNum: Int, orderStatus: String,
                        lastTransitPoint: Int, photo: Bitmap?): Pair<LiveData<String>, Disposable> {
        return repository.updateOrder(orderNum, orderStatus, lastTransitPoint, photo)
    }

    fun getCurrentLocation(): MutableLiveData<Location> {
        return repository.getCurrentLocation()
    }
    fun getLatestLocation(): Location? {
        return repository.getLatestLocation()
    }

}