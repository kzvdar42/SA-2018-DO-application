package com.example.kzvdar42.deliveryoperatorapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kzvdar42.deliveryoperatorapp.DB.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.DB.Repository

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
