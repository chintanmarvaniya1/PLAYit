package com.example.playit.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass:Application() {
    companion object{
        const val CHANNEL_ID = "Notification"
        const val PLAY = "Play"
        const val NEXT = "Next"
        const val PREV = "Prev"
        const val EXIT = "Exit"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notify = NotificationChannel(CHANNEL_ID,"Now Playing",NotificationManager.IMPORTANCE_HIGH)
            notify.description = "This channel showing notification of Song Playing"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notify)
        }
    }
}