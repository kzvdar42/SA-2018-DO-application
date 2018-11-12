package com.example.kzvdar42.deliveryoperatorapp.serverApi

import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.UpdateOrderReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.UpdatePositionReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.responce.SimpleResponce
import com.example.kzvdar42.deliveryoperatorapp.util.Constants
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import com.google.gson.GsonBuilder
import com.google.gson.Gson




interface ServerApi {
    @POST("/login")
    fun login(@Body loginReqBody: LoginReqBody): Single<Response<SimpleResponce>>

    @GET("/orders")
    fun getOrders(
            @Header("Authorization") auth_key: String
    ): Observable<List<OrderEntity>>

    @POST("/update_order_status")
    fun updateOrder(
            @Header("Authorization") auth_key: String,
            @Body loginReqBody: UpdateOrderReqBody
    ): Single<Response<SimpleResponce>>

    @POST("/update_position")
    fun updatePosition(
            @Header("Authorization") auth_key: String,
            @Body loginReqBody: UpdatePositionReqBody
    ): Single<Response<SimpleResponce>>

    @GET("/logout")
    fun logout(): Call<SimpleResponce>

    companion object Factory {
        fun create(): ServerApi {

            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create()

            val retrofit = retrofit2.Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(Constants.BASE_URL)
                    .build()

            return retrofit.create(ServerApi::class.java)
        }
    }
}