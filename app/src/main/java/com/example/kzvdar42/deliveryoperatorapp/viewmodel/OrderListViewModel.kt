package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.db.Repository

class OrderListViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: Repository = Repository(application)

    private var acceptedOrders: LiveData<List<OrderEntity>>? = null
    private var newOrders: LiveData<List<OrderEntity>>? = null

    fun getAcceptedOrders(): LiveData<List<OrderEntity>>? {
        if (acceptedOrders == null) {
            acceptedOrders = repository.getAcceptedOrders()
        }
        return acceptedOrders
    }

    fun getNewOrders(): LiveData<List<OrderEntity>>? {
        if (newOrders == null) {
            newOrders = repository.getNewOrders()
        }
        return newOrders
    }

    fun updateOrders() {
        repository.updateOrders()
    }

}
