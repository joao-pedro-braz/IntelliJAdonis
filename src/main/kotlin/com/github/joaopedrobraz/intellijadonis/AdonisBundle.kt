package com.github.joaopedrobraz.intellijadonis

import com.intellij.DynamicBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

private const val MY_BUNDLE: @NonNls String = "messages.AdonisBundle"

object AdonisBundle : DynamicBundle(MY_BUNDLE) {
    const val BUNDLE: @NonNls String = MY_BUNDLE

    @JvmStatic
    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getMessage(key, *params)

    @Suppress("unused")
    @JvmStatic
    fun messagePointer(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) =
        getLazyMessage(key, *params)
}

