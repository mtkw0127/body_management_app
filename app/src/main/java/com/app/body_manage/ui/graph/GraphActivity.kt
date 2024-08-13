package com.app.body_manage.ui.graph

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat

class GraphActivity : AppCompatActivity() {

    private lateinit var viewModel: GraphViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        viewModel = GraphViewModel(application = application)
        viewModel.loadBodyMeasure()

        setContent {
            val state: GraphState by viewModel.uiState.collectAsState()
            GraphScreen(
                state,
                onClickDataType = viewModel::setDataType,
                onClickDuration = viewModel::setDuration,
                onClickBack = ::finish
            )
        }
    }

    companion object {
        fun createIntent(context: Context) =
            Intent(context, GraphActivity::class.java)
    }
}
