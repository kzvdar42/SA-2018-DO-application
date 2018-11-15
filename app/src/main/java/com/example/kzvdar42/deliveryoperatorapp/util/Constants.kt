package com.example.kzvdar42.deliveryoperatorapp.util

import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import java.text.SimpleDateFormat
import java.util.*


class Constants {
    companion object {
        const val BASE_URL = "http://52.91.207.78:5000"

        // Lambda for adding right title for the marker.
        fun getTitle(ind: Int, maxIndex: Int): Int {
            return if (maxIndex < 2) R.string.order_title_end
            else when (ind) {
                0 -> R.string.order_title_from
                maxIndex - 1 -> R.string.order_title_end
                else -> R.string.order_title_transit
            }
        }

        fun offsetFromCurrTime(string: String): Long {
            val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US)
            val date = sdf.parse(string)
            val now = Date()
            return date.time - now.time
        }

        fun leftTime(string: String): Pair<Int, String> {
            val offset = offsetFromCurrTime(string)
            val days = ((offset / (1000 * 60 * 60 * 24)) % 365).toInt()
            val hours = ((offset / (1000 * 60 * 60)) % 24).toInt()
            val minutes = ((offset / (1000 * 60)) % 60).toInt()
            if (days > 0) return Pair(0, days.toString())
            if (hours > 0) return Pair(1, hours.toString())
            if (minutes > 0) return Pair(2, minutes.toString())
            return Pair(-1, "")
        }

        fun sortOrders(orders: List<OrderEntity>): List<OrderEntity> {
            return orders.filter { leftTime(it.expectedTtd).second != "" }.sortedWith(kotlin.Comparator
            { o1, o2 ->
                compareValues(offsetFromCurrTime(o1.expectedTtd),
                        offsetFromCurrTime(o2.expectedTtd))
            })
        }
    }
}