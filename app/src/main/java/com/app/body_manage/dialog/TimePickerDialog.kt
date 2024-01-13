package com.app.body_manage.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.app.body_manage.R
import java.time.LocalDateTime

class TimePickerDialog : DialogFragment(), android.app.TimePickerDialog.OnTimeSetListener {

    private var hour = 1
    private var minute = 1

    private lateinit var callBack: (hour: Int, minute: Int) -> Unit

    companion object {
        private const val HOUR = "HOUR"
        private const val MINUTE = "MINUTE"

        fun createTimePickerDialog(
            localDateTime: LocalDateTime,
            callBack: (hour: Int, minute: Int) -> Unit
        ): TimePickerDialog {
            val timePickerDialog = TimePickerDialog()
            val bundle = Bundle()
            bundle.putInt(HOUR, localDateTime.hour)
            bundle.putInt(MINUTE, localDateTime.minute)
            timePickerDialog.arguments = bundle
            timePickerDialog.callBack = callBack
            return timePickerDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = arguments
        if (extras != null) {
            hour = extras.getInt(HOUR, -1)
            minute = extras.getInt(MINUTE, -1)
            if (hour in 0..23 && minute in 0..59) {
                return
            }
            throw RuntimeException("Illegal Argument")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return android.app.TimePickerDialog(
            requireActivity(),
            R.style.TimePickerTheme,
            this,
            hour,
            minute,
            true,
        )
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        callBack(hourOfDay, minute)
        dismiss()
    }
}
