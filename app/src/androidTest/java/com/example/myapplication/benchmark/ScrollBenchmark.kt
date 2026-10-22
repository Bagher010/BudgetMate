package com.example.myapplication.benchmark

import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
@androidx.benchmark.macro.ExperimentalMetricApi
class ScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    companion object {
        private const val TARGET_PACKAGE = "com.example.myapplication"
        private const val DEFAULT_ITERATIONS = 3
    }

    @Test
    fun scrollHomeScreen() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            androidx.benchmark.macro.FrameTimingMetric(),
            androidx.benchmark.macro.TraceSectionMetric("scroll")
        ),
        iterations = DEFAULT_ITERATIONS,
        setupBlock = {
            pressHome()
        }
    ) {

        startActivityAndWait()

        device.wait(Until.hasObject(By.res("com.example.myapplication:id/home_screen")), 5000)

        val scrollable = device.findObject(By.res("com.example.myapplication:id/lazy_column"))

        scrollable?.let { scrollView ->

            repeat(5) {
                scrollView.fling(Direction.DOWN)
                device.waitForIdle()
            }

            repeat(5) {
                scrollView.fling(Direction.UP)
                device.waitForIdle()
            }
        }
    }

    @Test
    fun scrollTransactionsScreen() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            androidx.benchmark.macro.FrameTimingMetric(),
            androidx.benchmark.macro.TraceSectionMetric("transactions_scroll")
        ),
        iterations = DEFAULT_ITERATIONS,
        setupBlock = {
            pressHome()
        }
    ) {

        startActivityAndWait()

        device.findObject(By.text("Transactions"))?.click()
        device.waitForIdle()

        device.wait(Until.hasObject(By.res("com.example.myapplication:id/transactions_screen")), 5000)

        val transactionsList = device.findObject(By.res("com.example.myapplication:id/transactions_list"))

        transactionsList?.let { list ->

            repeat(8) {
                list.fling(Direction.DOWN)
                device.waitForIdle()
            }

            repeat(8) {
                list.fling(Direction.UP)
                device.waitForIdle()
            }
        }
    }

    @Test
    fun scrollStatisticsScreen() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            androidx.benchmark.macro.FrameTimingMetric(),
            androidx.benchmark.macro.TraceSectionMetric("statistics_scroll")
        ),
        iterations = DEFAULT_ITERATIONS,
        setupBlock = {
            pressHome()
        }
    ) {

        startActivityAndWait()

        device.findObject(By.text("Statistics"))?.click()
        device.waitForIdle()

        device.wait(Until.hasObject(By.res("com.example.myapplication:id/statistics_screen")), 5000)

        val statisticsContent = device.findObject(By.res("com.example.myapplication:id/statistics_content"))

        statisticsContent?.let { content ->

            repeat(6) {
                content.fling(Direction.DOWN)
                device.waitForIdle()
            }

            repeat(6) {
                content.fling(Direction.UP)
                device.waitForIdle()
            }
        }
    }
}
