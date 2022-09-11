package com.app.body_manage.ui.setting

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.body_manage.R
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.photoList.PhotoListActivity

class SettingActivity : AppCompatActivity() {


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bottomSheetDataList = createBottomDataList(
            calendarAction = { launcher.launch(CalendarActivity.createIntent(this)) },
            photoListAction = { launcher.launch(PhotoListActivity.createIntent(this)) },
            graphAction = { launcher.launch(GraphActivity.createIntent(this)) }
        )

        setContent {
            val checked = rememberSaveable { mutableStateOf(true) }
            SettingScreen(checked, bottomSheetDataList) {
                checked.value = it
                if (it) {
                    val channelId = "NOTIFICATION_CHANNEL_ID_TRIAL"
                    val builder = NotificationCompat.Builder(this, channelId)
                        // 1-2. 表示内容の設定
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("通知のTitle🍩")
                        .setContentText("通知のText🍮通知ですよ〜")
                        // 1-3. 優先度の設定
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)


                    val name = "お試し通知"
                    val descriptionText = "お試しで通知を送るための通知チャンネルです😊！"
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(channelId, name, importance).apply {
                        description = descriptionText
                    }
                    // 2-2. チャネルをシステムに登録
                    val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)

                    with(NotificationManagerCompat.from(this)) {
                        // notificationIDとbuilder.build()を渡します
                        notify(12345, builder.build())
                    }
                }
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}