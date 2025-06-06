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

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * Custom view for a tab.
 *
 * @param context context.
 * @param attrs attributes.
 * @param defStyleAttr default style attribute.
 */
class TabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    /**
     * Tab name view.
     */
    private var tabNameView: TextView? = null

    /**
     * Tab name.
     */
    var tabName: String?
        get() = tabNameView?.text?.toString()
        set(value) {
            tabNameView?.text = value
        }

    /**
     * Called when the view is created.
     */
    init {
        // Load layout
        LayoutInflater.from(context).inflate(R.layout.tab_view, this)

        tabNameView = findViewById(R.id.tab_name)
    }
}