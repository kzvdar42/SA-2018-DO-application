package com.example.kzvdar42.deliveryoperatorapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface OrderDao {
    @Query("Select * from orders where is_new == 1")
    fun getNewOrders(): LiveData<List<OrderEntity>>

    @Query("Select * from orders where is_new == 0")
    fun getAcceptedOrders(): LiveData<List<OrderEntity>>

    @Query("Select * from orders where order_num = :num")
    fun getOrder(num : Int) : LiveData<OrderEntity>

    @Insert
    fun insert(orderEntity: OrderEntity)

    @Insert
    fun insertAll(orderEntities: List<OrderEntity>)

    @Delete
    fun delete(superHero: OrderEntity)
}