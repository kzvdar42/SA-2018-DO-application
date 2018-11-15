package com.example.kzvdar42.deliveryoperatorapp.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber
import kotlin.concurrent.thread

class SendLocation : Service() {
    private val repository by lazy { Repository(application) }

    override fun onCreate() {
        Timber.i("Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("Service onStartCommand %s", startId)
        // Start sending current location, only if thread with this action still not created.
        if (runningTread == null) {
            Timber.i("Started thread")
            runningTread = thread(true) {
                fun send() {
                    repository.sendCurrentPosition()
                    Thread.sleep(TIME_INTERVAL)
                    send()
                }
                send()
            }
        }
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Timber.i("Service onDestroy")
        runningTread?.interrupt()
        runningTread = null
        super.onDestroy()
    }

    companion object {
        private const val TIME_INTERVAL: Long = 20000
        private var runningTread: Thread? = null
    }

}