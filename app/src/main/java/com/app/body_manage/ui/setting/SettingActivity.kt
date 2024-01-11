package com.app.body_manage.ui.setting

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.data.local.UserPreferenceRepository
import com.app.body_manage.ui.alarm.AlarmNotification
import com.app.body_manage.ui.compare.CompareActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import com.app.body_manage.ui.top.TopActivity
import java.util.Calendar

class SettingActivity : AppCompatActivity() {

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                setUpNotification()
            }
        }

    private val alarmIntent: PendingIntent
        get() {
            return Intent(baseContext, AlarmNotification::class.java).let { intent ->
                intent.setAction("com.app.body_manage.AlarmAction")
                PendingIntent.getBroadcast(
                    baseContext,
                    REQUEST_CODE_ALARM,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            }
        }

    private lateinit var viewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = SettingViewModel(UserPreferenceRepository(this))

        val bottomSheetDataList = createBottomDataList(
            context = this,
            topAction = { launcher.launch(TopActivity.createIntent(this)) },
            compareAction = { launcher.launch(CompareActivity.createIntent(this)) },
            photoListAction = { launcher.launch(PhotoListActivity.createIntent(this)) },
            graphAction = { launcher.launch(GraphActivity.createIntent(this)) }
        )

        setContent {
            val checked = viewModel.uiState.collectAsState()
            SettingScreen(checked, bottomSheetDataList) { on ->
                viewModel.updateAlarm(on)
                if (on) {
                    checkNotificationPrivilege()
                } else {
                    val receiver = ComponentName(baseContext, AlarmNotification::class.java)
                    val alarmMgr =
                        baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmIntent.cancel()
                    alarmMgr.cancel(alarmIntent)
                    baseContext.packageManager.setComponentEnabledSetting(
                        receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }
        }
    }

    private fun checkNotificationPrivilege() {
        if (Build.VERSION.SDK_INT >= 33) {
            permissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            setUpNotification()
        }
    }

    private fun setUpNotification() {
        val receiver = ComponentName(baseContext, AlarmNotification::class.java)
        val alarmMgr = baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent: PendingIntent =
            Intent(baseContext, AlarmNotification::class.java).let { intent ->
                intent.setAction("com.app.body_manage.AlarmAction")
                PendingIntent.getBroadcast(
                    baseContext,
                    REQUEST_CODE_ALARM,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            }
        // 毎朝７時に通知する
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        // 既に7時を超えている場合は翌日を開始時刻に設定する
        var startUpTime = calendar.timeInMillis
        if (System.currentTimeMillis() > startUpTime) {
            startUpTime += 24 * 60 * 60 * 1000
        }
        // 毎日7時に通知を実施する
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent,
        )

        baseContext.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    companion object {
        private const val REQUEST_CODE_ALARM = 123123

        fun createIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}
