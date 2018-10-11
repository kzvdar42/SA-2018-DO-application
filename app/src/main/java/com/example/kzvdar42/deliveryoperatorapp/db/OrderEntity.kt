package com.example.kzvdar42.deliveryoperatorapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "orders")
data class OrderEntity(
        @ColumnInfo(name = "order_num")
        var OrderNum: Int,
        @ColumnInfo(name = "user_name")
        var Username: String,
        @ColumnInfo(name = "order_description")
        var OrderDescription: String,
        @ColumnInfo(name = "from_lat")
        var FromLat: Double,
        @ColumnInfo(name = "from_lng")
        var FromLng: Double,
        @ColumnInfo(name = "to_lat")
        var ToLat: Double,
        @ColumnInfo(name = "to_lng")
        var ToLng: Double) {
    @ColumnInfo(name = "order_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
