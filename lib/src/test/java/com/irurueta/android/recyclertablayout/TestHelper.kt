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

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

/**
 * Calls a private function on a class.
 *
 * @param name name of the function to call.
 * @param args arguments to pass to the function.
 * @return result of the function call.
 */
inline fun <reified T : Any> T.callPrivateFunc(name: String, vararg args: Any?): Any? =
    T::class.declaredMemberFunctions
        .firstOrNull { it.name == name }
        ?.apply { isAccessible = true }
        ?.call(this, *args)

/**
 * Gets a private property from a class.
 *
 * @param name name of the property to get.
 * @return value of the property.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any, R> T.getPrivateProperty(name: String): R? =
    T::class.memberProperties
        .firstOrNull { it.name == name }
        ?.apply { isAccessible = true }
        ?.get(this) as? R

/**
 * Sets a private property on a class.
 *
 * @param name name of the property to set.
 */
inline fun <reified T : Any, R> T.setPrivateProperty(name: String, value: R?) {
    val property = T::class.memberProperties.find { it.name == name }
    if (property is KMutableProperty<*>) {
        property.isAccessible = true
        property.setter.call(this, value)
    } else {
        property?.isAccessible = true
        property?.javaField?.isAccessible = true
        property?.javaField?.set(this, value)
    }
}
