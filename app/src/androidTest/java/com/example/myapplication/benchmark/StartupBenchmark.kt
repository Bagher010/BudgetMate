package com.example.myapplication.benchmark

import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    companion object {
        private const val TARGET_PACKAGE = "com.example.myapplication"
        private const val DEFAULT_ITERATIONS = 5
    }

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            androidx.benchmark.macro.StartupTimingMetric()
        ),
        iterations = DEFAULT_ITERATIONS,
        setupBlock = {

            pressHome()
        }
    ) {

        startActivityAndWait()
    }

    @Test
    fun startupCompilationMode() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            androidx.benchmark.macro.StartupTimingMetric()
        ),
        iterations = DEFAULT_ITERATIONS,
        compilationMode = androidx.benchmark.macro.CompilationMode.Partial(),
        setupBlock = {
            pressHome()
        }
    ) {
        startActivityAndWait()
    }

    @Test
    fun startupFullCompilation() = benchmarkRule.measureRepeated(
        packageName = TARGET_PACKAGE,
        metrics = listOf(
            androidx.benchmark.macro.StartupTimingMetric()
        ),
        iterations = DEFAULT_ITERATIONS,
        compilationMode = androidx.benchmark.macro.CompilationMode.Full(),
        setupBlock = {
            pressHome()
        }
    ) {
        startActivityAndWait()
    }
}
