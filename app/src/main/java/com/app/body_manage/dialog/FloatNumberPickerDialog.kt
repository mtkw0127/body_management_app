package com.app.body_manage.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.app.body_manage.R

class FloatNumberPickerDialog : DialogFragment() {
    private var number: Float = 0.0F
    private var unit: String = ""
    private lateinit var bigPicker: NumberPicker
    private lateinit var smallPicker: NumberPicker
    private lateinit var unitTextView: TextView
    private lateinit var callBack: (weight: Float) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = arguments
        if (extras != null) {
            number = extras.getFloat(NUMBER, 50.0F)
            unit = extras.getString(UNIT, "")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val activity = requireActivity()
        val dialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.float_number_picker_dialog, null)
        bigPicker = dialogLayout.findViewById(R.id.weight)
        smallPicker = dialogLayout.findViewById(R.id.small_weight)

        bigPicker.minValue = 0
        bigPicker.maxValue = 200
        bigPicker.value = number.toInt()

        smallPicker.minValue = 0
        smallPicker.maxValue = 9
        smallPicker.value = ((number - number.toInt()) * 100).toInt()

        unitTextView = dialogLayout.findViewById(R.id.small_weight_txt)
        unitTextView.text = unit

        val okButton = dialogLayout.findViewById<Button>(R.id.ok_wight_btn)
        okButton.setOnClickListener {
            val number = bigPicker.value.toFloat() + (smallPicker.value).toFloat() / 10.0F
            callBack(number)
            this.dismiss()
        }

        return dialogBuilder.setView(dialogLayout).create()
    }

    companion object {
        const val NUMBER = "NUMBER"
        const val UNIT = "UNIT"
        fun createDialog(
            number: Float,
            unit: String,
            callBack: (weight: Float) -> Unit
        ): FloatNumberPickerDialog {
            val numberPickerDialog = FloatNumberPickerDialog()
            val bundle = Bundle()
            bundle.putFloat(NUMBER, number)
            bundle.putString(UNIT, unit)
            numberPickerDialog.arguments = bundle
            numberPickerDialog.callBack = callBack
            return numberPickerDialog
        }
    }
}
