package com.app.body_manage.ui.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.body_manage.R

class AlarmNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val builder = NotificationCompat.Builder(context, channelId)
            // 1-2. 表示内容の設定
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(context.resources.getString(R.string.app_name))
            .setContentText("本日の体型を登録しましょう\uD83D\uDCAA")
            // 1-3. 優先度の設定
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val name = "毎朝の通知"
        val descriptionText = "午前7時に体型登録を通知します"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        // 2-2. チャネルをシステムに登録
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        with(NotificationManagerCompat.from(context)) {
            // notificationIDとbuilder.build()を渡します
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }
    }

    companion object {
        private const val channelId = "NOTIFICATION_CHANNEL_ID_TRIAL"
        private const val notificationId = 12345
    }
}
