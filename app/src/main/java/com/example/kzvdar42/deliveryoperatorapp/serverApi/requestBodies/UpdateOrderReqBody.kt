package com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies

class UpdateOrderReqBody(
        val suborder_id:Int,
        val order_status: String,
        val ltp:Int,
        val photo:String?
)