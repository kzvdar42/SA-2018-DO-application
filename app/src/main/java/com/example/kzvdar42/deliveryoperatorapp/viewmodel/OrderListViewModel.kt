package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.db.Repository

class OrderListViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: Repository = Repository(application)

    private var data: LiveData<List<OrderEntity>>? = null

    fun getOrders(): LiveData<List<OrderEntity>>? {
        if (data == null) {
            data = repository.getAllOrders()
        }
        return data
    }

}
