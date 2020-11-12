package com.foryouandme.core.cases.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.foryouandme.core.activity.FYAMActivity
import com.foryouandme.core.arch.app.ForYouAndMeApp
import com.foryouandme.core.cases.auth.AuthUseCase.isLogged
import com.foryouandme.core.cases.auth.AuthUseCase.updateFirebaseToken
import com.foryouandme.core.ext.injector
import com.foryouandme.core.ext.mapNotNull
import com.foryouandme.core.ext.startCoroutineAsync
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class PushService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        val authModule = injector.authModule()

        startCoroutineAsync {

            if (authModule.isLogged())
                authModule.updateFirebaseToken(p0)

        }

    }

    override fun onMessageReceived(p0: RemoteMessage) {

        val smallIcon = (application as? ForYouAndMeApp)?.imageConfiguration?.pushSmallIcon()

        mapNotNull(
            p0.notification?.title,
            p0.notification?.body,
            smallIcon
        )
            ?.let { (title, body, icon) ->

                sendNotification(title, body, icon, p0.messageId ?: "0", p0.data)

            }

    }

    private fun sendNotification(
        title: String,
        message: String,
        icon: Int,
        id: String,
        args: Map<String, String>
    ): Unit {

        val intent =
            FYAMActivity.getIntent(this, args)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

        val notifyPendingIntent =
            PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )

        val channelId = "fyam_notification_${id}"
        val channelName = "fyam_notification_notification"

        val notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(notifyPendingIntent)


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel =
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )

            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setShowBadge(false)
            channel.vibrationPattern =
                longArrayOf(
                    100,
                    200,
                    300,
                    400,
                    500,
                    400,
                    300,
                    200,
                    400
                )

            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(0, notificationBuilder.build())

        } else {

            val notificationManager =
                NotificationManagerCompat.from(this)

            notificationManager.notify(0, notificationBuilder.build())

        }


    }

}