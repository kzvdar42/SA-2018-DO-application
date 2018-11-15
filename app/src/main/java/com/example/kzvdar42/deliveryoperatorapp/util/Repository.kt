package com.example.kzvdar42.deliveryoperatorapp.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kzvdar42.deliveryoperatorapp.db.AppDatabase
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.serverApi.ServerApi
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.UpdateOrderReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.UpdatePositionReqBody
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.mapboxsdk.Mapbox
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import timber.log.Timber
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread


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
        //locEng.priority = LocationEnginePriority.BALANCED_POWER_ACCURACY
        locEng.activate()
        Timber.i("Activated location Engine")
        locEng
    }

    override fun onConnected() {
        Timber.i("Connected to observe new location.")
    }

    override fun onLocationChanged(newLocation: Location) {
        Timber.i("Location changed \n ${newLocation.longitude} ${newLocation.latitude} ")
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
    @SuppressLint("TimberExceptionLogging")
    fun getOrdersFromServer(): Disposable {
        return mServerApi.getOrders(getCredentials())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    thread(true) {
                        mOrderDao.deleteAll()
                        mOrderDao.insertAll(it)
                    }
                }, {
                    Timber.e(it.message)
                })
    }

    fun getOrders(): LiveData<List<OrderEntity>> {
        getOrdersFromServer()
        return mOrderDao.getAllOrders()
    }

    fun getOrder(orderNumber: Int): LiveData<OrderEntity> {
//        getOrdersFromServer()
        return mOrderDao.getOrder(orderNumber)
    }

    fun getNewOrders(): LiveData<List<OrderEntity>> {
        getOrdersFromServer()
        return mOrderDao.getNewOrders()
    }

    fun getAcceptedOrders(): LiveData<List<OrderEntity>> {
        getOrdersFromServer()
        return mOrderDao.getAcceptedOrders()
    }

    fun updateOrder(orderNum: Int, orderStatus: String, photo: Bitmap?): Pair<LiveData<String>, Disposable> {
        // Body for request
        val body = UpdateOrderReqBody(orderNum, encodeToBase64(photo))
        val result = MutableLiveData<String>()
        val disposable = mServerApi.updateOrder(getCredentials(), body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result.postValue(it.body()?.message)
                    doAsync { mOrderDao.updateOrder(orderNum, orderStatus) }
                }, {
                    Timber.e(it)
                    result.postValue(it.message)
                })
        if (orderStatus == "Delivered") orderPref.edit().remove("orderNum").apply()
        return Pair(result, disposable)
    }

    private fun encodeToBase64(image: Bitmap?): String? {
        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


    @SuppressLint("MissingPermission", "CheckResult")
    fun sendCurrentPosition(): LiveData<String> {
        val result = MutableLiveData<String>()
        val position = locationService.lastLocation
        if (position != null) {
            val body = UpdatePositionReqBody(position.latitude, position.longitude)
            Timber.i("Sending current position: ${position.longitude} ${position.latitude}")
            mServerApi.updatePosition(getCredentials(), body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        result.postValue(it.body()?.message)
                    }, {
                        result.postValue(it.message)
                    })
        } else {
            Timber.e("Unable to send current position, location engine returned null.")
        }
        return result
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): MutableLiveData<Location> {
        val lastLocation = locationService.lastLocation
        if (lastLocation != null) {
            originLocation.postValue(lastLocation)
            Timber.i("Last location in not null ${lastLocation.longitude} ${lastLocation.latitude} ")
        } else {
            Timber.i("Added listener")
            locationService.addLocationEngineListener(this)
        }
        return originLocation
    }

    @SuppressLint("MissingPermission")
    fun getLatestLocation(): Location? {
        return locationService.lastLocation
    }

}