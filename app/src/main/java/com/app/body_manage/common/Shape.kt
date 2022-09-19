package com.app.body_manage.common

import androidx.compose.foundation.shape.GenericShape

val ReverseTriangleShape = GenericShape { size, _ ->
    // 1)
    moveTo(0f, 0f)

    // 2)
    lineTo(size.width, 0f)

    // 3)
    lineTo(size.width / 2, size.height)
}