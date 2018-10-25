package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import com.example.kzvdar42.deliveryoperatorapp.serverApi.responce.LoginResponce

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPref = application.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val repository = Repository(application)

    fun isLogged() : Boolean {
        return sharedPref.getString("token", "") != ""
    }
    fun login(login: String, password: String): LiveData<Pair<String, String>> {
        return repository.login(LoginReqBody(login, password))
    }
}