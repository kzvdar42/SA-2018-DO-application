package com.example.kzvdar42.deliveryoperatorapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "orders")
data class OrderEntity(
        @ColumnInfo(name = "order_num")
        var orderNum: Int,
        @ColumnInfo(name = "is_new")
        var isNew: Boolean,
        @ColumnInfo(name = "user_name")
        var username: String,
        @ColumnInfo(name = "order_description")
        var orderDescription: String,
        @ColumnInfo(name = "phone_number")
        var phoneNumber: String,
        @ColumnInfo(name = "weight")
        var weight: Double,
        @ColumnInfo(name = "length")
        var length: Double,
        @ColumnInfo(name = "width")
        var width: Double,
        @ColumnInfo(name = "height")
        var height: Double,
        @ColumnInfo(name = "expected_ttd")
        var expectedTtd: String,
        @ColumnInfo(name = "from_lat")
        var fromLat: Double,
        @ColumnInfo(name = "from_lng")
        var fromLng: Double,
        @ColumnInfo(name = "to_lat")
        var toLat: Double,
        @ColumnInfo(name = "to_lng")
        var toLng: Double) {
    @ColumnInfo(name = "order_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
