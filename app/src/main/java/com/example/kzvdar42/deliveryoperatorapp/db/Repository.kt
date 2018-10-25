package com.example.kzvdar42.deliveryoperatorapp.db

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kzvdar42.deliveryoperatorapp.serverApi.ServerApi
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync


class Repository(application: Application) {
    private val sharedPref = application.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val appDatabase = AppDatabase.getInstance(application)
    private val mOrderDao = appDatabase.orderDao()
    private val mServerApi = ServerApi.create()

    private fun updateOrders() {
        doAsync {
            mOrderDao.deleteAll()
            val result = mServerApi.getOrders(sharedPref.getString("token", "") ?: "").execute()
            if (result.isSuccessful) {
                mOrderDao.insertAll(result.body()!!)
            }
        } //FIXME: ad hoc solution
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

    fun login(requestBody: LoginReqBody): LiveData<Pair<String, String>> {
        val result: MutableLiveData<Pair<String, String>> = MutableLiveData()
        val rrr = mServerApi.login(requestBody)
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

}