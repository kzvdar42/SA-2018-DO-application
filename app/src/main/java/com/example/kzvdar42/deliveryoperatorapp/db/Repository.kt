package com.example.kzvdar42.deliveryoperatorapp.db

import android.app.Application
import android.content.Context
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kzvdar42.deliveryoperatorapp.serverApi.ServerApi
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync


class Repository(application: Application) {
    private val userPref = application.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val orderPref = application.getSharedPreferences("currentOrder", Context.MODE_PRIVATE)
    private val appDatabase = AppDatabase.getInstance(application)
    private val mOrderDao = appDatabase.orderDao()
    private val mServerApi = ServerApi.create()

    fun updateOrders() {
        val credentials = (userPref.getString("token", "") ?: "") + ":"
        val credentialsEncoded = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val disposable = mServerApi.getOrders("Basic $credentialsEncoded")
                .subscribeOn(Schedulers.io())
                .subscribe({
                    mOrderDao.insertAll(it)
                }, {}) // TODO: Do something on error
    }

    fun getOrder(orderNumber: Int): LiveData<OrderEntity> {
        return mOrderDao.getOrder(orderNumber)
    }

    fun getNewOrders(): LiveData<List<OrderEntity>> {
        updateOrders()
        return mOrderDao.getNewOrders()
    }

    fun getAcceptedOrders(): LiveData<List<OrderEntity>> {
        updateOrders()
        return mOrderDao.getAcceptedOrders()
    }

    fun login(requestBody: LoginReqBody): LiveData<Pair<String, String>> {
        val result: MutableLiveData<Pair<String, String>> = MutableLiveData()
        val disposable = mServerApi.login(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result.postValue(Pair(it.headers().toString(), it.headers().get("Authentication")
                            ?: ""))
                }, {
                    result.postValue(Pair(it.message!!, ""))
                })
        return result
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

}