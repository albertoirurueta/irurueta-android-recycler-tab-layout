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

package com.irurueta.android.recyclertablayout

import android.view.View
import android.view.View.LAYOUT_DIRECTION_RTL

/**
 * Helper class to simplify view operations.
 */
internal object ViewHelper {

    /**
     * Determines if a view is in right-to-left mode.
     *
     * @param view view to check.
     * @return true if view is in right-to-left mode, false otherwise.
     */
    fun isRtl(view: View): Boolean {
        return view.layoutDirection == LAYOUT_DIRECTION_RTL
    }
}