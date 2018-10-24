package com.example.kzvdar42.deliveryoperatorapp.db

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.kzvdar42.deliveryoperatorapp.serverApi.ServerApi
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.responce.LoginResponce


class Repository(application: Application) {
    private val sharedPref = application.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val appDatabase = AppDatabase.getInstance(application)
    private val mOrderDao = appDatabase.orderDao()
    private val mServerApi = ServerApi.create()

    private fun updateOrders() {
        mOrderDao.deleteAll()
        mOrderDao.insertAll(mServerApi.getOrders(sharedPref.getString("token","") ?: ""))
    }

    fun getOrder(orderNumber: Int): LiveData<OrderEntity> {
        updateOrders()
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

    fun login(requestBody: LoginReqBody): LoginResponce {
        return mServerApi.login(requestBody)
    }

}