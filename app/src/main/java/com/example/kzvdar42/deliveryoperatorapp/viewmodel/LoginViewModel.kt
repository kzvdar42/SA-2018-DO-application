package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPref = application.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val repository = Repository(application)

    fun isLogged() : Boolean {
        return sharedPref.getString("token", "") != ""
    }
    fun login(login: String, password: String): Boolean {
        val result = repository.login(LoginReqBody(login, password))
        sharedPref.edit().putString("token", result.token).apply()
        return result.token != ""
    }
}