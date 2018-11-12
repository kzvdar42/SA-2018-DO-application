package com.example.kzvdar42.deliveryoperatorapp.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import kotlin.concurrent.thread

class SendLocation : Service() {
    private val repository by lazy { Repository(application) }

    override fun onCreate() {
        Log.i(TAG, "Service onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service onStartCommand " + startId)
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
        Log.i(TAG, "Service onBind")
        return null
    }

    override fun onDestroy() {
        Log.i(TAG, "Service onDestroy")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "SendLocationService"
        private const val TIME_INTERVAL: Long = 10000
    }

}