package com.example.kzvdar42.deliveryoperatorapp.util

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.mapboxsdk.Mapbox


class GetLocation : Service(), LocationEngineListener {

    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): GetLocation = this@GetLocation
    }


    private val locationEngine by lazy {
        val locEng = LocationEngineProvider(Mapbox.getApplicationContext()).obtainBestLocationEngineAvailable()
        locEng.priority = LocationEnginePriority.BALANCED_POWER_ACCURACY
        locEng.activate()
        locEng
    }
    private var originLocation: Location? = null


    @SuppressLint("MissingPermission")
    fun getCurrentPosition(): Location? {
        val lastLocation = locationEngine.lastLocation
        if (lastLocation != null) {
            Log.e(TAG, "getCurrentPosition: ${lastLocation.latitude} ${lastLocation.longitude}")
            originLocation = lastLocation
        } else {
            Log.e(TAG, "addLocationEngineListener")
            locationEngine.addLocationEngineListener(this)
        }
        return originLocation
    }


    override fun onConnected() {
        Log.e(TAG, "Connected.")
    }

    override fun onLocationChanged(newLocation: Location?) {
        Log.e(TAG, "onLocationChanged: $newLocation")
        originLocation = newLocation
    }

    override fun onBind(arg0: Intent): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        getCurrentPosition()
        return Service.START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        locationEngine.deactivate()
    }

    companion object {
        private const val TAG = "GetLocationService"
    }
}