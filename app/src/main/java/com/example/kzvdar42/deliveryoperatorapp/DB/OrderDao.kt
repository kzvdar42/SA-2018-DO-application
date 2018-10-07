package com.example.kzvdar42.deliveryoperatorapp.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface OrderDao {
    @Query("Select * from orders")
    fun getAllOrders(): LiveData<List<OrderEntity>>

    @Insert
    fun insert(orderEntity: OrderEntity)

    @Insert
    fun insertAll(orderEntities: List<OrderEntity>)

    @Delete
    fun delete(superHero: OrderEntity)
}