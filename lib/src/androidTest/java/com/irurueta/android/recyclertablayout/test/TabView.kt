package com.irurueta.android.recyclertablayout.test

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView

class TabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var tabNameView: TextView? = null

    var tabName: String?
        get() = tabNameView?.text?.toString()
        set(value) {
            tabNameView?.text = value
        }

    init {
        // Load layout
        LayoutInflater.from(context).inflate(R.layout.tab_view, this)

        tabNameView = findViewById(R.id.tab_name)
    }
}