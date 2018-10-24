package com.example.kzvdar42.deliveryoperatorapp.db

import androidx.room.*
import java.sql.Date


//Order:
//order_num : Int, PrimaryKey,

@Entity(tableName = "orders")
data class OrderEntity(
        @ColumnInfo(name = "order_num")
        @PrimaryKey(autoGenerate = false)
        var orderNum: Int,
        @ColumnInfo(name = "sender_name")
        var senderName: String,
        @ColumnInfo(name = "sender_surname")
        var senderSurname: String,
        @ColumnInfo(name = "sender_phone_number")
        var senderPhoneNumber : String,
        @ColumnInfo(name = "receiver_name")
        var receiverName: String,
        @ColumnInfo(name = "receiver_surname")
        var receiverSurname: String,
        @ColumnInfo(name = "receiver_third_name")
        var receiverThirdName: String,
        @ColumnInfo(name = "receiver_phone_number")
        var receiverPhoneNumber: String,
        // FIXME
        @ColumnInfo(name = "receiver_address")
        var receiverAddress: String,
        @ColumnInfo(name = "send_date")
        var sendDate: String,
        @ColumnInfo(name = "sender_address")
        var senderAddress: String,
        // FIXME
        @ColumnInfo(name = "weight")
        var weight: Double,
        @ColumnInfo(name = "length")
        var length: Double,
        @ColumnInfo(name = "width")
        var width: Double,
        @ColumnInfo(name = "height")
        var height: Double,
        @ColumnInfo(name = "insurance")
        var insurance: Boolean,
        @ColumnInfo(name = "price")
        var price: Double,
        @ColumnInfo(name = "price_Currency")
        var priceCurrency: String,
        @ColumnInfo(name = "last_transit_point")
        var lastTransitPoint: String, //FIXME change
        @ColumnInfo(name = "order_status")
        var orderStatus: String,
        @ColumnInfo(name = "coords")
        @TypeConverters(Converter::class)
        var coords: ArrayList<CoordsEntity>,
        @ColumnInfo(name = "expected_ttd")
        var expectedTtd: String,
        @ColumnInfo(name = "sender_notes")
        var senderNotes: String)


data class CoordsEntity(
        var latitude : Double,
        var longitude : Double)
