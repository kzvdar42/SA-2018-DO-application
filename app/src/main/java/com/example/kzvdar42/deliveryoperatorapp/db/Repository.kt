package com.example.kzvdar42.deliveryoperatorapp.db

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.serverApi.ServerApi
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.UpdateOrderReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.UpdatePositionReqBody
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.mapboxsdk.Mapbox
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync


class Repository(application: Application) : LocationEngineListener {
    private val userPref = application.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val orderPref = application.getSharedPreferences("currentOrder", Context.MODE_PRIVATE)
    private val appDatabase = AppDatabase.getInstance(application)
    private val mOrderDao = appDatabase.orderDao()
    private val mServerApi = ServerApi.create()
    private val originLocation = MutableLiveData<Location>()
    private val locationService by lazy {
//        Mapbox.getInstance(application, application.getString(R.string.access_token))
        val locEng = LocationEngineProvider(Mapbox.getApplicationContext()).obtainBestLocationEngineAvailable()
        locEng.priority = LocationEnginePriority.HIGH_ACCURACY
        locEng.activate()
        Log.e(TAG, "Activated location Engine")
        locEng
    }

    @SuppressLint("MissingPermission")
    fun getCurrentPosition(): MutableLiveData<Location> {
        val lastLocation = locationService.lastLocation
        if (lastLocation != null) {
            originLocation.postValue(lastLocation)
            Log.e(TAG, "Last location in not null ${lastLocation.longitude} ${lastLocation.latitude} ")
        } else {
            Log.e(TAG, "Added listener")
            locationService.addLocationEngineListener(this)
        }
        return originLocation
    }

    override fun onConnected() {
        Log.e(TAG, "Connected to observe new location.")
    }

    override fun onLocationChanged(newLocation: Location) {
        Log.e(TAG, "Location changed \n ${newLocation.longitude} ${newLocation.latitude} ")
        originLocation.postValue(newLocation)
    }

    private fun getCredentials(): String {
        val credentials = (userPref.getString("token", "") ?: "") + ":"
        val credentialsEncoded = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        return "Basic $credentialsEncoded"
    }

    fun login(requestBody: LoginReqBody): Pair<LiveData<String>, Disposable> {
        val result = MutableLiveData<String>()
        val disposable = mServerApi.login(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    userPref.edit().putString("token", response.headers().get("Authentication")
                            ?: "").apply()
                    result.postValue("OK")
                }, { responce ->
                    result.postValue(responce.message)
                })
        return Pair(result, disposable)
    }

    fun logout() {
        // Delete the user data
        doAsync {
            mOrderDao.deleteAll()
        }
        mServerApi.logout()
        orderPref.edit().remove("orderNum").apply()
        userPref.edit().remove("token").apply()
    }

    /**
     * Updates the orders database with the info from server.
     * @return: Disposable of request.
     */
    fun getOrders(): Disposable {
        return mServerApi.getOrders(getCredentials())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    mOrderDao.insertAll(it)
                }, {
                    Log.e(TAG, it.message)
                })
    }

    fun getOrder(orderNumber: Int): LiveData<OrderEntity> {
        return mOrderDao.getOrder(orderNumber)
    }

    fun getNewOrders(): LiveData<List<OrderEntity>> {
        getOrders()
        return mOrderDao.getNewOrders()
    }

    fun getAcceptedOrders(): LiveData<List<OrderEntity>> {
        getOrders()
        return mOrderDao.getAcceptedOrders()
    }

    fun updateOrder(orderNum: Int, orderStatus: String,
                    lastTransitPoint: Int, photo: Bitmap?): Pair<LiveData<String>, Disposable> {
        // Body for request
        val body = UpdateOrderReqBody(orderNum, orderStatus, lastTransitPoint, null) // TODO: Send photo.
        val result = MutableLiveData<String>()
        val disposable = mServerApi.updateOrder(getCredentials(), body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result.postValue(it.body()?.message)
                    doAsync { mOrderDao.updateOrder(orderNum, orderStatus, lastTransitPoint) }
                }, {
                    result.postValue(it.message)
                })
        return Pair(result, disposable)
    }

    @SuppressLint("MissingPermission")
    fun sendCurrentPosition(): LiveData<String> {
        val result = MutableLiveData<String>()
        val position = locationService.lastLocation
        if (position != null) {
            val body = UpdatePositionReqBody(position.latitude, position.longitude)
            Log.e(TAG, "Sending current position: ${position.longitude} ${position.latitude}")
            val disposable = mServerApi.updatePosition(getCredentials(), body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        result.postValue(it.body()?.message)
                    }, {
                        result.postValue(it.message)
                    })
        } else {
            Log.e(TAG, "Unable to send current position, no latest position found.")
        }
        return result
    }

    companion object Factory {
        private const val TAG = "Repository"
    }

}