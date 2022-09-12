package com.app.body_manage.ui.setting

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.ui.alarm.AlarmNotification
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import java.util.Calendar

class SettingActivity : AppCompatActivity() {

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private lateinit var viewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = SettingViewModel(UserPreferenceRepository(this))

        val bottomSheetDataList = createBottomDataList(
            calendarAction = { launcher.launch(CalendarActivity.createIntent(this)) },
            photoListAction = { launcher.launch(PhotoListActivity.createIntent(this)) },
            graphAction = { launcher.launch(GraphActivity.createIntent(this)) }
        )

        setContent {
            val checked = viewModel.uiState.collectAsState()
            SettingScreen(checked, bottomSheetDataList) { on ->
                viewModel.updateAlarm(on)
                val alarmMgr =
                    baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent =
                    Intent(baseContext, AlarmNotification::class.java).let { intent ->
                        PendingIntent.getBroadcast(baseContext, 0, intent, 0)
                    }
                if (on) {
                    // 毎朝７時に通知する
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                        set(Calendar.HOUR_OF_DAY, 7)
                    }
                    alarmMgr.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        alarmIntent,
                    )
                } else {
                    alarmIntent.cancel()
                    alarmMgr.cancel(alarmIntent)
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