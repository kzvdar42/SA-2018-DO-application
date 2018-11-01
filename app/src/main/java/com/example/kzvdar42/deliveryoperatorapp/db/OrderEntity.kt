package com.example.kzvdar42.deliveryoperatorapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName


@Entity(tableName = "orders")
data class OrderEntity(
        @ColumnInfo(name = "order_num")
        @SerializedName("order_num")
        @PrimaryKey(autoGenerate = false)
        var orderNum: Int,
        @ColumnInfo(name = "sender_name")
        @SerializedName("sender_name")
        var senderName: String?,
        @ColumnInfo(name = "sender_surname")
        @SerializedName("sender_surname")
        var senderSurname: String?,
        @ColumnInfo(name = "sender_phone_number")
        @SerializedName("sender_phone_number")
        var senderPhoneNumber: String?,
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
        // FIXME
        @ColumnInfo(name = "receiver_address")
        @SerializedName("receiver_address")
        var receiverAddress: String,
        @ColumnInfo(name = "send_date")
        @SerializedName("send_date")
        var sendDate: String,
        @ColumnInfo(name = "sender_address")
        @SerializedName("sender_address")
        var senderAddress: String,
        // FIXME
        @ColumnInfo(name = "weight")
        @SerializedName("weight")
        var weight: Long,
        @ColumnInfo(name = "length")
        @SerializedName("length")
        var length: Long,
        @ColumnInfo(name = "width")
        @SerializedName("width")
        var width: Long,
        @ColumnInfo(name = "height")
        @SerializedName("height")
        var height: Long,
        @ColumnInfo(name = "insurance")
        @SerializedName("insurance")
        var insurance: Boolean,
        @ColumnInfo(name = "price")
        @SerializedName("price")
        var price: Long,
        @ColumnInfo(name = "price_currency")
        @SerializedName("price_currency")
        var priceCurrency: String,
        @ColumnInfo(name = "last_transit_point")
        @SerializedName("last_transit_point")
        var lastTransitPoint: String?, //FIXME change
        @ColumnInfo(name = "order_status")
        @SerializedName("order_status")
        var orderStatus: String,
        @ColumnInfo(name = "coords")
        @SerializedName("coords")
        @TypeConverters(Converter::class)
        var coords: ArrayList<CoordsEntity>,
        @ColumnInfo(name = "expected_ttd")
        @SerializedName("expected_ttd")
        var expectedTtd: String,
        @ColumnInfo(name = "sender_notes")
        @SerializedName("sender_notes")
        var senderNotes: String?)


data class CoordsEntity(
        var latitude: Double,
        var longitude: Double)
