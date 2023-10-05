package com.github.joaopedrobraz.intellijadonis.parsing

import com.github.joaopedrobraz.intellijadonis.AdonisBundle
import com.github.joaopedrobraz.intellijadonis.AdonisBundle.BUNDLE
import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.PropertyKey

class EdgeElementType(
    private val debugName: String,
    @PropertyKey(resourceBundle = BUNDLE) private val expectedMessageKey: String
) :
    IElementType(debugName, EdgeLanguage.INSTANCE) {

    private var params: Array<String> = arrayOf()

    constructor(
        debugName: String,
        @PropertyKey(resourceBundle = BUNDLE) expectedMessageKey: String,
        params: Array<String>
    ) : this(
        debugName,
        expectedMessageKey
    ) {
        this.params = params
    }

    override fun toString() = "[Edge] ${super.toString()}"

    override fun equals(other: Any?): Boolean {
        if (other !is EdgeElementType) return false
        return debugName === other.debugName
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + debugName.hashCode()
        return result
    }

    fun parseExpectedMessage(): String =
        AdonisBundle.message(expectedMessageKey, *params)

    fun with(params: Array<String>) = EdgeElementType(debugName, expectedMessageKey, params)
}