package com.app.body_manage.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class Colors {
    companion object {
        val theme = Color(red = 232, green = 222, blue = 248)
        val secondThemeColor = Color(red = 212, green = 202, blue = 248)
        val accentColor = Color(98, 65, 247, 255)
        val nonAccentColor = Color(211, 219, 252, 255)
        val backgroundColor = Color(197, 230, 236, 255)
    }
}

@Preview
@Composable
private fun ThemeColor() {
    Box(
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .background(color = Colors.theme)
    )
}
