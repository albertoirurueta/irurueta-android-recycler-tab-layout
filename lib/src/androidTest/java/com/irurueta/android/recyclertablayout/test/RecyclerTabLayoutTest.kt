package com.irurueta.android.recyclertablayout.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.irurueta.android.recyclertablayout.RecyclerTabLayout
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecyclerTabLayoutTest {

    @get:Rule
    val activityRule = ActivityTestRule(RecyclerTabLayoutActivity::class.java, true)

    private var activity: RecyclerTabLayoutActivity? = null
    private var view: RecyclerTabLayout? = null

    @Before
    fun setUp() {
        activity = activityRule.activity
        view = activity?.findViewById(R.id.recycler_tab_layout_test)
    }

    @After
    fun tearDown() {
        view = null
        activity = null
    }

    @Test
    fun setCurrentItem_whenSmooth_displaysExpectedElements() {
        // create adapter
        val adapter = TabAdapter(COUNT)

        activityRule.runOnUiThread {
            view?.adapter = adapter
        }

        Thread.sleep(SLEEP)

        for (i in 0 until COUNT) {
            activityRule.runOnUiThread {
                view?.setCurrentItem(i)
            }

            Thread.sleep(SLEEP)
        }

        for (i in COUNT-1 downTo 0) {
            activityRule.runOnUiThread {
                view?.setCurrentItem(i)
            }

            Thread.sleep(SLEEP)
        }
    }

    @Test
    fun setCurrentItem_whenNotSmooth_displaysExpectedElements() {
        // create adapter
        val adapter = TabAdapter(COUNT)

        activityRule.runOnUiThread {
            view?.adapter = adapter
        }

        Thread.sleep(SLEEP)

        for (i in 0 until COUNT) {
            activityRule.runOnUiThread {
                view?.setCurrentItem(i, false)
            }

            Thread.sleep(SLEEP)
        }

        for (i in COUNT-1 downTo 0) {
            activityRule.runOnUiThread {
                view?.setCurrentItem(i, false)
            }

            Thread.sleep(SLEEP)
        }
    }

    private companion object {
        const val COUNT = 20

        const val SLEEP = 1000L
    }
}