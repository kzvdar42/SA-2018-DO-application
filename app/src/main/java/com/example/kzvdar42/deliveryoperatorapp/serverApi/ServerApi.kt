package com.example.kzvdar42.deliveryoperatorapp.serverApi

import com.example.kzvdar42.deliveryoperatorapp.db.OrderEntity
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.responce.LoginResponce
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


interface ServerApi {
    @POST("/login")
    fun login(@Body loginReqBody: LoginReqBody): Single<Response<LoginResponce>>

    @GET("/orders")
    fun getOrders(
            @Header("Authorization") auth_key: String
    ): Observable<List<OrderEntity>>

    @GET("/logout")
    fun logout(): Call<LoginResponce>

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