package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)

    fun login(login: String, password: String): LiveData<Pair<String, String>> {
        return repository.login(LoginReqBody(login, password))
    }
}