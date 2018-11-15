package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kzvdar42.deliveryoperatorapp.util.Repository
import com.example.kzvdar42.deliveryoperatorapp.serverApi.requestBodies.LoginReqBody
import io.reactivex.disposables.Disposable

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)

    fun login(login: String, password: String): Pair<LiveData<String>, Disposable> {
        return repository.login(LoginReqBody(login, password))
    }
}