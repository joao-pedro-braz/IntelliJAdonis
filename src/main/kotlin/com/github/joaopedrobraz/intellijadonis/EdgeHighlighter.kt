package com.github.joaopedrobraz.intellijadonis

import com.github.joaopedrobraz.intellijadonis.EdgeHighlighter.Attributes.TOKEN_HIGHLIGHTS
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeRawLexer
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes
import com.intellij.lang.javascript.highlighting.JavaScriptHighlightDescriptor
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class EdgeHighlighter : SyntaxHighlighterBase() {
    private object Attributes {
        val CURLY_BRACES = TextAttributesKey.createTextAttributesKey(
            "EDGE.BRACES",
            JavaScriptHighlightDescriptor.BRACES.orCreateTextAttributesKey,
        )

        val PARENTHESES = TextAttributesKey.createTextAttributesKey(
            "EDGE.PARENTHESES",
            JavaScriptHighlightDescriptor.PARENTHESES.orCreateTextAttributesKey,
        )

        val TAG_IDENTIFIER = TextAttributesKey.createTextAttributesKey(
            "EDGE.TAG_IDENTIFIER",
            JavaScriptHighlightDescriptor.GLOBAL_FUNCTION.orCreateTextAttributesKey,
        )

        val TAG = TextAttributesKey.createTextAttributesKey(
            "EDGE.TAG",
            JavaScriptHighlightDescriptor.DECORATOR.orCreateTextAttributesKey,
        )

        val ESCAPED = TextAttributesKey.createTextAttributesKey(
            "EDGE.ESCAPED",
            JavaScriptHighlightDescriptor.LINE_COMMENT.orCreateTextAttributesKey,
        )

        val CONTENT = TextAttributesKey.createTextAttributesKey(
            "EDGE.CONTENT",
            DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR
        )

        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "EDGE.COMMENT",
            JavaScriptHighlightDescriptor.BLOCK_COMMENT.orCreateTextAttributesKey
        )

        val TOKEN_HIGHLIGHTS: Map<IElementType, TextAttributesKey> = mapOf(
            Pair(EdgeTokenTypes.TAG, TAG),
            Pair(EdgeTokenTypes.ESCAPED_TAG, ESCAPED),
            Pair(EdgeTokenTypes.SELF_CLOSING_TAG, TAG),
            Pair(EdgeTokenTypes.OPEN_PARENTHESES, PARENTHESES),
            Pair(EdgeTokenTypes.CLOSE_PARENTHESES, PARENTHESES),
            Pair(EdgeTokenTypes.OPEN_CURLY_BRACES, CURLY_BRACES),
            Pair(EdgeTokenTypes.ESCAPED_OPEN_CURLY_BRACES, ESCAPED),
            Pair(EdgeTokenTypes.OPEN_RAW_CURLY_BRACES, CURLY_BRACES),
            Pair(EdgeTokenTypes.ESCAPED_OPEN_RAW_CURLY_BRACES, ESCAPED),
            Pair(EdgeTokenTypes.CLOSE_CURLY_BRACES, CURLY_BRACES),
            Pair(EdgeTokenTypes.CLOSE_RAW_CURLY_BRACES, CURLY_BRACES),
            Pair(EdgeTokenTypes.IDENTIFIER, TAG_IDENTIFIER),
            Pair(EdgeTokenTypes.JAVASCRIPT_CONTENT, CONTENT),
            Pair(EdgeTokenTypes.HTML_CONTENT, CONTENT),
            Pair(EdgeTokenTypes.COMMENT, COMMENT),
            // TODO: Is this really needed?
            Pair(EdgeTokenTypes.OPEN_COMMENT, COMMENT),
            Pair(EdgeTokenTypes.COMMENT_CONTENT, COMMENT),
            Pair(EdgeTokenTypes.CLOSE_COMMENT, COMMENT),
        )
    }

    override fun getHighlightingLexer() = EdgeRawLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        if (TOKEN_HIGHLIGHTS.containsKey(tokenType)) {
            arrayOf(TOKEN_HIGHLIGHTS[tokenType]!!)
        } else {
            thisLogger().debug("Tried highlighting unknown token")
            arrayOf()
        }
}