package com.app.body_manage.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.DialogFragment
import com.app.body_manage.R
import com.app.body_manage.common.CustomButton
import com.app.body_manage.style.Colors

class IntNumberPickerDialog : DialogFragment() {
    private var number: Int = 50
    private var unit: String = ""
    private var label: String = ""
    private lateinit var callBack: (weight: Int) -> Unit

    // 初期値
    private var thousandPlace: String = ""
    private var hundredsPlace: String = ""
    private var tensPlace: String = ""
    private var onesPlace: String = ""

    enum class Digit {
        THOUSAND, HUNDRED, TENS, ONES
    }

    private var currentFocus: Digit = Digit.HUNDRED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = arguments
        if (extras != null) {
            label = extras.getString(LABEL, "")
            number = extras.getInt(NUMBER, 50)
            unit = extras.getString(UNIT, "")

            // 600 -> 0600
            val integerPart = String.format("%04d", number)
            thousandPlace = integerPart[0].toString()
            hundredsPlace = integerPart[1].toString()
            tensPlace = integerPart[2].toString()
            onesPlace = integerPart[3].toString()
            // 千の位に初期値がある場合はそこから入力を始める
            if (thousandPlace != "0") {
                currentFocus = Digit.THOUSAND
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                var thousandPlace by remember { mutableStateOf(thousandPlace) }
                var hundredsPlace by remember { mutableStateOf(hundredsPlace) }
                var tensPlace by remember { mutableStateOf(tensPlace) }
                var onesPlace by remember { mutableStateOf(onesPlace) }

                val clearNumber = {
                    thousandPlace = "0"
                    hundredsPlace = "0"
                    tensPlace = "0"
                    onesPlace = "0"
                    currentFocus = Digit.THOUSAND
                }

                val updateNumber: (Int) -> Unit = { number ->
                    when (currentFocus) {
                        Digit.THOUSAND -> {
                            thousandPlace = number.toString()
                            currentFocus = Digit.HUNDRED
                        }

                        Digit.HUNDRED -> {
                            hundredsPlace = number.toString()
                            currentFocus = Digit.TENS
                        }

                        Digit.TENS -> {
                            tensPlace = number.toString()
                            currentFocus = Digit.ONES
                        }

                        Digit.ONES -> {
                            onesPlace = number.toString()
                            currentFocus = Digit.HUNDRED
                        }
                    }
                }

                Dialog(
                    onDismissRequest = { dismiss() },
                    properties = DialogProperties(
                        usePlatformDefaultWidth = false
                    ),
                ) {
                    val roundedCornerShape = RoundedCornerShape(10.dp)
                    Column(
                        modifier = Modifier
                            .border(0.dp, Color.DarkGray, roundedCornerShape)
                            .background(Color.White, roundedCornerShape)
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            // 入力対象
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.width(100.dp),
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                                Row(
                                    modifier = Modifier
                                        .background(
                                            Colors.backgroundDark,
                                            RoundedCornerShape(5.dp)
                                        )
                                        .width(100.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.Bottom,
                                ) {
                                    Text(
                                        text = thousandPlace,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = hundredsPlace,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = tensPlace,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = onesPlace,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.size(5.dp))
                                Text(
                                    text = unit,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            // 電卓箇所
                            Column(
                                modifier = Modifier
                                    .background(
                                        Colors.background,
                                        roundedCornerShape,
                                    )
                                    .width(220.dp)
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    CustomButton(
                                        onClick = { updateNumber(1) },
                                        valueResourceId = R.string.label_number_1
                                    )
                                    CustomButton(
                                        onClick = { updateNumber(2) },
                                        valueResourceId = R.string.label_number_2
                                    )
                                    CustomButton(
                                        onClick = { updateNumber(3) },
                                        valueResourceId = R.string.label_number_3
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    CustomButton(
                                        onClick = { updateNumber(4) },
                                        valueResourceId = R.string.label_number_4
                                    )
                                    CustomButton(
                                        onClick = { updateNumber(5) },
                                        valueResourceId = R.string.label_number_5
                                    )
                                    CustomButton(
                                        onClick = { updateNumber(6) },
                                        valueResourceId = R.string.label_number_6
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    CustomButton(
                                        onClick = { updateNumber(7) },
                                        valueResourceId = R.string.label_number_7
                                    )
                                    CustomButton(
                                        onClick = { updateNumber(8) },
                                        valueResourceId = R.string.label_number_8
                                    )
                                    CustomButton(
                                        onClick = { updateNumber(9) },
                                        valueResourceId = R.string.label_number_9
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    CustomButton(
                                        onClick = clearNumber,
                                        valueResourceId = R.string.label_number_C
                                    )
                                    Spacer(modifier = Modifier.size(3.dp))
                                    CustomButton(
                                        onClick = { updateNumber(0) },
                                        valueResourceId = R.string.label_number_0
                                    )
                                }
                            }
                        }
                        CustomButton(
                            onClick = {
                                val integerPlace =
                                    thousandPlace.toInt() * 1000 + hundredsPlace.toInt() * 100 + tensPlace.toInt() * 10 + onesPlace.toInt()
                                callBack(integerPlace)
                                dismiss()
                            },
                            backgroundColor = Colors.theme,
                            valueResourceId = R.string.label_record,
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val LABEL = "LABEL"
        private const val NUMBER = "NUMBER"
        private const val UNIT = "UNIT"
        fun createDialog(
            label: String,
            number: Int,
            unit: String,
            callBack: (weight: Int) -> Unit
        ): IntNumberPickerDialog {
            val numberPickerDialog = IntNumberPickerDialog()
            val bundle = Bundle().apply {
                putString(LABEL, label)
                putInt(NUMBER, number)
                putString(UNIT, unit)
            }
            numberPickerDialog.arguments = bundle
            numberPickerDialog.callBack = callBack
            return numberPickerDialog
        }
    }
}
