package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.db.CoordsEntity
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox.getApplicationContext


class MapViewModel(application: Application) : AndroidViewModel(application), LocationEngineListener, PermissionsListener {

    private var repository: Repository = Repository(application)
    private val sharedPref = application.getSharedPreferences("currentOrder", Context.MODE_PRIVATE)

    private var locationEngine: LocationEngine? = null
    private var originLocation: Location? = null


    fun getCurrentOrder(): LiveData<OrderEntity> {
        return repository.getOrder(sharedPref.getInt("orderNum", 0))
    }

    fun saveOrderStatus(points: ArrayList<Point>) {
        if (points.size > 0) { //TODO: Rewrite to write data to the database and server
            val coords = DoubleArray(points.size * 2)

            var i = -1
            for (point in points) {
                coords[++i] = point.longitude()
                coords[++i] = point.latitude()
            }

            sharedPref.edit().putString("coords", Gson().toJson(coords)).apply()
        } // TODO: Implement the part there's no more points
    }

    fun getCurrentPosition(): Location? {
        enableLocationPlugin()
        return originLocation
    }

    private fun enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getApplicationContext())) {
            initializeLocationEngine()
        } else {
            // TODO: Request the permission:
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine() {
        val locationEngineProvider = LocationEngineProvider(getApplicationContext())
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable()
        locationEngine!!.priority = LocationEnginePriority.BALANCED_POWER_ACCURACY
        locationEngine!!.activate()

        val lastLocation = locationEngine!!.lastLocation
        if (lastLocation != null) {
            originLocation = lastLocation
        } else {
            locationEngine!!.addLocationEngineListener(this)
        }
    }


    override fun onConnected() {
    }

    override fun onLocationChanged(location: Location?) {
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationPlugin()
        } else {
            Toast.makeText(getApplication(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
        }
    }

}