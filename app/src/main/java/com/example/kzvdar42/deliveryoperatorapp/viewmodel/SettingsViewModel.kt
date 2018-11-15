package com.example.kzvdar42.deliveryoperatorapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kzvdar42.deliveryoperatorapp.util.Repository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: Repository = Repository(application)

    fun logout() {
        repository.logout()
    }
}