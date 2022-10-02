package com.app.body_manage.ui.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.body_manage.R

class AlarmNotification : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0 ?: return
        val channelId = "NOTIFICATION_CHANNEL_ID_TRIAL"
        val builder = NotificationCompat.Builder(p0, channelId)
            // 1-2. 表示内容の設定
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("アラーム\uD83D\uDCAA")
            .setContentText("本日の体型を登録しましょう\uD83D\uDCAA")
            // 1-3. 優先度の設定
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        
        val name = "毎朝の通知"
        val descriptionText = "午前7時に通知します😊！"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        // 2-2. チャネルをシステムに登録
        val notificationManager: NotificationManager =
            p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        with(NotificationManagerCompat.from(p0)) {
            // notificationIDとbuilder.build()を渡します
            notify(12345, builder.build())
        }
    }
}
