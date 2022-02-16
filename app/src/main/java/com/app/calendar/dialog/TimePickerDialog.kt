package com.app.calendar.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.app.calendar.R
import java.lang.RuntimeException


class TimePickerDialog: DialogFragment(){

    var hour = 1
    var minute = 1

    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var callBack:(hour:Int, minute:Int)->Unit

    companion object {
        const val HOUR = "HOUR"
        const val MINUTE = "MINUTE"

        fun createTimePickerDialog(hour:Int, minute: Int, callBack: (hour:Int, minute:Int)->Unit):TimePickerDialog {
            val timePickerDialog = TimePickerDialog()
            val bundle = Bundle()
            bundle.putInt(HOUR, hour)
            bundle.putInt(MINUTE, minute)
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

            if(hour in 0..23 && minute in 0..59) {
                return
            }
            throw RuntimeException("Illegal Argument")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val activity = requireActivity()
        val dialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.time_picker_dialog, null)
        hourPicker = dialogLayout.findViewById(R.id.hour)
        minutePicker = dialogLayout.findViewById(R.id.minute)

        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        hourPicker.value = hour

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = minute

        val okButton = dialogLayout.findViewById<Button>(R.id.ok_btn)
        okButton.setOnClickListener {
            callBack(hourPicker.value, minutePicker.value)
            this.dismiss()
        }

        return dialogBuilder.setView(dialogLayout).create()
    }
}