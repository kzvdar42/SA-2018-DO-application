package com.example.kzvdar42.deliveryoperatorapp.db

import android.app.Application
import androidx.lifecycle.LiveData


class Repository(application: Application) {
    private var mMenuDao: OrderDao

    init {
        val db = AppDatabase.getDatabase(application)
        mMenuDao = db.orderDao()
    }

    fun getAllOrders(): LiveData<List<OrderEntity>> {
        return mMenuDao.getAllOrders()
    }

}