/*
 * Copyright (C) 2025 Alberto Irurueta Carro (alberto@irurueta.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.irurueta.android.recyclertablayout.test

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.irurueta.android.recyclertablayout.RecyclerTabLayout
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecyclerTabLayoutTest {

    @get:Rule
    val rule = activityScenarioRule<RecyclerTabLayoutActivity>()

    private var activity: RecyclerTabLayoutActivity? = null
    private var view: RecyclerTabLayout? = null

    @Before
    fun setUp() {
        rule.scenario.onActivity { activity ->
            this.activity = activity
            view = activity?.findViewById(R.id.recycler_tab_layout_test)
        }
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

        activity?.runOnUiThread {
            view?.adapter = adapter
        }

        Thread.sleep(SLEEP)

        for (i in 0 until COUNT) {
            activity?.runOnUiThread {
                view?.setCurrentItem(i)
            }

            Thread.sleep(SLEEP)
        }

        for (i in COUNT-1 downTo 0) {
            activity?.runOnUiThread {
                view?.setCurrentItem(i)
            }

            Thread.sleep(SLEEP)
        }
    }

    @Test
    fun setCurrentItem_whenNotSmooth_displaysExpectedElements() {
        // create adapter
        val adapter = TabAdapter(COUNT)

        activity?.runOnUiThread {
            view?.adapter = adapter
        }

        Thread.sleep(SLEEP)

        for (i in 0 until COUNT) {
            activity?.runOnUiThread {
                view?.setCurrentItem(i, false)
            }

            Thread.sleep(SLEEP)
        }

        for (i in COUNT - 1 downTo 0) {
            activity?.runOnUiThread {
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