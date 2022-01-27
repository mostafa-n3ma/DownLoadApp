package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat




    private val NOTIFICATION_ID=0

   fun NotificationManager.sendNotification(context: Context,intent: Intent){

        val pendingIntent=PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder=NotificationCompat.Builder(
            context,
            context.getString(R.string.channel_id)
        )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setAutoCancel(true)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0,context.getString(R.string.notification_button),pendingIntent)



        notify(NOTIFICATION_ID,builder.build())
    }


