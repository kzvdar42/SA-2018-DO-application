package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.db.Repository

class OrderInfoViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: Repository = Repository(application)

    fun getOrder(orderNumber : Int) :  LiveData<OrderEntity> {
        return repository.getOrder(orderNumber)
    }

}