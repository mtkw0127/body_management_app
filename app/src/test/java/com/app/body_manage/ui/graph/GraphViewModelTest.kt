package com.app.body_manage.ui.graph

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.body_manage.InstantExecutorExtension
import com.app.body_manage.TrainingApplication
import com.app.body_manage.data.entity.BodyMeasureEntity
import com.app.body_manage.data.repository.BodyMeasureRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
class GraphViewModelTest {

    @get:Rule
    val instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private val application: TrainingApplication = mockk()

    private lateinit var viewModel: GraphViewModel

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        viewModel = GraphViewModel(application)
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun after() {
        unmockkAll()
    }

    @Test
    fun `0件のグラフ用データ取得`() {
        // prepare
        val repository: BodyMeasureRepository = mockk(relaxed = true)
        coEvery { application.bodyMeasureRepository }.returns(repository)
        coEvery { repository.getEntityListAll() }.returns(mutableListOf())
        // exec
        viewModel.loadBodyMeasure()
        // assert
        val entryList = checkNotNull(viewModel.entryList.value)
        Assertions.assertEquals(2, entryList.size, "体重と体脂肪率以外が入っている")
        Assertions.assertEquals(true, entryList[0].isEmpty(), "体重が0件になっていない")
        Assertions.assertEquals(true, entryList[1].isEmpty(), "体脂肪率が0件になっていない")
    }

    @Test
    fun `有件のグラフ用データ取得`() {
        // prepare
        val returnValue: MutableList<BodyMeasureEntity> = mutableListOf()
        repeat(3) {
            returnValue.add(
                BodyMeasureEntity(
                    ui = 0,
                    calendarDate = LocalDate.now(),
                    capturedDate = LocalDate.now(),
                    capturedTime = LocalDateTime.now(),
                    weight = 0F,
                    fatRate = 0F,
                    photoUri = null,
                    tall = null,
                )
            )
        }
        val repository = mockk<BodyMeasureRepository>()
        coEvery { application.bodyMeasureRepository }.returns(repository)
        coEvery { repository.getEntityListAll() } returns returnValue.toList()
        // exec
        runBlocking {
            viewModel.loadBodyMeasure()
        }
        // assert
        val entryList = checkNotNull(viewModel.entryList.value)
        Assertions.assertEquals(2, entryList.size, "体重と体脂肪率以外が入っている")
        Assertions.assertEquals(3, entryList[0].size, "体重が3件になっていない")
        Assertions.assertEquals(3, entryList[1].size, "体脂肪率が3件になっていない")
    }
}
