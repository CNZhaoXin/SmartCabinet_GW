package com.zk.cabinet.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.widget.Toast
import java.lang.ref.WeakReference

class NetService : Service() {

    private fun handleMessage(receiveMessage: Message) {
        when (receiveMessage.what) {
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private class NetHandler(looper: Looper, netService: NetService) : Handler(looper) {
        private val mainWeakReference = WeakReference(netService)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mainWeakReference.get()!!.handleMessage(msg)
        }
    }
}
