package com.example.kzvdar42.deliveryoperatorapp.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import timber.log.Timber
import kotlin.concurrent.thread

class SendLocation : Service() {
    private val repository by lazy { Repository(application) }

    override fun onCreate() {
        Timber.i("Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("Service onStartCommand %s", startId)
        thread(true) {
            fun send() {
                repository.sendCurrentPosition()
                Thread.sleep(TIME_INTERVAL)
                send()
            }
            send()
        }
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Timber.i("Service onDestroy")
        super.onDestroy()
    }

    companion object {
        private const val TIME_INTERVAL: Long = 10000
    }

}