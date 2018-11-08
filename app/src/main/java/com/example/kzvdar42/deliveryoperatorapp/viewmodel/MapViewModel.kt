package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import com.google.gson.Gson
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox


class MapViewModel(application: Application) : AndroidViewModel(application), LocationEngineListener {

    private var repository: Repository = Repository(application)
    private val sharedPref = application.getSharedPreferences("currentOrder", Context.MODE_PRIVATE)

    private val locationEngine: LocationEngine
            by lazy {
                val locEng = LocationEngineProvider(Mapbox.getApplicationContext()).obtainBestLocationEngineAvailable()
                locEng.priority = LocationEnginePriority.BALANCED_POWER_ACCURACY
                locEng.activate()
                locEng
            }
    private var originLocation: Location? = null


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

    @SuppressLint("MissingPermission")
    fun getCurrentPosition(): Location? {
        val lastLocation = locationEngine.lastLocation
        if (lastLocation != null) {
            originLocation = lastLocation
        } else {
            locationEngine.addLocationEngineListener(this)
        }
        return originLocation
    }


    override fun onConnected() {
    }

    override fun onLocationChanged(newLocation: Location?) {
        originLocation = newLocation
    }

}