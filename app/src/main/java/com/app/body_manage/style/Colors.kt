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
        val theme = Color(0xFFFFDA7F)
        val secondPrimary = Color(0xFF800020)
        val accentColor = Color(0xFF5CD5FA)
        val disable = Color(0xFFCACACA)
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
