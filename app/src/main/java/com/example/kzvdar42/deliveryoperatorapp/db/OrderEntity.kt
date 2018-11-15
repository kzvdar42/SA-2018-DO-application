package com.example.kzvdar42.deliveryoperatorapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import java.util.*


@Entity(tableName = "orders")
data class OrderEntity(
        @ColumnInfo(name = "coords")
        @SerializedName("coords")
        @TypeConverters(Converter::class)
        var coords: ArrayList<CoordsEntity>,
        @ColumnInfo(name = "expected_delivery_time")
        @SerializedName("expected_delivery_time")
        var expectedTtd: String,
        @ColumnInfo(name = "height")
        @SerializedName("height")
        var height: Double,
        @ColumnInfo(name = "length")
        @SerializedName("length")
        var length: Double,
        @ColumnInfo(name = "order_num")
        @SerializedName("order_num")
        @PrimaryKey(autoGenerate = false)
        var orderNum: Int,
        @ColumnInfo(name = "receiver_address")
        @SerializedName("receiver_address")
        var receiverAddress: String?,
        @ColumnInfo(name = "receiver_name")
        @SerializedName("receiver_name")
        var receiverName: String,
        @ColumnInfo(name = "receiver_surname")
        @SerializedName("receiver_surname")
        var receiverSurname: String,
        @ColumnInfo(name = "receiver_third_name")
        @SerializedName("receiver_third_name")
        var receiverThirdName: String?,
        @ColumnInfo(name = "receiver_phone_number")
        @SerializedName("receiver_phone_number")
        var receiverPhoneNumber: String,
        @ColumnInfo(name = "send_date")
        @SerializedName("send_date")
        var sendDate: String,
        @ColumnInfo(name = "sender_address")
        @SerializedName("sender_address")
        var senderAddress: String?,
        @ColumnInfo(name = "sender_name")
        @SerializedName("sender_name")
        var senderName: String?,
        @ColumnInfo(name = "sender_surname")
        @SerializedName("sender_surname")
        var senderSurname: String?,
        @ColumnInfo(name = "sender_phone_number")
        @SerializedName("sender_phone_number")
        var senderPhoneNumber: String?,
        @ColumnInfo(name = "weight")
        @SerializedName("weight")
        var weight: Double,
        @ColumnInfo(name = "width")
        @SerializedName("width")
        var width: Double,
        @ColumnInfo(name = "status")
        @SerializedName("status")
        var orderStatus: String,
        @ColumnInfo(name = "sender_notes")
        @SerializedName("sender_notes")
        var senderNotes: String?)


data class CoordsEntity(
        var lat: Double,
        var long: Double)
