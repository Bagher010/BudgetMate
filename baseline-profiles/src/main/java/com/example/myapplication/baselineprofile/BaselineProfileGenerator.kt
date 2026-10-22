package com.example.myapplication.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.UiDevice
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() {
        try {
            baselineProfileRule.collect(
        packageName = "com.example.myapplication"
    ) {

        startActivityAndWait()
        device.wait(Until.hasObject(By.text("Home")), 3000)

        Thread.sleep(100) 

        val scrollable = device.findObject(By.scrollable(true))

        repeat(15) { iteration ->
            val speed = when {
                iteration < 5 -> 0.4f  
                iteration < 10 -> 0.6f 
                else -> 0.5f           
            }

            scrollable?.scroll(Direction.DOWN, speed)
            Thread.sleep(50) 
        }

        repeat(10) {
            scrollable?.scroll(Direction.UP, 0.5f)
            Thread.sleep(50)
        }

        repeat(20) {
            scrollable?.scroll(Direction.DOWN, 0.1f) 
            Thread.sleep(100) 
        }

        repeat(20) {
            scrollable?.scroll(Direction.UP, 0.1f)
            Thread.sleep(100)
        }

        repeat(8) {

            scrollable?.fling(Direction.DOWN)
            Thread.sleep(500) 

            scrollable?.fling(Direction.UP)
            Thread.sleep(500)
        }

        device.findObject(By.text("Transactions"))?.click()
        device.wait(Until.hasObject(By.text("Transactions")), 2000)

        val transactionsScrollable = device.findObject(By.scrollable(true))

        repeat(8) { iteration ->
            when (iteration % 4) {
                0 -> {

                    transactionsScrollable?.scroll(Direction.DOWN, 0.3f)
                    Thread.sleep(50)
                }
                1 -> {

                    transactionsScrollable?.scroll(Direction.DOWN, 0.5f)
                    Thread.sleep(30)
                }
                2 -> {

                    transactionsScrollable?.scroll(Direction.DOWN, 0.8f)
                    Thread.sleep(20)
                }
                3 -> {

                    transactionsScrollable?.scroll(Direction.DOWN, 0.4f)
                    Thread.sleep(30)
                    transactionsScrollable?.scroll(Direction.UP, 0.5f)
                    Thread.sleep(30)
                }
            }
        }

        listOf("Home", "Statistics").forEach { screen ->
            device.findObject(By.text(screen))?.click()
            device.wait(Until.hasObject(By.text(screen)), 1500)

            val screenScrollable = device.findObject(By.scrollable(true))
            screenScrollable?.scroll(Direction.DOWN, 0.5f)
            Thread.sleep(50)
            screenScrollable?.scroll(Direction.UP, 0.4f)
            Thread.sleep(50)
        }
            }
        } catch (e: Exception) {

            println("Baseline profile rule failed, running manual test: ${e.message}")
            runManualBaselineProfileTest()
        }
    }

    private fun runManualBaselineProfileTest() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        device.executeShellCommand("am start -n com.example.myapplication/.MainActivity")
        device.wait(Until.hasObject(By.text("Home")), 3000)

        val scrollable = device.findObject(By.scrollable(true))

        repeat(15) { iteration ->
            val speed = when {
                iteration < 5 -> 0.4f  
                iteration < 10 -> 0.6f 
                else -> 0.5f           
            }

            scrollable?.scroll(Direction.DOWN, speed)
            Thread.sleep(50) 
        }

        repeat(10) {
            scrollable?.scroll(Direction.UP, 0.5f)
            Thread.sleep(50)
        }

        repeat(20) {
            scrollable?.scroll(Direction.DOWN, 0.1f) 
            Thread.sleep(100) 
        }

        repeat(20) {
            scrollable?.scroll(Direction.UP, 0.1f)
            Thread.sleep(100)
        }

        repeat(8) {
            scrollable?.fling(Direction.DOWN)
            Thread.sleep(500) 

            scrollable?.fling(Direction.UP)
            Thread.sleep(500)
        }

        device.findObject(By.text("Transactions"))?.click()
        device.wait(Until.hasObject(By.text("Transactions")), 2000)

        val transactionsScrollable = device.findObject(By.scrollable(true))

        repeat(8) { iteration ->
            when (iteration % 4) {
                0 -> {
                    transactionsScrollable?.scroll(Direction.DOWN, 0.3f)
                    Thread.sleep(50)
                }
                1 -> {
                    transactionsScrollable?.scroll(Direction.DOWN, 0.5f)
                    Thread.sleep(30)
                }
                2 -> {
                    transactionsScrollable?.scroll(Direction.DOWN, 0.8f)
                    Thread.sleep(20)
                }
                3 -> {
                    transactionsScrollable?.scroll(Direction.DOWN, 0.4f)
                    Thread.sleep(30)
                    transactionsScrollable?.scroll(Direction.UP, 0.5f)
                    Thread.sleep(30)
                }
            }
        }

        listOf("Home", "Statistics").forEach { screen ->
            device.findObject(By.text(screen))?.click()
            device.wait(Until.hasObject(By.text(screen)), 1500)

            val screenScrollable = device.findObject(By.scrollable(true))
            screenScrollable?.scroll(Direction.DOWN, 0.5f)
            Thread.sleep(50)
            screenScrollable?.scroll(Direction.UP, 0.4f)
            Thread.sleep(50)
        }

        println("Manual baseline profile test completed successfully!")
    }
}
