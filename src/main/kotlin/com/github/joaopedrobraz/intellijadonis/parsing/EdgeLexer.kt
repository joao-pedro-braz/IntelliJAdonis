package com.github.joaopedrobraz.intellijadonis.parsing

import com.intellij.lexer.MergeFunction
import com.intellij.lexer.MergingLexerAdapterBase

class EdgeLexer : MergingLexerAdapterBase(EdgeRawLexer()) {
    companion object {
        private val MERGE_FUNCTION = MergeFunction { type, originalLexer ->
//            if (EdgeTokenTypes.COMMENT_OPEN !== type) {
//                return@MergeFunction type
//            }
//            if (originalLexer.tokenType === EdgeTokenTypes.COMMENT_CONTENT) {
//                originalLexer.advance()
//            }
//            if (originalLexer.tokenType === EdgeTokenTypes.COMMENT_CLOSE) {
//                originalLexer.advance()
//                return@MergeFunction EdgeTokenTypes.COMMENT
//            }
//            if (originalLexer.tokenType == null) {
//                return@MergeFunction EdgeTokenTypes.UNCLOSED_COMMENT
//            }
//            if (originalLexer.tokenType === EdgeTokenTypes.UNCLOSED_COMMENT) {
//                originalLexer.advance()
//                return@MergeFunction EdgeTokenTypes.UNCLOSED_COMMENT
//            }
            type
        }
    }

    override fun getMergeFunction() = MERGE_FUNCTION
}