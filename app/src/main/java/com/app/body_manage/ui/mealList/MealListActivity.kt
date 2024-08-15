package com.app.body_manage.ui.mealList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.body_manage.R
import com.app.body_manage.TrainingApplication
import com.app.body_manage.common.toKcal
import com.app.body_manage.ui.photoList.BottomBar

class MealListActivity : AppCompatActivity() {

    private val viewModel: MealListViewModel by lazy {
        MealListViewModel(
            mealRepository = (application as TrainingApplication).mealFoodsRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.reload()
        setContent {
            val foods by viewModel.foods.collectAsState()
            Scaffold(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                ),
                bottomBar = {
                    BottomBar(
                        onClickBack = ::finish
                    )
                },
            ) {
                if (foods.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .padding(horizontal = 10.dp)
                    ) {
                        items(foods) { food ->
                            val cornerShape = RoundedCornerShape(10.dp)
                            Column(
                                modifier = Modifier
                                    .shadow(1.dp, cornerShape)
                                    .fillMaxWidth()
                                    .border(0.5.dp, Color.DarkGray, cornerShape)
                                    .background(Color.White, cornerShape)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(text = stringResource(id = R.string.label_food_name))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(text = food.name)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(text = stringResource(id = R.string.label_food_kcal))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(text = food.kcal.toKcal())
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = stringResource(id = R.string.label_empty_food))
                    }
                }
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, MealListActivity::class.java)
    }
}
