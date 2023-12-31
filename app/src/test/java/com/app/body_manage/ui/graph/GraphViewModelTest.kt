package com.app.body_manage.ui.graph

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.body_manage.InstantExecutorExtension
import com.app.body_manage.TrainingApplication
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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
}
