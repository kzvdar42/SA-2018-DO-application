package com.example.kzvdar42.deliveryoperatorapp.serverApi

import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.responce.LoginResponce
import com.example.kzvdar42.deliveryoperatorapp.util.Constants
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ServerApi {
    @POST("login/")
    fun login(@Body loginReqBody: LoginReqBody): LoginResponce

    @GET("orders/")
    fun getOrders(
            @Header("Authorization") auth_key: String
    ): ArrayList<OrderEntity>

    companion object Factory {
        fun create(): ServerApi {
            val retrofit = retrofit2.Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .build()

            return retrofit.create(ServerApi::class.java)
        }
    }
}