package com.app.body_manage.ui.graph

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class GraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val plots: MutableList<Pair<Float, Float>> = mutableListOf()
                plots.add(0F to 0F)
                plots.add(50F to 50F)
                plots.add(60F to 10F)
                plots.add(80F to 100F)
                Graph(plots.toList(), maxHeight = 400.dp)
            }
        }
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, GraphActivity::class.java)
    }
}

/**
 * グラフツール（自作途中）
 */
@Composable
fun Graph(plots: List<Pair<Float, Float>>, maxHeight: Dp) {
    val instaColors = listOf(Color.Yellow, Color.Red, Color.Magenta)

    val maxCanvasWidth = 500F
    val maxCanvasHeight = 500F

    Canvas(
        modifier = Modifier
            .fillMaxWidth(maxCanvasWidth)
            .fillMaxHeight(maxCanvasHeight)
            .border(
                width = 1.dp,
                color = Color.DarkGray
            )
            .background(Color.White)
    ) {
        // メモリ描画
        println(size.height)

        // 描画
        for ((index, plot) in plots.withIndex()) {
            // 点を表示
            drawCircle(
                brush = Brush.linearGradient(instaColors),
                center = Offset(
                    x = plot.first,
                    y = y(maxCanvasHeight, plot.second)
                ),
                radius = 5f
            )

            //線を表示
            if (plots.size == index + 1) continue
            drawLine(
                start = Offset(
                    x = plot.first,
                    y = y(maxCanvasHeight, plot.second)
                ),
                end = Offset(
                    x = plots[index + 1].first,
                    y = y(maxCanvasHeight, plots[index + 1].second)
                ),
                color = Color.Black,
                strokeWidth = 3F
            )
        }
    }
}

fun y(maxHeight: Float, y: Float): Float = maxHeight - y

@Composable
fun MyShape() {
    // 原点は左上の(0, 0)
    // 右にx、下にyの座標系
    val shape = GenericShape { size, _ ->
        moveTo(size.width / 2f, 0f)

        lineTo(size.width, size.height)

        lineTo(0f, size.height)
    }

    Box(
        modifier = Modifier
            .width(90.dp)
            .height(90.dp)
            .clip(shape)
            .background(Color.LightGray)
    )

}