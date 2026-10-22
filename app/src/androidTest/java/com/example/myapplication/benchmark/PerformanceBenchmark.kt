package com.example.myapplication.benchmark

import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
@androidx.benchmark.macro.ExperimentalMetricApi
class PerformanceBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    companion object {
        private const val TARGET_PACKAGE = "com.example.myapplication"
        private const val DEFAULT_ITERATIONS = 3
    }

    @Test
    fun appPerformance() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            androidx.benchmark.macro.StartupTimingMetric(),
            androidx.benchmark.macro.FrameTimingMetric(),
            androidx.benchmark.macro.MemoryUsageMetric(androidx.benchmark.macro.MemoryUsageMetric.Mode.Last)
        ),
        iterations = DEFAULT_ITERATIONS,
        setupBlock = {
            pressHome()
        }
    ) {

        startActivityAndWait()

        device.wait(Until.hasObject(By.res("com.example.myapplication:id/home_screen")), 5000)

        device.findObject(By.text("Transactions"))?.click()
        device.waitForIdle()
        device.wait(Until.hasObject(By.res("com.example.myapplication:id/transactions_screen")), 5000)

        device.findObject(By.text("Statistics"))?.click()
        device.waitForIdle()
        device.wait(Until.hasObject(By.res("com.example.myapplication:id/statistics_screen")), 5000)

        device.findObject(By.text("Home"))?.click()
        device.waitForIdle()
        device.wait(Until.hasObject(By.res("com.example.myapplication:id/home_screen")), 5000)

        device.findObject(By.res("com.example.myapplication:id/fab"))?.click()
        device.waitForIdle()
        device.wait(Until.hasObject(By.res("com.example.myapplication:id/add_transaction_screen")), 5000)

        device.pressBack()
        device.waitForIdle()
    }

    @Test
    fun memoryUsage() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            androidx.benchmark.macro.MemoryUsageMetric(androidx.benchmark.macro.MemoryUsageMetric.Mode.Last)
        ),
        iterations = DEFAULT_ITERATIONS,
        setupBlock = {
            pressHome()
        }
    ) {

        startActivityAndWait()

        device.wait(Until.hasObject(By.res("com.example.myapplication:id/home_screen")), 5000)

        repeat(3) {

            device.findObject(By.text("Transactions"))?.click()
            device.waitForIdle()

            device.findObject(By.text("Statistics"))?.click()
            device.waitForIdle()

            device.findObject(By.text("Home"))?.click()
            device.waitForIdle()
        }
    }

    @Test
    fun frameTiming() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            androidx.benchmark.macro.FrameTimingMetric()
        ),
        iterations = DEFAULT_ITERATIONS,
        setupBlock = {
            pressHome()
        }
    ) {

        startActivityAndWait()

        device.wait(Until.hasObject(By.res("com.example.myapplication:id/home_screen")), 5000)

        val homeScreen = device.findObject(By.res("com.example.myapplication:id/home_screen"))
        homeScreen?.let { screen ->

            repeat(5) {
                screen.click()
                device.waitForIdle()
            }
        }

        device.findObject(By.text("Transactions"))?.click()
        device.waitForIdle()

        device.findObject(By.text("Statistics"))?.click()
        device.waitForIdle()

        device.findObject(By.text("Home"))?.click()
        device.waitForIdle()
    }
}
