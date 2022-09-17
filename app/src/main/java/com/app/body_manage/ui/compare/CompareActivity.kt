package com.app.body_manage.ui.compare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.photoList.PhotoListActivity

class CompareActivity : AppCompatActivity() {

    private val simpleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val bottomSheetDataList = createBottomDataList(
                calendarAction = { simpleLauncher.launch(CalendarActivity.createIntent(this)) },
                compareAction = { simpleLauncher.launch(createIntent(this)) },
                photoListAction = { simpleLauncher.launch(PhotoListActivity.createIntent(this)) },
                graphAction = { simpleLauncher.launch(GraphActivity.createIntent(this)) }
            )
            CompareScreen(bottomSheetDataList = bottomSheetDataList)
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, CompareActivity::class.java)
        }
    }
}