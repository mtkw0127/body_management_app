package com.app.body_manage.ui.top

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.app.body_manage.common.createBottomDataList
import com.app.body_manage.ui.calendar.CalendarActivity
import com.app.body_manage.ui.compare.CompareActivity
import com.app.body_manage.ui.graph.GraphActivity
import com.app.body_manage.ui.measure.form.MeasureFormActivity
import com.app.body_manage.ui.photoList.PhotoListActivity
import java.time.LocalDate

class TopActivity : AppCompatActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback {}
        val bottomSheetDataList = createBottomDataList(
            topAction = { },
            compareAction = { launcher.launch(CompareActivity.createIntent(this)) },
            graphAction = { launcher.launch(GraphActivity.createIntent(this)) },
            photoListAction = { launcher.launch(PhotoListActivity.createIntent(this)) },
            isTop = true,
        )
        setContent {
            TopScreen(
                bottomSheetDataList = bottomSheetDataList,
                onClickCalendar = { startActivity(CalendarActivity.createIntent(this)) },
                onClickAdd = {
                    launcher.launch(
                        MeasureFormActivity.createMeasureFormIntent(
                            this,
                            LocalDate.now()
                        )
                    )
                }
            )
        }
    }

    companion object {
        fun createIntent(
            context: Context
        ): Intent {
            return Intent(context, TopActivity::class.java)
        }
    }
}