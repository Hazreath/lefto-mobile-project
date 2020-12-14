package com.example.lefto.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.lefto.R
import com.example.lefto.utils.GeneralUtils
import com.example.lefto.view.ClientActivity

class FirebaseInstance : FirebaseMessagingService() {
    val TAG = "FirebaseCloudMessaging"

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        handleMessage(p0)
    }

    // Firebase Cloud Message sends a msg everyday at 19h EEST
    private fun handleMessage(remoteMessage: RemoteMessage) {
        val handler = Handler(Looper.getMainLooper())

        Log.d("Firebase","NOTIF")
        handler.post(Runnable {
//            GeneralUtils.showToast(ClientActivity,"aled")
            Toast.makeText(baseContext,"Notification received from app!",
                Toast.LENGTH_LONG).show()
        })
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "Refreshed token: $p0")
        //sendRegistrationToServer(p0)
    }
}