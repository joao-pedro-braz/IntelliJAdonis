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
    object Attributes {
        private val CURLY_BRACES = TextAttributesKey.createTextAttributesKey(
            "EDGE.BRACES",
            JavaScriptHighlightDescriptor.BRACES.orCreateTextAttributesKey,
        )

        private val PARENTHESES = TextAttributesKey.createTextAttributesKey(
            "EDGE.PARENTHESES",
            JavaScriptHighlightDescriptor.PARENTHESES.orCreateTextAttributesKey,
        )

        private val TAG_IDENTIFIER = TextAttributesKey.createTextAttributesKey(
            "EDGE.TAG_IDENTIFIER",
            JavaScriptHighlightDescriptor.GLOBAL_FUNCTION.orCreateTextAttributesKey,
        )

        private val TAG = TextAttributesKey.createTextAttributesKey(
            "EDGE.TAG",
            JavaScriptHighlightDescriptor.DECORATOR.orCreateTextAttributesKey,
        )

        private val CONTENT = TextAttributesKey.createTextAttributesKey(
            "EDGE.CONTENT",
            DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR
        )

        private val COMMENT = TextAttributesKey.createTextAttributesKey(
            "EDGE.COMMENT",
            JavaScriptHighlightDescriptor.BLOCK_COMMENT.orCreateTextAttributesKey
        )

        val TOKEN_HIGHLIGHTS: Map<IElementType, TextAttributesKey> = mapOf(
            Pair(EdgeTokenTypes.TAG, TAG),
            Pair(EdgeTokenTypes.SELF_CLOSING_TAG, TAG),
            Pair(EdgeTokenTypes.OPEN_PARENTHESES, PARENTHESES),
            Pair(EdgeTokenTypes.CLOSE_PARENTHESES, PARENTHESES),
            Pair(EdgeTokenTypes.OPEN_CURLY_BRACES, CURLY_BRACES),
            Pair(EdgeTokenTypes.OPEN_RAW_CURLY_BRACES, CURLY_BRACES),
            Pair(EdgeTokenTypes.CLOSE_CURLY_BRACES, CURLY_BRACES),
            Pair(EdgeTokenTypes.CLOSE_RAW_CURLY_BRACES, CURLY_BRACES),
            Pair(EdgeTokenTypes.IDENTIFIER, TAG_IDENTIFIER),
            Pair(EdgeTokenTypes.JAVASCRIPT_CONTENT, CONTENT),
            Pair(EdgeTokenTypes.HTML_CONTENT, CONTENT),
            Pair(EdgeTokenTypes.COMMENT, COMMENT),
            Pair(EdgeTokenTypes.OPEN_COMMENT, COMMENT),
            Pair(EdgeTokenTypes.COMMENT_CONTENT, COMMENT),
            Pair(EdgeTokenTypes.CLOSE_COMMENT, COMMENT),
        )

        val DISPLAY_NAMES = mapOf(
            Pair(CURLY_BRACES, AdonisBundle.message("edge.page.colors.descriptor.curly_braces.key")),
            Pair(PARENTHESES, AdonisBundle.message("edge.page.colors.descriptor.parentheses.key")),
            Pair(TAG_IDENTIFIER, AdonisBundle.message("edge.page.colors.descriptor.tag_identifier.key")),
            Pair(TAG, AdonisBundle.message("edge.page.colors.descriptor.tag.key")),
            Pair(COMMENT, AdonisBundle.message("edge.page.colors.descriptor.comment.key"))
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