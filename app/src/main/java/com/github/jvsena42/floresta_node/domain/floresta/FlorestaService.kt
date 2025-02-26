package com.github.jvsena42.floresta_node.domain.floresta

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.jvm.java

class FlorestaService : Service() {
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val florestaDaemon: FlorestaDaemon by inject()

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
        // Your service logic here...
    }

    private fun createNotification(): Notification {
        val channelId = "floresta_service_channel"
        val channelName = "Floresta Service"

        // Create the NotificationChannel (for Android 8.0 and above)
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_MIN
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Floresta Service")
            .setContentText("Service is running in the background")
//            .setSmallIcon(R.drawable.notification_icon) // Replace with your notification icon
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setAutoCancel(false) // Prevent auto-dismissal
            .setOngoing(true) // Indicate ongoing service

        return notificationBuilder.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        try {
            ioScope.launch {
                Log.d(TAG, "onStartCommand: ")
                florestaDaemon.start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "onStartCommand error: ", e)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope.launch {
            Log.d(TAG, "onDestroy: ")
            florestaDaemon.stop()
        }
        ioScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "FlorestaService"
        private const val NOTIFICATION_ID = 1000
    }
}