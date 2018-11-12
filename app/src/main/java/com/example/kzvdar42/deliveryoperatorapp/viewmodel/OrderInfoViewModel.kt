package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import io.reactivex.disposables.Disposable

class OrderInfoViewModel(application: Application) : AndroidViewModel(application) {


    private var repository: Repository = Repository(application)

    fun getOrder(orderNumber: Int): LiveData<OrderEntity> {
        return repository.getOrder(orderNumber)
    }

    fun updateOrder(orderNum: Int, orderStatus: String,
                    lastTransitPoint: Int, photo: Bitmap?): Pair<LiveData<String>, Disposable> {
        return repository.updateOrder(orderNum, orderStatus, lastTransitPoint, photo)
    }


    @SuppressLint("MissingPermission")
    fun getCurrentPosition(): MutableLiveData<Location> {
        return repository.getCurrentPosition()
    }

}