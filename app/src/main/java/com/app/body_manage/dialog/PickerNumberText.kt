package com.app.body_manage.dialog

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun PickerNumberText(
    text: String,
    fontSize: TextUnit = 18.sp,
    currentDigit: Digit,
    thisDigit: Digit
) {
    Text(
        text = text,
        fontSize = fontSize,
        modifier = Modifier.drawBehind {
            if (currentDigit == thisDigit) {
                drawLine(
                    Color.Black,
                    Offset(0F, this.size.height - 3),
                    Offset(
                        this.size.width,
                        this.size.height - 3
                    ),
                    strokeWidth = 1F
                )
            }
        }
    )
}
