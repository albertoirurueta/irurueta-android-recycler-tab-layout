/*
 * Copyright (C) 2023 Alberto Irurueta Carro (alberto@irurueta.com)
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

package com.irurueta.android.recyclertablayout

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import io.mockk.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

@RunWith(RobolectricTestRunner::class)
class RecyclerTabLayoutTest {

    @Test
    fun constants_haveExpectedDefaultValues() {
        assertEquals(200L, RecyclerTabLayout.DEFAULT_SCROLL_DURATION_MILLIS)
    }

    @Test
    fun constructor_initializesDefaultValues() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        assertEquals(context.getColor(
            com.google.android.material.R.color.design_default_color_primary),
            view.indicatorColor)
        assertEquals(2, view.indicatorHeight)
        val tabOnScreenLimitEnabled: Boolean? = view.getPrivateProperty("tabOnScreenLimitEnabled")
        assertTrue(tabOnScreenLimitEnabled != null && tabOnScreenLimitEnabled)
        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        assertEquals(72, tabMinWidth)
        val scrollEnabled: Boolean? = view.getPrivateProperty("scrollEnabled")
        assertTrue(scrollEnabled != null && scrollEnabled)
        assertEquals(0, view.indicatorMarginLeft)
        assertEquals(0, view.indicatorMarginRight)

        val linearLayoutManager: LinearLayoutManager? =
            view.getPrivateProperty("linearLayoutManager")
        requireNotNull(linearLayoutManager)
        assertEquals(LinearLayoutManager.HORIZONTAL, linearLayoutManager.orientation)
    }

    @Test
    fun constructor_withAttributeSet_initializesWithDifferentParameters() {
        val typedArray = mockk<TypedArray>()
        every { typedArray.indexCount }.returns(0)

        every {
            typedArray.getColor(
                R.styleable.RecyclerTabLayout_tabIndicatorColor,
                Color.TRANSPARENT
            )
        }.returns(Color.BLACK)
        every {
            typedArray.getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabIndicatorHeight, 0)
        }.returns(10)
        every {
            typedArray.getBoolean(R.styleable.RecyclerTabLayout_tabOnScreenLimitEnabled, true)
        }.returns(true)
        every {
            typedArray.getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabMinWidth, 0)
        }.returns(100)
        every {
            typedArray.getBoolean(R.styleable.RecyclerTabLayout_scrollEnabled, true)
        }.returns(true)
        every {
            typedArray.getDimensionPixelSize(
                R.styleable.RecyclerTabLayout_tabIndicatorMarginStart,
                0
            )
        }.returns(1)
        every {
            typedArray.getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabIndicatorMarginEnd, 0)
        }.returns(2)
        every {
            typedArray.getString(com.google.android.material.R.styleable.RecyclerView_layoutManager)
        }.returns(null)
        every {
            typedArray.getInt(
                com.google.android.material.R.styleable.RecyclerView_android_descendantFocusability,
                -1)
        }.returns(-1)
        every {
            typedArray.getBoolean(
                com.google.android.material.R.styleable.RecyclerView_android_clipToPadding, true)
        }.returns(true)
        every {
            typedArray.getBoolean(
                com.google.android.material.R.styleable.RecyclerView_fastScrollEnabled, false)
        }.returns(false)

        justRun { typedArray.recycle() }

        mockkObject(ViewHelper)
        every { ViewHelper.isRtl(any()) }.returns(false)

        val context = spyk(ApplicationProvider.getApplicationContext())
        every { context.obtainStyledAttributes(any(), any(), any(), any()) }.returns(typedArray)

        val attrs = mockk<AttributeSet>()
        val view = RecyclerTabLayout(context, attrs)

        // check
        assertEquals(Color.BLACK, view.indicatorColor)
        assertEquals(10, view.indicatorHeight)
        val tabOnScreenLimitEnabled: Boolean? = view.getPrivateProperty("tabOnScreenLimitEnabled")
        assertTrue(tabOnScreenLimitEnabled != null && tabOnScreenLimitEnabled)
        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        assertEquals(100, tabMinWidth)
        val scrollEnabled: Boolean? = view.getPrivateProperty("scrollEnabled")
        assertTrue(scrollEnabled != null && scrollEnabled)
        assertEquals(1, view.indicatorMarginLeft)
        assertEquals(2, view.indicatorMarginRight)

        val linearLayoutManager: LinearLayoutManager? =
            view.getPrivateProperty("linearLayoutManager")
        requireNotNull(linearLayoutManager)
        assertEquals(LinearLayoutManager.HORIZONTAL, linearLayoutManager.orientation)

        verify(atLeast = 1) { typedArray.recycle() }
    }

    @Test
    fun constructor_withAttributeSetAndRtl_initializesWithDifferentParameters() {
        val typedArray = mockk<TypedArray>()
        every { typedArray.indexCount }.returns(0)

        every {
            typedArray.getColor(
                R.styleable.RecyclerTabLayout_tabIndicatorColor,
                Color.TRANSPARENT
            )
        }.returns(Color.BLACK)
        every {
            typedArray.getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabIndicatorHeight, 0)
        }.returns(10)
        every {
            typedArray.getBoolean(R.styleable.RecyclerTabLayout_tabOnScreenLimitEnabled, true)
        }.returns(true)
        every {
            typedArray.getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabMinWidth, 0)
        }.returns(100)
        every {
            typedArray.getBoolean(R.styleable.RecyclerTabLayout_scrollEnabled, true)
        }.returns(true)
        every {
            typedArray.getDimensionPixelSize(
                R.styleable.RecyclerTabLayout_tabIndicatorMarginStart,
                0
            )
        }.returns(1)
        every {
            typedArray.getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabIndicatorMarginEnd, 0)
        }.returns(2)
        every {
            typedArray.getString(com.google.android.material.R.styleable.RecyclerView_layoutManager)
        }.returns(null)
        every {
            typedArray.getInt(
                com.google.android.material.R.styleable.RecyclerView_android_descendantFocusability,
                -1)
        }.returns(-1)
        every {
            typedArray.getBoolean(
                com.google.android.material.R.styleable.RecyclerView_android_clipToPadding, true)
        }.returns(true)
        every {
            typedArray.getBoolean(
                com.google.android.material.R.styleable.RecyclerView_fastScrollEnabled, false)
        }.returns(false)

        justRun { typedArray.recycle() }

        mockkObject(ViewHelper)
        every { ViewHelper.isRtl(any()) }.returns(true)

        val context = spyk(ApplicationProvider.getApplicationContext())
        every { context.obtainStyledAttributes(any(), any(), any(), any()) }.returns(typedArray)

        val attrs = mockk<AttributeSet>()
        val view = RecyclerTabLayout(context, attrs)

        // check
        assertEquals(Color.BLACK, view.indicatorColor)
        assertEquals(10, view.indicatorHeight)
        val tabOnScreenLimitEnabled: Boolean? = view.getPrivateProperty("tabOnScreenLimitEnabled")
        assertTrue(tabOnScreenLimitEnabled != null && tabOnScreenLimitEnabled)
        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        assertEquals(100, tabMinWidth)
        val scrollEnabled: Boolean? = view.getPrivateProperty("scrollEnabled")
        assertTrue(scrollEnabled != null && scrollEnabled)
        assertEquals(2, view.indicatorMarginLeft)
        assertEquals(1, view.indicatorMarginRight)

        val linearLayoutManager: LinearLayoutManager? =
            view.getPrivateProperty("linearLayoutManager")
        requireNotNull(linearLayoutManager)
        assertEquals(LinearLayoutManager.HORIZONTAL, linearLayoutManager.orientation)

        verify(atLeast = 1) { typedArray.recycle() }
    }

    @Test
    fun indicatorColor_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        // check default value
        assertEquals(context.getColor(
            com.google.android.material.R.color.design_default_color_primary), view.indicatorColor)

        // set new value
        view.indicatorColor = Color.RED

        // check
        assertEquals(Color.RED, view.indicatorColor)
    }

    @Test
    fun indicatorHeight_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        // check default value
        assertEquals(2, view.indicatorHeight)

        // set new value
        view.indicatorHeight = 5

        // check
        assertEquals(5, view.indicatorHeight)
    }

    @Test
    fun indicatorMarginLeft_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        // check default value
        assertEquals(0, view.indicatorMarginLeft)

        // set new value
        view.indicatorMarginLeft = 3

        // check
        assertEquals(3, view.indicatorMarginLeft)
    }

    @Test
    fun indicatorMarginRight_returnsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        // check default value
        assertEquals(0, view.indicatorMarginRight)

        // set new value
        view.indicatorMarginRight = 4

        // check
        assertEquals(4, view.indicatorMarginRight)
    }

    @Test
    fun setAutoSelectionMode_setsExpectedValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        // check initial values
        val listener1: Any? = view.getPrivateProperty("recyclerOnScrollListener")
        assertNotNull(listener1)
        val autoSelect1: Boolean? = view.getPrivateProperty("autoSelect")
        requireNotNull(autoSelect1)
        assertFalse(autoSelect1)

        // set new value
        view.setAutoSelectionMode(true)

        // check
        val listener2: Any? = view.getPrivateProperty("recyclerOnScrollListener")
        assertNotNull(listener2)
        assertNotSame(listener1, listener2)
        val autoSelect2: Boolean? = view.getPrivateProperty("autoSelect")
        requireNotNull(autoSelect2)
        assertTrue(autoSelect2)
    }

    @Test
    fun setCurrentItem_whenNotSmoothScrollNoViewsInLayoutManagerNoMeasuredWidthAndNoIndicatorHeight_scrollsToExpectedPosition() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = spyk(RecyclerTabLayout(context))

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        assertEquals(72, tabMinWidth)

        assertEquals(2, view.indicatorHeight)

        // set no indicator height
        view.indicatorHeight = 0

        // check default value
        val indicatorPosition1: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition1)
        assertEquals(0, indicatorPosition1)
        assertNotNull(view.getPrivateProperty("linearLayoutManager"))

        // set new current item
        view.setCurrentItem(1, false)

        // check
        val hasSelectedView: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView)
        assertFalse(hasSelectedView)

        val startIndicatorLeft: Float? = view.getPrivateProperty("startIndicatorLeft")
        requireNotNull(startIndicatorLeft)
        assertEquals(0.0f, startIndicatorLeft, 0.0f)
        val startIndicatorRight: Float? = view.getPrivateProperty("startIndicatorRight")
        requireNotNull(startIndicatorRight)
        assertEquals(0.0f, startIndicatorRight, 0.0f)

        val indicatorPosition2: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition2)
        assertEquals(1, indicatorPosition2)

        val oldPosition: Int? = view.getPrivateProperty("oldPosition")
        requireNotNull(oldPosition)
        assertEquals(1, oldPosition)

        val oldScrollOffset: Int? = view.getPrivateProperty("oldScrollOffset")
        requireNotNull(oldScrollOffset)
        assertEquals(0, oldScrollOffset)

        val oldPositionOffset: Float? = view.getPrivateProperty("oldPositionOffset")
        requireNotNull(oldPositionOffset)
        assertEquals(0.0f, oldPositionOffset, 0.0f)

        verify(exactly = 0) { view.invalidate() }
    }

    @Test
    fun setCurrentItem_whenNotSmoothScrollNoViewsInLayoutManagerNoMeasuredWidthAndWithIndicatorHeight_scrollsToExpectedPosition() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = spyk(RecyclerTabLayout(context))

        assertEquals(2, view.indicatorHeight)

        // check default value
        val indicatorPosition1: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition1)
        assertEquals(0, indicatorPosition1)
        assertNotNull(view.getPrivateProperty("linearLayoutManager"))

        // set new current item
        view.setCurrentItem(1, false)

        // check
        val hasSelectedView: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView)
        assertFalse(hasSelectedView)

        val startIndicatorLeft: Float? = view.getPrivateProperty("startIndicatorLeft")
        requireNotNull(startIndicatorLeft)
        assertEquals(0.0f, startIndicatorLeft, 0.0f)
        val startIndicatorRight: Float? = view.getPrivateProperty("startIndicatorRight")
        requireNotNull(startIndicatorRight)
        assertEquals(0.0f, startIndicatorRight, 0.0f)

        val indicatorPosition2: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition2)
        assertEquals(1, indicatorPosition2)

        val oldPosition: Int? = view.getPrivateProperty("oldPosition")
        requireNotNull(oldPosition)
        assertEquals(1, oldPosition)

        val oldScrollOffset: Int? = view.getPrivateProperty("oldScrollOffset")
        requireNotNull(oldScrollOffset)
        assertEquals(0, oldScrollOffset)

        val oldPositionOffset: Float? = view.getPrivateProperty("oldPositionOffset")
        requireNotNull(oldPositionOffset)
        assertEquals(0.0f, oldPositionOffset, 0.0f)

        verify(exactly = 1) { view.invalidate() }
    }

    @Test
    fun setCurrentItem_whenNotSmoothScrollNoViewsInLayoutManagerWithMeasuredWidth_scrollsToExpectedPosition() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        assertEquals(0, view.measuredWidth)
        assertEquals(2, view.indicatorHeight)

        // set view size
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)
        assertEquals(2, view.indicatorHeight)

        // check default value
        val indicatorPosition1: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition1)
        assertEquals(0, indicatorPosition1)
        assertNotNull(view.getPrivateProperty("linearLayoutManager"))

        // set new current item
        view.setCurrentItem(1, false)

        // check
        val hasSelectedView: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView)
        assertFalse(hasSelectedView)

        val startIndicatorLeft: Float? = view.getPrivateProperty("startIndicatorLeft")
        requireNotNull(startIndicatorLeft)
        assertEquals(0.0f, startIndicatorLeft, 0.0f)
        val startIndicatorRight: Float? = view.getPrivateProperty("startIndicatorRight")
        requireNotNull(startIndicatorRight)
        assertEquals(0.0f, startIndicatorRight, 0.0f)

        val indicatorPosition2: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition2)
        assertEquals(1, indicatorPosition2)

        val oldPosition: Int? = view.getPrivateProperty("oldPosition")
        requireNotNull(oldPosition)
        assertEquals(1, oldPosition)

        val oldScrollOffset: Int? = view.getPrivateProperty("oldScrollOffset")
        requireNotNull(oldScrollOffset)
        assertEquals((view.measuredWidth - tabMinWidth) / 2, oldScrollOffset)

        val oldPositionOffset: Float? = view.getPrivateProperty("oldPositionOffset")
        requireNotNull(oldPositionOffset)
        assertEquals(0.0f, oldPositionOffset, 0.0f)
    }

    @Test
    fun setCurrentItem_whenNotSmoothScrollWithViewsInLayoutManager_scrollsToExpectedPosition() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        // setup view returned by layout manager spy
        val selectedView = mockk<View>()
        every { selectedView.measuredWidth }.returns(101)
        every { selectedView.translationX }.returns(10.0f)
        every { selectedView.left }.returns(11)
        every { selectedView.right }.returns(112)
        every { layoutManagerSpy.findViewByPosition(1) }.returns(selectedView)

        val nextView = mockk<View>()
        every { nextView.measuredWidth }.returns(102)
        every { layoutManagerSpy.findViewByPosition(2) }.returns(nextView)

        // set view size
        assertEquals(0, view.measuredWidth)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)

        // check default value
        val indicatorPosition1: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition1)
        assertEquals(0, indicatorPosition1)
        assertNotNull(view.getPrivateProperty("linearLayoutManager"))

        // set new current item
        view.setCurrentItem(1, false)

        // check
        val hasSelectedView: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView)
        assertTrue(hasSelectedView)

        val endIndicatorLeft: Float? = view.getPrivateProperty("endIndicatorLeft")
        requireNotNull(endIndicatorLeft)
        assertEquals(selectedView.left + selectedView.translationX, endIndicatorLeft)

        val endIndicatorRight: Float? = view.getPrivateProperty("endIndicatorRight")
        requireNotNull(endIndicatorRight)
        assertEquals(selectedView.right + selectedView.translationX, endIndicatorRight)

        val currentIndicatorLeft: Float? = view.getPrivateProperty("currentIndicatorLeft")
        requireNotNull(currentIndicatorLeft)
        assertEquals(endIndicatorLeft, currentIndicatorLeft)

        val currentIndicatorRight: Float? = view.getPrivateProperty("currentIndicatorRight")
        requireNotNull(currentIndicatorRight)
        assertEquals(endIndicatorRight, currentIndicatorRight)

        val startIndicatorLeft: Float? = view.getPrivateProperty("startIndicatorLeft")
        requireNotNull(startIndicatorLeft)
        assertEquals(endIndicatorLeft, startIndicatorLeft, 0.0f)
        val startIndicatorRight: Float? = view.getPrivateProperty("startIndicatorRight")
        requireNotNull(startIndicatorRight)
        assertEquals(endIndicatorRight, startIndicatorRight, 0.0f)

        val indicatorPosition2: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition2)
        assertEquals(1, indicatorPosition2)

        val indicatorScroll: Int? = view.getPrivateProperty("indicatorScroll")
        requireNotNull(indicatorScroll)
        assertEquals(0, indicatorScroll)

        val oldPosition: Int? = view.getPrivateProperty("oldPosition")
        requireNotNull(oldPosition)
        assertEquals(1, oldPosition)

        val oldScrollOffset: Int? = view.getPrivateProperty("oldScrollOffset")
        requireNotNull(oldScrollOffset)
        val sLeft = view.measuredWidth / 2.0f - selectedView.measuredWidth
        assertEquals(sLeft, oldScrollOffset.toFloat(), 0.0f)

        val oldPositionOffset: Float? = view.getPrivateProperty("oldPositionOffset")
        requireNotNull(oldPositionOffset)
        assertEquals(0.0f, oldPositionOffset, 0.0f)

        verify(exactly = 1) {
            (layoutManagerSpy as LinearLayoutManager).scrollToPositionWithOffset(1, oldScrollOffset)
        }
    }

    @Test
    fun setCurrentItem_whenNotSmoothScrollNoNextViewInLayoutManager_scrollsToExpectedPosition() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        // setup view returned by layout manager spy
        val selectedView = mockk<View>()
        every { selectedView.measuredWidth }.returns(101)
        every { selectedView.translationX }.returns(10.0f)
        every { selectedView.left }.returns(11)
        every { selectedView.right }.returns(112)
        every { layoutManagerSpy.findViewByPosition(1) }.returns(selectedView)

        // set view size
        assertEquals(0, view.measuredWidth)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)

        // check default value
        val indicatorPosition1: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition1)
        assertEquals(0, indicatorPosition1)
        assertNotNull(view.getPrivateProperty("linearLayoutManager"))

        // set new current item
        view.setCurrentItem(1, false)

        // check
        val hasSelectedView: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView)
        assertTrue(hasSelectedView)

        val endIndicatorLeft: Float? = view.getPrivateProperty("endIndicatorLeft")
        requireNotNull(endIndicatorLeft)
        assertEquals(selectedView.left + selectedView.translationX, endIndicatorLeft)

        val endIndicatorRight: Float? = view.getPrivateProperty("endIndicatorRight")
        requireNotNull(endIndicatorRight)
        assertEquals(selectedView.right + selectedView.translationX, endIndicatorRight)

        val currentIndicatorLeft: Float? = view.getPrivateProperty("currentIndicatorLeft")
        requireNotNull(currentIndicatorLeft)
        assertEquals(endIndicatorLeft, currentIndicatorLeft)

        val currentIndicatorRight: Float? = view.getPrivateProperty("currentIndicatorRight")
        requireNotNull(currentIndicatorRight)
        assertEquals(endIndicatorRight, currentIndicatorRight)

        val startIndicatorLeft: Float? = view.getPrivateProperty("startIndicatorLeft")
        requireNotNull(startIndicatorLeft)
        assertEquals(endIndicatorLeft, startIndicatorLeft, 0.0f)
        val startIndicatorRight: Float? = view.getPrivateProperty("startIndicatorRight")
        requireNotNull(startIndicatorRight)
        assertEquals(endIndicatorRight, startIndicatorRight, 0.0f)

        val indicatorPosition2: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition2)
        assertEquals(1, indicatorPosition2)

        val indicatorScroll: Int? = view.getPrivateProperty("indicatorScroll")
        requireNotNull(indicatorScroll)
        assertEquals(0, indicatorScroll)

        val oldPosition: Int? = view.getPrivateProperty("oldPosition")
        requireNotNull(oldPosition)
        assertEquals(1, oldPosition)

        val oldScrollOffset: Int? = view.getPrivateProperty("oldScrollOffset")
        requireNotNull(oldScrollOffset)
        val sLeft = view.measuredWidth / 2.0f - selectedView.measuredWidth
        assertEquals(sLeft, oldScrollOffset.toFloat(), 0.0f)

        val oldPositionOffset: Float? = view.getPrivateProperty("oldPositionOffset")
        requireNotNull(oldPositionOffset)
        assertEquals(0.0f, oldPositionOffset, 0.0f)

        verify(exactly = 1) {
            (layoutManagerSpy as LinearLayoutManager).scrollToPositionWithOffset(1, oldScrollOffset)
        }
    }

    @Test
    fun setCurrentItem_whenNotSmoothScrollAndPositionZero_scrollsToExpectedPosition() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        // setup view returned by layout manager spy
        val selectedView = mockk<View>()
        every { selectedView.measuredWidth }.returns(101)
        every { selectedView.translationX }.returns(10.0f)
        every { selectedView.left }.returns(11)
        every { selectedView.right }.returns(112)
        every { layoutManagerSpy.findViewByPosition(0) }.returns(selectedView)

        // set view size
        assertEquals(0, view.measuredWidth)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)

        // check default value
        val indicatorPosition1: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition1)
        assertEquals(0, indicatorPosition1)
        assertNotNull(view.getPrivateProperty("linearLayoutManager"))

        // set new current item
        view.setCurrentItem(0, false)

        // check
        val hasSelectedView: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView)
        assertTrue(hasSelectedView)

        val endIndicatorLeft: Float? = view.getPrivateProperty("endIndicatorLeft")
        requireNotNull(endIndicatorLeft)
        assertEquals(selectedView.left + selectedView.translationX, endIndicatorLeft)

        val endIndicatorRight: Float? = view.getPrivateProperty("endIndicatorRight")
        requireNotNull(endIndicatorRight)
        assertEquals(selectedView.right + selectedView.translationX, endIndicatorRight)

        val currentIndicatorLeft: Float? = view.getPrivateProperty("currentIndicatorLeft")
        requireNotNull(currentIndicatorLeft)
        assertEquals(endIndicatorLeft, currentIndicatorLeft)

        val currentIndicatorRight: Float? = view.getPrivateProperty("currentIndicatorRight")
        requireNotNull(currentIndicatorRight)
        assertEquals(endIndicatorRight, currentIndicatorRight)

        val startIndicatorLeft: Float? = view.getPrivateProperty("startIndicatorLeft")
        requireNotNull(startIndicatorLeft)
        assertEquals(endIndicatorLeft, startIndicatorLeft, 0.0f)
        val startIndicatorRight: Float? = view.getPrivateProperty("startIndicatorRight")
        requireNotNull(startIndicatorRight)
        assertEquals(endIndicatorRight, startIndicatorRight, 0.0f)

        val indicatorPosition2: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition2)
        assertEquals(0, indicatorPosition2)

        val indicatorScroll: Int? = view.getPrivateProperty("indicatorScroll")
        requireNotNull(indicatorScroll)
        assertEquals(0, indicatorScroll)

        val oldPosition: Int? = view.getPrivateProperty("oldPosition")
        requireNotNull(oldPosition)
        assertEquals(0, oldPosition)

        val oldScrollOffset: Int? = view.getPrivateProperty("oldScrollOffset")
        requireNotNull(oldScrollOffset)
        assertEquals(0, oldScrollOffset)

        val oldPositionOffset: Float? = view.getPrivateProperty("oldPositionOffset")
        requireNotNull(oldPositionOffset)
        assertEquals(0.0f, oldPositionOffset, 0.0f)

        verify(exactly = 0) {
            (layoutManagerSpy as LinearLayoutManager).scrollToPositionWithOffset(any(), any())
        }
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun setCurrentItem_whenSmoothScroll_scrollsToExpectedPosition() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        // setup view returned by layout manager spy
        val selectedView = mockk<View>()
        every { selectedView.measuredWidth }.returns(101)
        every { selectedView.translationX }.returns(10.0f)
        every { selectedView.left }.returns(11)
        every { selectedView.right }.returns(112)
        every { selectedView.x }.returns(selectedView.left + selectedView.translationX)
        every { layoutManagerSpy.findViewByPosition(1) }.returns(selectedView)

        val nextView = mockk<View>()
        every { nextView.measuredWidth }.returns(102)
        every { layoutManagerSpy.findViewByPosition(2) }.returns(nextView)

        // set view size
        assertEquals(0, view.measuredWidth)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)

        // check default value
        val indicatorPosition1: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition1)
        assertEquals(0, indicatorPosition1)
        assertNotNull(view.getPrivateProperty("linearLayoutManager"))

        // set new current item
        view.setCurrentItem(1, true)

        //finish animation
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // check
        val hasSelectedView: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView)
        assertTrue(hasSelectedView)

        val endIndicatorLeft: Float? = view.getPrivateProperty("endIndicatorLeft")
        requireNotNull(endIndicatorLeft)
        assertEquals(selectedView.left + selectedView.translationX, endIndicatorLeft)

        val endIndicatorRight: Float? = view.getPrivateProperty("endIndicatorRight")
        requireNotNull(endIndicatorRight)
        assertEquals(selectedView.right + selectedView.translationX, endIndicatorRight)

        val currentIndicatorLeft: Float? = view.getPrivateProperty("currentIndicatorLeft")
        requireNotNull(currentIndicatorLeft)
        assertEquals(endIndicatorLeft, currentIndicatorLeft)

        val currentIndicatorRight: Float? = view.getPrivateProperty("currentIndicatorRight")
        requireNotNull(currentIndicatorRight)
        assertEquals(endIndicatorRight, currentIndicatorRight)

        val startIndicatorLeft: Float? = view.getPrivateProperty("startIndicatorLeft")
        requireNotNull(startIndicatorLeft)
        assertEquals(endIndicatorLeft, startIndicatorLeft, 0.0f)
        val startIndicatorRight: Float? = view.getPrivateProperty("startIndicatorRight")
        requireNotNull(startIndicatorRight)
        assertEquals(endIndicatorRight, startIndicatorRight, 0.0f)

        val indicatorPosition2: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition2)
        assertEquals(1, indicatorPosition2)

        val indicatorScroll: Int? = view.getPrivateProperty("indicatorScroll")
        requireNotNull(indicatorScroll)
        assertEquals(0, indicatorScroll)

        val oldPosition: Int? = view.getPrivateProperty("oldPosition")
        requireNotNull(oldPosition)
        assertEquals(1, oldPosition)

        val oldScrollOffset: Int? = view.getPrivateProperty("oldScrollOffset")
        requireNotNull(oldScrollOffset)
        val sLeft = view.measuredWidth / 2.0f - selectedView.measuredWidth
        assertEquals(sLeft, oldScrollOffset.toFloat(), 0.0f)

        val oldPositionOffset: Float? = view.getPrivateProperty("oldPositionOffset")
        requireNotNull(oldPositionOffset)
        assertEquals(0.0f, oldPositionOffset, 0.0f)

        verify(exactly = 1) {
            (layoutManagerSpy as LinearLayoutManager).scrollToPositionWithOffset(1, oldScrollOffset)
        }
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun setCurrentItem_whenSmoothScrollAndAnimatorRunning_scrollsToExpectedPosition() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        val animator = mockk<ValueAnimator>()
        every { animator.isRunning }.returns(true)
        justRun { animator.cancel() }
        view.setPrivateProperty("animator", animator)

        // setup view returned by layout manager spy
        val selectedView = mockk<View>()
        every { selectedView.measuredWidth }.returns(101)
        every { selectedView.translationX }.returns(10.0f)
        every { selectedView.left }.returns(11)
        every { selectedView.right }.returns(112)
        every { selectedView.x }.returns(selectedView.left + selectedView.translationX)
        every { layoutManagerSpy.findViewByPosition(1) }.returns(selectedView)

        val nextView = mockk<View>()
        every { nextView.measuredWidth }.returns(102)
        every { layoutManagerSpy.findViewByPosition(2) }.returns(nextView)

        // set view size
        assertEquals(0, view.measuredWidth)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)

        // check default value
        val indicatorPosition1: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition1)
        assertEquals(0, indicatorPosition1)
        assertNotNull(view.getPrivateProperty("linearLayoutManager"))

        // set new current item
        view.setCurrentItem(1, true)

        //finish animation
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // check
        val hasSelectedView: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView)
        assertTrue(hasSelectedView)

        val endIndicatorLeft: Float? = view.getPrivateProperty("endIndicatorLeft")
        requireNotNull(endIndicatorLeft)
        assertEquals(selectedView.left + selectedView.translationX, endIndicatorLeft)

        val endIndicatorRight: Float? = view.getPrivateProperty("endIndicatorRight")
        requireNotNull(endIndicatorRight)
        assertEquals(selectedView.right + selectedView.translationX, endIndicatorRight)

        val currentIndicatorLeft: Float? = view.getPrivateProperty("currentIndicatorLeft")
        requireNotNull(currentIndicatorLeft)
        assertEquals(endIndicatorLeft, currentIndicatorLeft)

        val currentIndicatorRight: Float? = view.getPrivateProperty("currentIndicatorRight")
        requireNotNull(currentIndicatorRight)
        assertEquals(endIndicatorRight, currentIndicatorRight)

        val startIndicatorLeft: Float? = view.getPrivateProperty("startIndicatorLeft")
        requireNotNull(startIndicatorLeft)
        assertEquals(endIndicatorLeft, startIndicatorLeft, 0.0f)
        val startIndicatorRight: Float? = view.getPrivateProperty("startIndicatorRight")
        requireNotNull(startIndicatorRight)
        assertEquals(endIndicatorRight, startIndicatorRight, 0.0f)

        val indicatorPosition2: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition2)
        assertEquals(1, indicatorPosition2)

        val indicatorScroll: Int? = view.getPrivateProperty("indicatorScroll")
        requireNotNull(indicatorScroll)
        assertEquals(0, indicatorScroll)

        val oldPosition: Int? = view.getPrivateProperty("oldPosition")
        requireNotNull(oldPosition)
        assertEquals(1, oldPosition)

        val oldScrollOffset: Int? = view.getPrivateProperty("oldScrollOffset")
        requireNotNull(oldScrollOffset)
        val sLeft = view.measuredWidth / 2.0f - selectedView.measuredWidth
        assertEquals(sLeft, oldScrollOffset.toFloat(), 0.0f)

        val oldPositionOffset: Float? = view.getPrivateProperty("oldPositionOffset")
        requireNotNull(oldPositionOffset)
        assertEquals(0.0f, oldPositionOffset, 0.0f)

        verify(exactly = 1) {
            (layoutManagerSpy as LinearLayoutManager).scrollToPositionWithOffset(1, oldScrollOffset)
        }
        verify(exactly = 1) { animator.cancel() }
    }

    @LooperMode(LooperMode.Mode.PAUSED)
    @Test
    fun setCurrentItem_whenSmoothScrollAndSmallerPosition_scrollsToExpectedPosition() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        val animator = mockk<ValueAnimator>()
        every { animator.isRunning }.returns(true)
        justRun { animator.cancel() }
        view.setPrivateProperty("animator", animator)

        // setup view returned by layout manager spy
        val selectedView = mockk<View>()
        every { selectedView.measuredWidth }.returns(101)
        every { selectedView.translationX }.returns(10.0f)
        every { selectedView.left }.returns(11)
        every { selectedView.right }.returns(112)
        every { selectedView.x }.returns(selectedView.left + selectedView.translationX)
        every { layoutManagerSpy.findViewByPosition(1) }.returns(selectedView)
        every { layoutManagerSpy.findViewByPosition(0) }.returns(selectedView)

        val nextView = mockk<View>()
        every { nextView.measuredWidth }.returns(102)
        every { layoutManagerSpy.findViewByPosition(2) }.returns(nextView)

        // set view size
        assertEquals(0, view.measuredWidth)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)

        // check default value
        val indicatorPosition1: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition1)
        assertEquals(0, indicatorPosition1)
        assertNotNull(view.getPrivateProperty("linearLayoutManager"))

        // set new current item
        view.setCurrentItem(1, false)

        // set smaller new current item
        view.setCurrentItem(0, true)

        //finish animation
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // check
        val hasSelectedView: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView)
        assertTrue(hasSelectedView)

        val endIndicatorLeft: Float? = view.getPrivateProperty("endIndicatorLeft")
        requireNotNull(endIndicatorLeft)
        assertEquals(selectedView.left + selectedView.translationX, endIndicatorLeft)

        val endIndicatorRight: Float? = view.getPrivateProperty("endIndicatorRight")
        requireNotNull(endIndicatorRight)
        assertEquals(selectedView.right + selectedView.translationX, endIndicatorRight)

        val currentIndicatorLeft: Float? = view.getPrivateProperty("currentIndicatorLeft")
        requireNotNull(currentIndicatorLeft)
        assertEquals(endIndicatorLeft, currentIndicatorLeft)

        val currentIndicatorRight: Float? = view.getPrivateProperty("currentIndicatorRight")
        requireNotNull(currentIndicatorRight)
        assertEquals(endIndicatorRight, currentIndicatorRight)

        val startIndicatorLeft: Float? = view.getPrivateProperty("startIndicatorLeft")
        requireNotNull(startIndicatorLeft)
        assertEquals(endIndicatorLeft, startIndicatorLeft, 0.0f)
        val startIndicatorRight: Float? = view.getPrivateProperty("startIndicatorRight")
        requireNotNull(startIndicatorRight)
        assertEquals(endIndicatorRight, startIndicatorRight, 0.0f)

        val indicatorPosition2: Int? = view.getPrivateProperty("indicatorPosition")
        requireNotNull(indicatorPosition2)
        assertEquals(0, indicatorPosition2)

        val indicatorScroll: Int? = view.getPrivateProperty("indicatorScroll")
        requireNotNull(indicatorScroll)
        assertEquals(0, indicatorScroll)

        val oldPosition: Int? = view.getPrivateProperty("oldPosition")
        requireNotNull(oldPosition)
        assertEquals(0, oldPosition)

        val oldScrollOffset: Int? = view.getPrivateProperty("oldScrollOffset")
        requireNotNull(oldScrollOffset)
        assertEquals(0, oldScrollOffset)

        val oldPositionOffset: Float? = view.getPrivateProperty("oldPositionOffset")
        requireNotNull(oldPositionOffset)
        assertEquals(0.0f, oldPositionOffset, 0.0f)

        verify(exactly = 1) { animator.cancel() }
    }

    @Test
    fun onDraw_whenNoViewFoundByLayoutManager_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        // call onDraw
        val canvas = mockk<Canvas>()
        view.onDraw(canvas)

        verify(exactly = 1) { layoutManagerSpy.findViewByPosition(0) }
        verify { canvas wasNot Called }
    }

    @Test
    fun onDraw_whenViewFoundByLayoutManagerAndNotVisibleAtLeft_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        // setup view returned by layout manager spy
        val positionView = mockk<View>()
        every { positionView.right }.returns(-1)
        every { layoutManagerSpy.findViewByPosition(0) }.returns(positionView)

        // call onDraw
        val canvas = mockk<Canvas>()
        view.onDraw(canvas)

        verify(exactly = 1) { layoutManagerSpy.findViewByPosition(0) }
        verify { canvas wasNot Called }
    }

    @Test
    fun onDraw_whenViewFoundByLayoutManagerAndNotVisibleAtRight_makesNoAction() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        // setup view returned by layout manager spy
        val positionView = mockk<View>()
        every { positionView.right }.returns(0)
        every { positionView.left }.returns(1081)
        every { layoutManagerSpy.findViewByPosition(0) }.returns(positionView)

        // set view size
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)
        assertTrue(positionView.left > view.measuredWidth)

        // call onDraw
        val canvas = mockk<Canvas>()
        view.onDraw(canvas)

        verify(exactly = 1) { layoutManagerSpy.findViewByPosition(0) }
        verify { canvas wasNot Called }
    }

    @Test
    fun onDraw_whenViewFoundByLayoutManagerVisibleAndNotSelected_drawsIndicator() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        // setup view returned by layout manager spy
        val positionView = mockk<View>()
        every { positionView.right }.returns(0)
        every { positionView.left }.returns(1080)
        every { layoutManagerSpy.findViewByPosition(0) }.returns(positionView)

        // set view size
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)
        assertEquals(positionView.left, view.measuredWidth)

        val hasSelectedView: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView)
        assertFalse(hasSelectedView)

        // call onDraw
        val canvas = mockk<Canvas>()
        justRun {
            canvas.drawRect(any(), any(), any(), any(), any())
        }
        view.onDraw(canvas)

        verify(exactly = 1) { layoutManagerSpy.findViewByPosition(0) }

        val endIndicatorLeft: Float? = view.getPrivateProperty("endIndicatorLeft")
        requireNotNull(endIndicatorLeft)
        assertEquals(positionView.left.toFloat(), endIndicatorLeft, 0.0f)

        val startIndicatorLeft: Float? = view.getPrivateProperty("startIndicatorLeft")
        requireNotNull(startIndicatorLeft)
        assertEquals(endIndicatorLeft, startIndicatorLeft, 0.0f)

        val endIndicatorRight: Float? = view.getPrivateProperty("endIndicatorRight")
        requireNotNull(endIndicatorRight)
        assertEquals(positionView.right.toFloat(), endIndicatorRight)

        val startIndicatorRight: Float? = view.getPrivateProperty("startIndicatorRight")
        requireNotNull(startIndicatorRight)
        assertEquals(endIndicatorRight, startIndicatorRight, 0.0f)

        val left = startIndicatorLeft + view.indicatorMarginLeft
        val right = startIndicatorRight - view.indicatorMarginRight
        val top = (view.height - view.indicatorHeight).toFloat()
        val bottom = view.height.toFloat()

        val indicatorPaint: Paint? = view.getPrivateProperty("indicatorPaint")
        requireNotNull(indicatorPaint)
        verify(exactly = 1) { canvas.drawRect(left, top, right, bottom, indicatorPaint) }
    }

    @Test
    fun onDraw_whenViewFoundByLayoutManagerVisibleAndSelected_drawsIndicator() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val tabMinWidth: Int? = view.getPrivateProperty("tabMinWidth")
        requireNotNull(tabMinWidth)
        assertEquals(72, tabMinWidth)

        // set spy as layout manager
        val layoutManager = view.layoutManager
        requireNotNull(layoutManager)
        val layoutManagerSpy: RecyclerView.LayoutManager = spyk(layoutManager)
        view.setPrivateProperty("linearLayoutManager", layoutManagerSpy)

        // setup view returned by layout manager spy
        val positionView = mockk<View>()
        every { positionView.right }.returns(0)
        every { positionView.left }.returns(1080)
        every { positionView.measuredWidth }.returns(111)
        every { positionView.translationX }.returns(0.0f)
        every { layoutManagerSpy.findViewByPosition(0) }.returns(positionView)

        // set view size
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)
        assertEquals(positionView.left, view.measuredWidth)

        val hasSelectedView1: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView1)
        assertFalse(hasSelectedView1)

        // set current item
        view.setCurrentItem(0, false)

        // check
        val hasSelectedView2: Boolean? = view.getPrivateProperty("hasSelectedView")
        requireNotNull(hasSelectedView2)
        assertTrue(hasSelectedView2)

        // call onDraw
        val canvas = mockk<Canvas>()
        justRun {
            canvas.drawRect(any(), any(), any(), any(), any())
        }
        view.onDraw(canvas)

        verify(exactly = 2) { layoutManagerSpy.findViewByPosition(0) }

        val indicatorScroll: Int? = view.getPrivateProperty("indicatorScroll")
        requireNotNull(indicatorScroll)
        val currentIndicatorLeft: Float? = view.getPrivateProperty("currentIndicatorLeft")
        requireNotNull(currentIndicatorLeft)
        val left = currentIndicatorLeft - indicatorScroll + view.indicatorMarginLeft

        val currentIndicatorRight: Float? = view.getPrivateProperty("currentIndicatorRight")
        requireNotNull(currentIndicatorRight)
        val right = currentIndicatorRight - indicatorScroll - view.indicatorMarginRight

        val top = (view.height - view.indicatorHeight).toFloat()
        val bottom = view.height.toFloat()

        val indicatorPaint: Paint? = view.getPrivateProperty("indicatorPaint")
        requireNotNull(indicatorPaint)
        verify(exactly = 1) { canvas.drawRect(left, top, right, bottom, indicatorPaint) }
    }

    @Test
    fun onDetachedFromWindow_removesScrollListener() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        assertNotNull(view.getPrivateProperty("recyclerOnScrollListener"))

        // detach from window
        view.callPrivateFunc("onDetachedFromWindow")

        // check
        assertNull(view.getPrivateProperty("recyclerOnScrollListener"))
    }

    @Test
    fun recyclerOnScrollListener_whenOnScrolled() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        val listener: RecyclerView.OnScrollListener? =
            view.getPrivateProperty("recyclerOnScrollListener")
        requireNotNull(listener)
        val recyclerTabLayout: RecyclerTabLayout? =
            getPrivatePropertyGetterValue(listener, "recyclerTabLayout")
        requireNotNull(recyclerTabLayout)
        assertSame(view, recyclerTabLayout)

        // set spy view in listener
        val viewSpy = spyk(view)
        setPrivatePropertySetterValue(listener, "recyclerTabLayout", viewSpy)

        // check default value
        val dx1: Int? = getPrivatePropertyGetterValue(listener, "dx")
        requireNotNull(dx1)
        assertEquals(0, dx1)

        val indicatorScroll1: Int? = view.getPrivateProperty("indicatorScroll")
        requireNotNull(indicatorScroll1)
        assertEquals(0, indicatorScroll1)

        // execute
        listener.onScrolled(view, 1, 2)

        // check
        val dx2: Int? = getPrivatePropertyGetterValue(listener, "dx")
        requireNotNull(dx2)
        assertEquals(1, dx2)
        val indicatorScroll2: Int? = viewSpy.getPrivateProperty("indicatorScroll")
        requireNotNull(indicatorScroll2)
        assertEquals(1, indicatorScroll2)

        verify(exactly = 1) { viewSpy.invalidate() }
    }

    @Test
    fun recyclerOnScrollListener_whenAutoSelectIdleNewScrollStateZeroDxViewByPositionNotAvailableAndOnScrollStateChanged_selectsCenterForLeftScroll() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        // set view size
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)

        // enable auto select
        view.setAutoSelectionMode(true)

        val listener: RecyclerView.OnScrollListener? =
            view.getPrivateProperty("recyclerOnScrollListener")
        requireNotNull(listener)

        val linearLayoutManager: LinearLayoutManager? =
            getPrivatePropertyGetterValue(listener, "linearLayoutManager")
        requireNotNull(linearLayoutManager)

        val recyclerTabLayout: RecyclerTabLayout? =
            getPrivatePropertyGetterValue(listener, "recyclerTabLayout")
        requireNotNull(recyclerTabLayout)
        assertSame(view, recyclerTabLayout)

        // set spy view in listener
        val viewSpy = spyk(view)
        setPrivatePropertySetterValue(listener, "recyclerTabLayout", viewSpy)

        // set spy layout manager in listener
        val linearLayoutManagerSpy = spyk(linearLayoutManager)
        setPrivatePropertySetterValue(listener, "linearLayoutManager", linearLayoutManagerSpy)

        val dx1: Int? = getPrivatePropertyGetterValue(listener, "dx")
        requireNotNull(dx1)
        assertEquals(0, dx1)

        // execute
        listener.onScrollStateChanged(view, RecyclerView.SCROLL_STATE_IDLE)

        // check
        verify(exactly = 1) { linearLayoutManagerSpy.findFirstVisibleItemPosition() }
        verify(exactly = 1) { linearLayoutManagerSpy.findLastVisibleItemPosition() }
        verify(exactly = 1) { linearLayoutManagerSpy.findViewByPosition(any()) }
        verify(exactly = 0) { viewSpy.setCurrentItem(any(), any()) }
    }

    @Test
    fun recyclerOnScrollListener_whenAutoSelectIdleNewScrollStateZeroDxViewByPositionAvailableAndOnScrollStateChanged_selectsCenterForLeftScroll() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        // set view size
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)

        // enable auto select
        view.setAutoSelectionMode(true)

        val listener: RecyclerView.OnScrollListener? =
            view.getPrivateProperty("recyclerOnScrollListener")
        requireNotNull(listener)

        val linearLayoutManager: LinearLayoutManager? =
            getPrivatePropertyGetterValue(listener, "linearLayoutManager")
        requireNotNull(linearLayoutManager)

        val recyclerTabLayout: RecyclerTabLayout? =
            getPrivatePropertyGetterValue(listener, "recyclerTabLayout")
        requireNotNull(recyclerTabLayout)
        assertSame(view, recyclerTabLayout)

        // set spy view in listener
        val viewSpy = spyk(view)
        setPrivatePropertySetterValue(listener, "recyclerTabLayout", viewSpy)

        // set spy layout manager in listener
        val linearLayoutManagerSpy = spyk(linearLayoutManager)
        val positionView = mockk<View>()
        every { positionView.left }.returns(0)
        every { linearLayoutManagerSpy.findViewByPosition(any()) }.returns(positionView)
        setPrivatePropertySetterValue(listener, "linearLayoutManager", linearLayoutManagerSpy)

        val dx1: Int? = getPrivatePropertyGetterValue(listener, "dx")
        requireNotNull(dx1)
        assertEquals(0, dx1)

        // execute
        listener.onScrollStateChanged(view, RecyclerView.SCROLL_STATE_IDLE)

        // check
        verify(exactly = 1) { linearLayoutManagerSpy.findFirstVisibleItemPosition() }
        verify(exactly = 1) { linearLayoutManagerSpy.findLastVisibleItemPosition() }
        verify(exactly = 1) { linearLayoutManagerSpy.findViewByPosition(any()) }
        verify(exactly = 1) { viewSpy.setCurrentItem(any(), any()) }
    }

    @Test
    fun recyclerOnScrollListener_whenAutoSelectIdleNewScrollStateOneDxViewByPositionNotAvailableAndOnScrollStateChanged_selectsCenterForLeftScroll() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        // set view size
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)

        // enable auto select
        view.setAutoSelectionMode(true)

        val listener: RecyclerView.OnScrollListener? =
            view.getPrivateProperty("recyclerOnScrollListener")
        requireNotNull(listener)

        val linearLayoutManager: LinearLayoutManager? =
            getPrivatePropertyGetterValue(listener, "linearLayoutManager")
        requireNotNull(linearLayoutManager)

        val recyclerTabLayout: RecyclerTabLayout? =
            getPrivatePropertyGetterValue(listener, "recyclerTabLayout")
        requireNotNull(recyclerTabLayout)
        assertSame(view, recyclerTabLayout)

        // set spy view in listener
        val viewSpy = spyk(view)
        setPrivatePropertySetterValue(listener, "recyclerTabLayout", viewSpy)

        // set spy layout manager in listener
        val linearLayoutManagerSpy = spyk(linearLayoutManager)
        setPrivatePropertySetterValue(listener, "linearLayoutManager", linearLayoutManagerSpy)

        // set dx
        listener.onScrolled(view, 1, 0)

        // check
        val dx1: Int? = getPrivatePropertyGetterValue(listener, "dx")
        requireNotNull(dx1)
        assertEquals(1, dx1)

        // execute
        listener.onScrollStateChanged(view, RecyclerView.SCROLL_STATE_IDLE)

        // check
        verify(exactly = 1) { linearLayoutManagerSpy.findFirstVisibleItemPosition() }
        verify(exactly = 1) { linearLayoutManagerSpy.findLastVisibleItemPosition() }
        verify(exactly = 1) { linearLayoutManagerSpy.findViewByPosition(any()) }
        verify(exactly = 0) { viewSpy.setCurrentItem(any(), any()) }

        val dx2: Int? = getPrivatePropertyGetterValue(listener, "dx")
        requireNotNull(dx2)
        assertEquals(0, dx2)
    }

    @Test
    fun recyclerOnScrollListener_whenAutoSelectIdleNewScrollStateOneDxViewByPositionAvailableAndOnScrollStateChanged_selectsCenterForLeftScroll() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = RecyclerTabLayout(context)

        // set view size
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )

        assertEquals(1080, view.measuredWidth)

        // enable auto select
        view.setAutoSelectionMode(true)

        val listener: RecyclerView.OnScrollListener? =
            view.getPrivateProperty("recyclerOnScrollListener")
        requireNotNull(listener)

        val linearLayoutManager: LinearLayoutManager? =
            getPrivatePropertyGetterValue(listener, "linearLayoutManager")
        requireNotNull(linearLayoutManager)

        val recyclerTabLayout: RecyclerTabLayout? =
            getPrivatePropertyGetterValue(listener, "recyclerTabLayout")
        requireNotNull(recyclerTabLayout)
        assertSame(view, recyclerTabLayout)

        // set spy view in listener
        val viewSpy = spyk(view)
        setPrivatePropertySetterValue(listener, "recyclerTabLayout", viewSpy)

        // set spy layout manager in listener
        val linearLayoutManagerSpy = spyk(linearLayoutManager)
        val positionView = mockk<View>()
        every { positionView.left }.returns(view.width)
        every { positionView.width }.returns(10)
        every { linearLayoutManagerSpy.findViewByPosition(any()) }.returns(positionView)
        setPrivatePropertySetterValue(listener, "linearLayoutManager", linearLayoutManagerSpy)

        // set dx
        listener.onScrolled(view, 1, 0)

        // check
        val dx1: Int? = getPrivatePropertyGetterValue(listener, "dx")
        requireNotNull(dx1)
        assertEquals(1, dx1)

        // execute
        listener.onScrollStateChanged(view, RecyclerView.SCROLL_STATE_IDLE)

        // check
        verify(exactly = 1) { linearLayoutManagerSpy.findFirstVisibleItemPosition() }
        verify(exactly = 1) { linearLayoutManagerSpy.findLastVisibleItemPosition() }
        verify(exactly = 1) { linearLayoutManagerSpy.findViewByPosition(any()) }
        verify(exactly = 1) { viewSpy.setCurrentItem(any(), any()) }

        val dx2: Int? = getPrivatePropertyGetterValue(listener, "dx")
        requireNotNull(dx2)
        assertEquals(0, dx2)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any, R> getPrivatePropertyGetterValue(instance: T, name: String): R? {
        return instance::class.memberProperties
            .firstOrNull { it.name == name }
            ?.apply { isAccessible = true }
            ?.getter?.call(instance) as? R
    }

    private fun <T : Any, R> setPrivatePropertySetterValue(instance: T, name: String, value: R?) {
        val property = instance::class.memberProperties.find { it.name == name }
        if (property is KMutableProperty<*>) {
            property.isAccessible = true
            property.setter.call(instance, value)
        } else {
            property?.isAccessible = true
            property?.javaField?.isAccessible = true
            property?.javaField?.set(instance, value)
        }
    }
}