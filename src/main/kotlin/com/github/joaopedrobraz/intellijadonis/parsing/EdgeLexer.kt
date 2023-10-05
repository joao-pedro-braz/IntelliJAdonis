package com.github.joaopedrobraz.intellijadonis.parsing

import com.intellij.lexer.MergeFunction
import com.intellij.lexer.MergingLexerAdapterBase

class EdgeLexer : MergingLexerAdapterBase(EdgeRawLexer()) {
    companion object {
        private val MERGE_FUNCTION = MergeFunction { type, _ ->
            type
        }
    }

    override fun getMergeFunction() = MERGE_FUNCTION
}