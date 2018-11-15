package com.example.kzvdar42.deliveryoperatorapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface OrderDao {
    @Query("Select * from orders where status == \"Created\"")
    fun getNewOrders(): LiveData<List<OrderEntity>>

    @Query("Select * from orders where status == \"Approved\" or status == \"In Transit\" ")
    fun getAcceptedOrders(): LiveData<List<OrderEntity>>

    @Query("Select * from orders")
    fun getAllOrders(): LiveData<List<OrderEntity>>

    @Query("Select * from orders where order_num = :num")
    fun getOrder(num: Int): LiveData<OrderEntity>

    @Insert
    fun insert(orderEntity: OrderEntity)

    @Insert
    fun insertAll(orderEntities: List<OrderEntity>)

    @Query("UPDATE orders " +
            "SET status = :orderStatus WHERE order_num = :orderNum")
    fun updateOrder(orderNum: Int, orderStatus: String)

    @Delete
    fun delete(orderEntity: OrderEntity)

    @Query("Delete from orders")
    fun deleteAll()
}