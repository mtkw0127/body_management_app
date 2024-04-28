package com.app.body_manage.common

import androidx.annotation.StringRes
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(
    onClick: () -> Unit,
    @StringRes valueResourceId: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    fontSize: TextUnit = 12.sp,
    enable: Boolean = true,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor
        ),
        modifier = modifier,
        enabled = enable,
    ) {
        Text(
            text = stringResource(id = valueResourceId),
            textAlign = TextAlign.Center,
            fontSize = fontSize
        )
    }
}
