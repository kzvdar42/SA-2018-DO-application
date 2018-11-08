package com.example.kzvdar42.deliveryoperatorapp.util

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.kzvdar42.deliveryoperatorapp.db.Repository
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlin.concurrent.thread

class SendLocation : Service() {
    private lateinit var mService: GetLocation
    private var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as GetLocation.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private fun sendCurrentLocation() {
        val repository = Repository(application)

        if (mBound) {
            val currPos = LatLng(mService.getCurrentPosition())
            Log.d(TAG, "sendingCurrentLocation: ${currPos.latitude} ${currPos.longitude}")
            repository.sendPosition(currPos)
        } else {
            Log.d(TAG, "Tried to send position, but not yet binded.")
        }


    }

    override fun onCreate() {
        Log.i(TAG, "Service onCreate")
        Intent(this, GetLocation::class.java).also { intent ->
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service onStartCommand " + startId)
        thread(true) {
            fun send() {
                sendCurrentLocation()
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
    }

    companion object {
        private const val TAG = "SendLocationService"
        private const val TIME_INTERVAL:Long =  60000
    }

}