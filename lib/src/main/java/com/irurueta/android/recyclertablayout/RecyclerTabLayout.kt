package com.irurueta.android.recyclertablayout

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlin.math.abs

/**
 * Implementation of a tab layout using a recycler view.
 * This view shows the indicator like tab layout with the corresponding
 * animations, however, views are populated like in any recycler view, allowing
 * view recycling and a much larger number of views. Hence, this implementation
 * is more memory efficient than [TabLayout] for large number of views.
 */
class RecyclerTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    /**
     * Paint to draw indicator.
     */
    private val indicatorPaint: Paint = Paint()

    /**
     * Indicates whether indicator minimum length displayed on screen is enabled or not.
     * When true, [tabMinWidth] is taken into account, otherwise it is ignored.
     */
    private var tabOnScreenLimitEnabled: Boolean = false

    /**
     * Minimum width of displayed indicator expressed in pixels.
     */
    private var tabMinWidth = 0

    /**
     * Layout manager used by recycler view used internally.
     */
    private var linearLayoutManager: LinearLayoutManager? = null

    /**
     * Scroll listener used internally to update location of indicator.
     */
    private var recyclerOnScrollListener: RecyclerOnScrollListener? = null

    /**
     * Item position where indicator must be displayed.
     */
    private var indicatorPosition = 0

    /**
     * Current scroll on indicator.
     */
    private var indicatorScroll = 0

    /**
     * Previous position where indicator was placed.
     */
    private var oldPosition = 0

    /**
     * Previous scroll offset.
     */
    private var oldScrollOffset = 0

    /**
     * Previous position offset.
     */
    private var oldPositionOffset = 0.0f

    /**
     * Indicates whether view can scroll horizontally or not.
     */
    private var scrollEnabled = false

    /**
     * Left position of item indicator when animation starts.
     */
    private var startIndicatorLeft = 0.0f

    /**
     * Right position of item indicator when animation starts.
     */
    private var startIndicatorRight = 0.0f

    /**
     * Current left position of item indicator during animation.
     */
    private var currentIndicatorLeft = 0.0f

    /**
     * Current right position of item indicator during animation.
     */
    private var currentIndicatorRight = 0.0f

    /**
     * Left position of item indicator when animation ends.
     */
    private var endIndicatorLeft = 0.0f

    /**
     * Right position of item indicator when animation ends.
     */
    private var endIndicatorRight = 0.0f

    /**
     * Indicates whether selected view could be found or not.
     * If not found, position will be searched again during the [onDraw] phase.
     */
    private var hasSelectedView = false

    /**
     * Animator to move item indicator.
     */
    private var animator: ValueAnimator? = null

    /**
     * Indicates whether item should be auto-selected when scroll is between two items.
     */
    private var autoSelect = false

    /**
     * Gets or sets color to be used for item indicator.
     * Provided or returned color is a 32 bit integer in ARGB format.
     */
    var indicatorColor: Int
        get() {
            return indicatorPaint.color
        }
        set(value) {
            indicatorPaint.color = value
        }

    /**
     * Number of pixels of indicator height.
     */
    var indicatorHeight = 0

    /**
     * Left margin on indicator expressed in pixels.
     */
    var indicatorMarginLeft = 0

    /**
     * Right margin on indicator expressed in pixels.
     */
    var indicatorMarginRight = 0

    /**
     * Indicates whether items are auto-selected to the closest item when scroll finishes
     * between two items.
     *
     * @param autoSelect true to enable auto-select, false otherwise.
     */
    fun setAutoSelectionMode(autoSelect: Boolean) {
        var recyclerOnScrollListener = this.recyclerOnScrollListener
        if (recyclerOnScrollListener != null) {
            removeOnScrollListener(recyclerOnScrollListener)
            this.recyclerOnScrollListener = null
        }

        this.autoSelect = autoSelect
        recyclerOnScrollListener = RecyclerOnScrollListener(this, linearLayoutManager)
        this.recyclerOnScrollListener = recyclerOnScrollListener
        addOnScrollListener(recyclerOnScrollListener)
    }

    /**
     * Sets currently selected item.
     *
     * @param position position of item to select and scroll to.
     * @param smoothScroll true to scroll and move item indicator with an animation,
     * false otherwise.
     */
    fun setCurrentItem(position: Int, smoothScroll: Boolean) {
        if (smoothScroll && position != indicatorPosition) {
            startAnimation(position)
        } else {
            scrollTo(position)
        }
    }

    /**
     * Called when view needs to be drawn.
     * This implementation takes care to draw item indicator at selected position.
     *
     * @param canvas canvas to draw view into.
     */
    override fun onDraw(canvas: Canvas?) {
        val view = linearLayoutManager?.findViewByPosition(indicatorPosition) ?: return

        if (view.right < 0 || view.left > measuredWidth) {
            return
        }

        var left: Float
        var right: Float
        if (hasSelectedView) {
            left = currentIndicatorLeft - indicatorScroll
            right = currentIndicatorRight - indicatorScroll
        } else {
            endIndicatorLeft = view.left.toFloat()
            startIndicatorLeft = endIndicatorLeft
            left = startIndicatorLeft

            endIndicatorRight = view.right.toFloat()
            startIndicatorRight = endIndicatorRight
            right = startIndicatorRight
        }

        left += indicatorMarginLeft
        right -= indicatorMarginRight

        val top = (height - indicatorHeight).toFloat()
        val bottom = height.toFloat()

        canvas?.drawRect(left, top, right, bottom, indicatorPaint)
    }

    /**
     * Called when view is detached from window.
     */
    override fun onDetachedFromWindow() {
        val recyclerOnScrollListener = this.recyclerOnScrollListener
        if (recyclerOnScrollListener != null) {
            removeOnScrollListener(recyclerOnScrollListener)
            this.recyclerOnScrollListener = null
        }
        super.onDetachedFromWindow()
    }

    /**
     * Initializes view.
     * @param context Android context.
     * @param attrs XML layout attributes.
     * @param defStyleAttr default style to use.
     */
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        setWillNotDraw(false)
        getAttributes(context, attrs, defStyleAttr)

        val linearLayoutManager = object : LinearLayoutManager(context) {
            override fun canScrollHorizontally(): Boolean {
                return scrollEnabled
            }
        }
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        this.linearLayoutManager = linearLayoutManager
        this.layoutManager = linearLayoutManager
        itemAnimator = null

        setAutoSelectionMode(false)
    }

    /**
     * Gets XML layout attributes.
     *
     * @param context Android context.
     * @param attrs XML layout attributes.
     * @param defStyleAttr style to be used.
     */
    private fun getAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.RecyclerTabLayout, defStyleAttr, R.style.RecyclerTabLayout
        )
        indicatorColor =
            a.getColor(R.styleable.RecyclerTabLayout_tabIndicatorColor, Color.TRANSPARENT)
        indicatorHeight =
            a.getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabIndicatorHeight, 0)

        tabOnScreenLimitEnabled =
            a.getBoolean(R.styleable.RecyclerTabLayout_tabOnScreenLimitEnabled, true)
        if (tabOnScreenLimitEnabled) {
            tabMinWidth = a.getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabMinWidth, 0)
        }

        scrollEnabled = a.getBoolean(R.styleable.RecyclerTabLayout_scrollEnabled, true)

        val indicatorMarginStart =
            a.getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabIndicatorMarginStart, 0)
        val indicatorMarginEnd =
            a.getDimensionPixelSize(R.styleable.RecyclerTabLayout_tabIndicatorMarginEnd, 0)

        if (isLayoutRtl()) {
            indicatorMarginLeft = indicatorMarginEnd
            indicatorMarginRight = indicatorMarginStart
        } else {
            indicatorMarginLeft = indicatorMarginStart
            indicatorMarginRight = indicatorMarginEnd
        }

        a.recycle()
    }

    /**
     * Indicates whether layout direction is Right-to-left or not.
     *
     * @return true indicates that layout direction i Right-to-left, false otherwise.
     */
    private fun isLayoutRtl(): Boolean {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    /**
     * Starts animation to scroll and select item on provided position.
     *
     * @param position position to scroll and move item indicator.
     */
    private fun startAnimation(position: Int) {
        var distance = 1.0f

        val view = linearLayoutManager?.findViewByPosition(position)
        if (view != null) {
            val currentX = view.x + view.measuredWidth / 2.0f
            val centerX = measuredWidth / 2.0f
            distance = abs(centerX - currentX) / view.measuredWidth
        }

        var animator = this.animator
        if (animator != null && animator.isRunning) {
            animator.cancel()
        }

        animator = if (position < indicatorPosition) {
            ValueAnimator.ofFloat(distance, 0.0f)
        } else {
            ValueAnimator.ofFloat(-distance, 0.0f)
        }
        this.animator = animator
        animator.duration = DEFAULT_SCROLL_DURATION_MILLIS
        animator.addUpdateListener { animation ->
            scrollTo(position, animation.animatedValue as Float, animation.animatedFraction)
        }
        animator.start()
    }

    /**
     * Scrolls and moves item indicator to provided position with provided scroll position
     * offset to move recycler view, and fraction to move item indicator.
     *
     * Default positionOffset and fraction values can be used to scroll and move indicator
     * to provided position without an animation.
     *
     * @param position position of item to move to.
     * @param positionOffset offset of scroll to scroll recycler view to.
     * @param fraction fraction to move item indicator to selected position.
     */
    private fun scrollTo(position: Int, positionOffset: Float = 0.0f, fraction: Float = 1.0f) {
        var scrollOffset = 0

        val selectedView = linearLayoutManager?.findViewByPosition(position)
        val nextView = linearLayoutManager?.findViewByPosition(position + 1)

        if (selectedView != null) {
            hasSelectedView = true

            val width = measuredWidth
            // left edge of selected tab
            val sLeft = if (position == 0) {
                0.0f
            } else {
                width / 2.0f - selectedView.measuredWidth
            }
            // right edge of selected tab
            val sRight = sLeft + selectedView.measuredWidth

            val transX = selectedView.translationX
            endIndicatorLeft = selectedView.left + transX
            endIndicatorRight = selectedView.right + transX

            scrollOffset = if (nextView != null) {
                // left edge of next tab
                val nLeft = (width - nextView.measuredWidth) / 2.0f
                // total distance that is needed to distance to next tab
                val distance = sRight - nLeft
                val dx = distance * positionOffset
                (sLeft - dx).toInt()
            } else {
                sLeft.toInt()
            }
            val oneMinusFraction = 1.0f - fraction
            currentIndicatorLeft =
                startIndicatorLeft * oneMinusFraction + fraction * endIndicatorLeft
            currentIndicatorRight =
                startIndicatorRight * oneMinusFraction + fraction * endIndicatorRight
            indicatorScroll = 0
        } else {
            hasSelectedView = false

            if (measuredWidth > 0 && tabMinWidth > 0) {
                val width = tabMinWidth
                val offset = -width * positionOffset
                val leftOffset = (measuredWidth - width) / 2.0f
                scrollOffset = (offset + leftOffset).toInt()
            }
        }

        if (fraction == 1.0f) {
            startIndicatorLeft = endIndicatorLeft
            startIndicatorRight = endIndicatorRight
        }

        indicatorPosition = position

        stopScroll()

        if (position != oldPosition || scrollOffset != oldScrollOffset) {
            linearLayoutManager?.scrollToPositionWithOffset(position, scrollOffset)
        }
        if (indicatorHeight > 0) {
            invalidate()
        }

        oldPosition = position
        oldScrollOffset = scrollOffset
        oldPositionOffset = positionOffset
    }

    init {
        init(context, attrs, defStyleAttr)
    }

    companion object {
        /**
         * Duration of the scroll animation expressed in milliseconds.
         */
        const val DEFAULT_SCROLL_DURATION_MILLIS = 200L
    }

    /**
     * Scroll listener for internal recycler view.
     * This implementation listens for changes in scroll to appropriately move item
     * indicator.
     *
     * @param recyclerTabLayout reference to [RecyclerTabLayout].
     * @param linearLayoutManager reference to layout manager.
     */
    private class RecyclerOnScrollListener(
        private val recyclerTabLayout: RecyclerTabLayout,
        private val linearLayoutManager: LinearLayoutManager?
    ) : RecyclerView.OnScrollListener() {

        /**
         * Amount of accumulated scroll on a single scroll gesture.
         */
        private var dx = 0

        /**
         * Called when recycler view scrolls.
         *
         * @param recyclerView reference to recycler view.
         * @param dx amount of horizontal scroll that has occurred.
         * @param dy amount of vertical scroll that has occurred.
         */
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            this.dx += dx
            recyclerTabLayout.indicatorScroll += dx
            recyclerTabLayout.invalidate()
        }

        /**
         * Called when scroll state changes (starts, settles or ends).
         *
         * @param recyclerView reference to recycler view.
         * @param newState new scroll state.
         */
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == SCROLL_STATE_IDLE && recyclerTabLayout.autoSelect) {
                if (dx > 0) {
                    selectCenterForRightScroll()
                } else {
                    selectCenterForLeftScroll()
                }
                dx = 0
            }
        }

        /**
         * Select closest item at right.
         */
        private fun selectCenterForRightScroll() {
            val first = linearLayoutManager?.findFirstVisibleItemPosition() ?: return
            val last = linearLayoutManager.findLastVisibleItemPosition()
            val center = recyclerTabLayout.width / 2
            for (position in first..last) {
                val view = linearLayoutManager.findViewByPosition(position)
                if (view != null && (view.left + view.width >= center)) {
                    recyclerTabLayout.setCurrentItem(position, false)
                    break
                }
            }
        }

        /**
         * Selects closest item at left.
         */
        private fun selectCenterForLeftScroll() {
            val first = linearLayoutManager?.findFirstVisibleItemPosition() ?: return
            val last = linearLayoutManager.findLastVisibleItemPosition()
            val center = recyclerTabLayout.width / 2
            for (position in last..first step -1) {
                val view = linearLayoutManager.findViewByPosition(position)
                if (view != null && (view.left <= center)) {
                    recyclerTabLayout.setCurrentItem(position, false)
                    break
                }
            }
        }
    }
}