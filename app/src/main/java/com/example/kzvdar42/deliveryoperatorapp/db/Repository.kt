package com.example.kzvdar42.deliveryoperatorapp.db

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kzvdar42.deliveryoperatorapp.serverApi.ServerApi
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.UpdateOrderReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.UpdatePositionReqBody
import com.mapbox.mapboxsdk.geometry.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync


class Repository(application: Application) {
    private val userPref = application.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val orderPref = application.getSharedPreferences("currentOrder", Context.MODE_PRIVATE)
    private val appDatabase = AppDatabase.getInstance(application)
    private val mOrderDao = appDatabase.orderDao()
    private val mServerApi = ServerApi.create()

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


    fun getOrders() {
        val disposable = mServerApi.getOrders(getCredentials())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    mOrderDao.insertAll(it)
                }, {}) // TODO: Do something on error
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

    fun sendPosition(position: LatLng): Pair<LiveData<String>, Disposable>  {
        val result = MutableLiveData<String>()
        val body = UpdatePositionReqBody(position.latitude, position.longitude)
        val disposable = mServerApi.updatePosition(getCredentials(), body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result.postValue(it.body()?.message)
                }, {
                    result.postValue(it.message)
                })
        return Pair(result, disposable)
    }


}