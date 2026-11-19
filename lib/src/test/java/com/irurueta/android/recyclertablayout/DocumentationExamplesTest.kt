/*
 * Copyright (C) 2026 Alberto Irurueta Carro (alberto@irurueta.com)
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

import android.content.Context
import android.graphics.Color
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.viewpager2.widget.ViewPager2
import com.irurueta.android.testutils.getPrivateProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.io.File

/**
 * Compiled versions of the code examples shown in the documentation page `usage.adoc`.
 * Each snippet is delimited by `tag::name[]` and `end::name[]` comments, and the documentation
 * shows exactly the lines between them (copied to `docs/modules/ROOT/examples/<name>.kt`).
 *
 * Variables used by a snippet but not declared in it (`context`, `container`,
 * `recyclerTabLayout`, ...) are the ones a real application would already have, and are declared
 * before its tag.
 */
@RunWith(RobolectricTestRunner::class)
class DocumentationExamplesTest {

    /**
     * Android context used to create views.
     */
    private lateinit var context: Context

    /**
     * Parent view the tab layout is added to.
     */
    private lateinit var container: FrameLayout

    /**
     * Tab layout used by the examples that need an existing view.
     */
    private lateinit var recyclerTabLayout: RecyclerTabLayout

    /**
     * Creates the context, the container and a tab layout before each test.
     */
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        container = FrameLayout(context)
        recyclerTabLayout = RecyclerTabLayout(context)
    }

    /**
     * Checks the example creating the view programmatically.
     */
    @Test
    fun constructor_whenCreatedInCode_isAddedToContainer() {
        // tag::createInCode[]
        val recyclerTabLayout = RecyclerTabLayout(context)
        recyclerTabLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        container.addView(recyclerTabLayout)
        // end::createInCode[]

        assertEquals(1, container.childCount)
        assertSame(recyclerTabLayout, container.getChildAt(0))
    }

    /**
     * Checks the example setting an adapter with a large number of tabs.
     */
    @Test
    fun adapter_withLargeNumberOfTabs_onlyCreatesVisibleTabViews() {
        // tag::setAdapter[]
        // 10,000 tabs: only the visible tab views are created, and they are recycled on scroll
        val titles = List(10_000) { "Tab $it" }
        recyclerTabLayout.adapter = TabAdapter(titles) { position ->
            recyclerTabLayout.setCurrentItem(position)
        }
        // end::setAdapter[]

        layout(recyclerTabLayout)

        assertEquals(TAB_COUNT, recyclerTabLayout.adapter?.itemCount)
        assertTrue(recyclerTabLayout.childCount > 0)
        assertTrue(recyclerTabLayout.childCount < TAB_COUNT)
    }

    /**
     * Checks that clicking a tab created by the documented adapter selects it.
     */
    @Test
    fun adapter_whenTabIsClicked_selectsClickedTab() {
        val titles = List(TAB_COUNT) { "Tab $it" }
        recyclerTabLayout.adapter = TabAdapter(titles) { position ->
            recyclerTabLayout.setCurrentItem(position, smoothScroll = false)
        }
        layout(recyclerTabLayout)

        val tab: View = recyclerTabLayout.getChildAt(2)
        tab.performClick()

        assertEquals(2, indicatorPosition())
    }

    /**
     * Checks the example customizing the selection indicator.
     */
    @Test
    fun indicator_whenCustomized_keepsValues() {
        // tag::customizeIndicator[]
        val density = recyclerTabLayout.resources.displayMetrics.density
        // ARGB color, for instance ContextCompat.getColor(context, R.color.accent)
        recyclerTabLayout.indicatorColor = Color.RED
        recyclerTabLayout.indicatorHeight = (3 * density).toInt()
        recyclerTabLayout.indicatorMarginLeft = (8 * density).toInt()
        recyclerTabLayout.indicatorMarginRight = (8 * density).toInt()
        recyclerTabLayout.invalidate()
        // end::customizeIndicator[]

        assertEquals(Color.RED, recyclerTabLayout.indicatorColor)
        assertEquals((3 * density).toInt(), recyclerTabLayout.indicatorHeight)
        assertEquals((8 * density).toInt(), recyclerTabLayout.indicatorMarginLeft)
        assertEquals((8 * density).toInt(), recyclerTabLayout.indicatorMarginRight)
    }

    /**
     * Checks the example selecting a tab programmatically.
     */
    @Test
    fun setCurrentItem_withAndWithoutAnimation_selectsTab() {
        recyclerTabLayout.adapter = TabAdapter(List(TAB_COUNT) { "Tab $it" }) {}
        layout(recyclerTabLayout)

        // tag::setCurrentItem[]
        // scroll to tab 5 and animate the indicator towards it
        recyclerTabLayout.setCurrentItem(5)

        // select tab 5 immediately, without animation (for instance when restoring state)
        recyclerTabLayout.setCurrentItem(5, smoothScroll = false)
        // end::setCurrentItem[]

        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(5, indicatorPosition())
    }

    /**
     * Checks the example enabling automatic selection.
     */
    @Test
    fun setAutoSelectionMode_whenEnabled_autoSelects() {
        // tag::autoSelection[]
        // when a scroll gesture stops between two tabs, select the tab closest to the center
        recyclerTabLayout.setAutoSelectionMode(true)
        // end::autoSelection[]

        val autoSelect: Boolean? = recyclerTabLayout.getPrivateProperty("autoSelect")
        assertEquals(true, autoSelect)
    }

    /**
     * Checks the example keeping the tab layout in sync with a [ViewPager2].
     */
    @Test
    fun viewPager_whenPageChanges_selectsMatchingTab() {
        val titles = List(TAB_COUNT) { "Tab $it" }
        val viewPager = ViewPager2(context)
        // pages would normally be fragments or views; any RecyclerView adapter works here
        viewPager.adapter = TabAdapter(titles) {}

        // tag::viewPager[]
        // selecting a page selects the matching tab
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                recyclerTabLayout.setCurrentItem(position)
            }
        })

        // clicking a tab shows the matching page
        recyclerTabLayout.adapter = TabAdapter(titles) { position ->
            viewPager.currentItem = position
        }
        // end::viewPager[]

        layout(recyclerTabLayout)
        viewPager.setCurrentItem(3, false)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(3, indicatorPosition())

        recyclerTabLayout.getChildAt(1).performClick()
        assertEquals(1, viewPager.currentItem)
    }

    /**
     * Checks that every example published in the documentation matches a tagged region of this
     * test, so that documented code always compiles and runs.
     */
    @Test
    fun examples_inDocumentation_matchTaggedRegionsOfThisTest() {
        val lines = File(TEST_SOURCE_PATH).readLines()
        val starts = lines.indices.filter { lines[it].trim().startsWith(TAG_PREFIX) }
        assertTrue(starts.isNotEmpty())

        for (start in starts) {
            val name = lines[start].trim().removePrefix(TAG_PREFIX).removeSuffix(TAG_SUFFIX)
            val end = (start + 1 until lines.size).first {
                lines[it].trim() == "$END_PREFIX$name$TAG_SUFFIX"
            }
            val expected = lines.subList(start + 1, end).joinToString("\n").trimIndent().trim()

            val example = File(EXAMPLES_PATH, "$name.kt")
            assertTrue("missing example ${example.path}", example.isFile)
            assertEquals("example $name is out of date", expected, example.readText().trim())
        }

        // every published example comes from a tagged region
        val published = File(EXAMPLES_PATH).listFiles { file -> file.extension == "kt" }
        assertEquals(starts.size, published?.size)
    }

    /**
     * Measures and lays out provided view so that its visible tab views are created.
     *
     * @param view view to lay out.
     */
    private fun layout(view: View) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(VIEW_WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(VIEW_HEIGHT, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, VIEW_WIDTH, VIEW_HEIGHT)
    }

    /**
     * Gets the position of the currently selected tab.
     *
     * @return position where the indicator is drawn.
     */
    private fun indicatorPosition(): Int? = recyclerTabLayout.getPrivateProperty("indicatorPosition")

    // tag::tabAdapter[]
    class TabViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    class TabAdapter(
        private val titles: List<String>,
        private val onTabClick: (position: Int) -> Unit
    ) : RecyclerView.Adapter<TabViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
            val density = parent.resources.displayMetrics.density
            val textView = TextView(parent.context)
            textView.gravity = Gravity.CENTER
            textView.minWidth = (72 * density).toInt()
            textView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            return TabViewHolder(textView)
        }

        override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
            holder.textView.text = titles[position]
            holder.textView.setOnClickListener {
                val clickedPosition = holder.bindingAdapterPosition
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    onTabClick(clickedPosition)
                }
            }
        }

        override fun getItemCount(): Int = titles.size
    }
    // end::tabAdapter[]

    private companion object {
        /**
         * Path of this file, relative to the `lib` module, where unit tests run.
         */
        const val TEST_SOURCE_PATH =
            "src/test/java/com/irurueta/android/recyclertablayout/DocumentationExamplesTest.kt"

        /**
         * Path of the Antora examples directory, relative to the `lib` module.
         */
        const val EXAMPLES_PATH = "../docs/modules/ROOT/examples"

        /**
         * Prefix of the comment starting a tagged region.
         */
        const val TAG_PREFIX = "// tag::"

        /**
         * Prefix of the comment ending a tagged region.
         */
        const val END_PREFIX = "// end::"

        /**
         * Suffix of the comments starting and ending a tagged region.
         */
        const val TAG_SUFFIX = "[]"

        /**
         * Number of tabs used by the examples.
         */
        const val TAB_COUNT = 10_000

        /**
         * Width of the laid out tab layout, in pixels.
         */
        const val VIEW_WIDTH = 1080

        /**
         * Height of the laid out tab layout, in pixels.
         */
        const val VIEW_HEIGHT = 144
    }
}
