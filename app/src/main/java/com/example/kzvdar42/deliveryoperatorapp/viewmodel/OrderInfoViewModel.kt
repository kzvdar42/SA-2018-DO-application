package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.mapboxsdk.Mapbox
import io.reactivex.disposables.Disposable

class OrderInfoViewModel(application: Application) : AndroidViewModel(application), LocationEngineListener {


    private var repository: Repository = Repository(application)
    private val locationEngine: LocationEngine
            by lazy {
                val locEng = LocationEngineProvider(Mapbox.getApplicationContext()).obtainBestLocationEngineAvailable()
                locEng.priority = LocationEnginePriority.BALANCED_POWER_ACCURACY
                locEng.activate()
                locEng
            }
    private var latestLocation: Location? = null

    fun getOrder(orderNumber: Int): LiveData<OrderEntity> {
        return repository.getOrder(orderNumber)
    }

    fun updateOrder(orderNum: Int, orderStatus: String,
                    lastTransitPoint: Int, photo: Bitmap?): Pair<LiveData<String>, Disposable> {
        return repository.updateOrder(orderNum, orderStatus, lastTransitPoint, photo)
    }


    @SuppressLint("MissingPermission")
    fun getCurrentPosition(): Location {
        val lastLocation = locationEngine.lastLocation
        if (lastLocation != null) {
            return lastLocation
        } else {
            locationEngine.addLocationEngineListener(this)
        }
        return lastLocation
    }

    override fun onLocationChanged(newLocation: Location) {
        latestLocation = newLocation
    }

    override fun onConnected() {
    }

}