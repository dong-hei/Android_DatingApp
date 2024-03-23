package com.dk.dating_app.message.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dk.dating_app.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * 유저 토큰 정보를 받아서 Firebase 서버로 메세지 보내라고 명령 ->
 * Firebase 서버에서 앱으로 메세지 보내주고 ->
 * 앱에서는 메세지를 받아 알람을 띄워줌
 */
class FirebaseService : FirebaseMessagingService(){

    private val TAG = "FirebaseService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        val title = message.data["title"].toString()
        val body = message.data["content"].toString()

        createNotificationChannel()
        sendNotification(title, body)

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel( "Test_channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(title : String, body : String) {
        try {
            var builder = NotificationCompat.Builder(this, "Test_channel")
                .setSmallIcon(R.drawable.logo1)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            with(NotificationManagerCompat.from(this)){
                notify(123, builder.build())
            }
        } catch (e: SecurityException) {
        }
    }

}