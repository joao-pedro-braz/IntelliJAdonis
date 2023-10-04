package com.github.joaopedrobraz.intellijadonis.parsing

import com.intellij.lang.html.HTMLLanguage
import com.intellij.psi.tree.OuterLanguageElementType
import com.intellij.psi.tree.TokenSet

object EdgeTokenTypes {

    // Used to delineate tags in the PSI tree. The formatter requires this extra structure.
    @JvmField
    val TAG_WRAPPER = EdgeCompositeElementType("TAG_WRAPPER")

    @JvmField
    val STATEMENTS = EdgeCompositeElementType("STATEMENTS")

    @JvmField
    val CURLY_BRACES = EdgeCompositeElementType("CURLY_BRACES")

    @JvmField
    val ESCAPED_CURLY_BRACES = EdgeCompositeElementType("ESCAPED_CURLY_BRACES")

    @JvmField
    val RAW_CURLY_BRACES = EdgeCompositeElementType("RAW_CURLY_BRACES")

    @JvmField
    val ESCAPED_RAW_CURLY_BRACES = EdgeCompositeElementType("ESCAPED_RAW_CURLY_BRACES")

    @JvmField
    val INLINE_TAG = EdgeCompositeElementType("INLINE_TAG")

    @JvmField
    val OPEN_BLOCK_TAG = EdgeCompositeElementType("OPEN_BLOCK_TAG")

    @JvmField
    val CLOSE_BLOCK_TAG = EdgeCompositeElementType("CLOSE_BLOCK_TAG")

    @JvmField
    val SELF_CLOSE_BLOCK_TAG = EdgeCompositeElementType("SELF_CLOSE_BLOCK_TAG")

    @JvmField
    val COMMENT = EdgeCompositeElementType("COMMENT")

    @JvmField
    val TAG_NAME = EdgeCompositeElementType("TAG_NAME")

    @JvmField
    val TAG = EdgeElementType("TAG", "edge.parsing.element.expected.tag")

    @JvmField
    val IDENTIFIER = EdgeElementType("IDENTIFIER", "edge.parsing.element.expected.identifier")

    @JvmField
    val KEYWORD = EdgeElementType("KEYWORD", "edge.parsing.element.expected.keyword")

    @JvmField
    val ESCAPED_TAG = EdgeElementType("ESCAPED_TAG", "edge.parsing.element.expected.escaped_tag")

    @JvmField
    val SELF_CLOSING_TAG = EdgeElementType("SELF_CLOSING_TAG", "edge.parsing.element.expected.self_closing_tag")

    @JvmField
    val OPEN_PARENTHESES = EdgeElementType("OPEN_PARENTHESES", "edge.parsing.element.expected.open_parentheses")

    @JvmField
    val CLOSE_PARENTHESES = EdgeElementType("CLOSE_PARENTHESES", "edge.parsing.element.expected.close_parentheses")

    @JvmField
    val OPEN_CURLY_BRACES = EdgeElementType("OPEN_CURLY_BRACES", "edge.parsing.element.expected.open_curly_braces")

    @JvmField
    val ESCAPED_OPEN_CURLY_BRACES =
        EdgeElementType("ESCAPED_OPEN_CURLY_BRACES", "edge.parsing.element.expected.escaped_open_curly_braces")

    @JvmField
    val OPEN_RAW_CURLY_BRACES =
        EdgeElementType("OPEN_RAW_CURLY_BRACES", "edge.parsing.element.expected.open_raw_curly_braces")

    @JvmField
    val ESCAPED_OPEN_RAW_CURLY_BRACES =
        EdgeElementType("ESCAPED_OPEN_RAW_CURLY_BRACES", "edge.parsing.element.expected.escaped_open_raw_curly_braces")

    @JvmField
    val CLOSE_CURLY_BRACES = EdgeElementType("CLOSE_CURLY_BRACES", "edge.parsing.element.expected.close_curly_braces")

    @JvmField
    val CLOSE_RAW_CURLY_BRACES =
        EdgeElementType("CLOSE_RAW_CURLY_BRACES", "edge.parsing.element.expected.close_raw_curly_braces")

    @JvmField
    val OPEN_COMMENT =
        EdgeElementType("OPEN_COMMENT", "edge.parsing.element.expected.open_comment")

    @JvmField
    val COMMENT_CONTENT = EdgeElementType("COMMENT_CONTENT", "edge.parsing.element.expected.comment_content")

    @JvmField
    val CLOSE_COMMENT =
        EdgeElementType("CLOSE_COMMENT", "edge.parsing.element.expected.close_comment")

    @JvmField
    val WHITE_SPACE = EdgeElementType("WHITE_SPACE", "edge.parsing.element.expected.white_space")

    @JvmField
    val INVALID = EdgeElementType("INVALID", "edge.parsing.element.expected.invalid")

    @JvmField
    val HTML_CONTENT = EdgeElementType("HTML_CONTENT", "edge.parsing.element.expected.html_content")

    @JvmField
    val HTML_OUTER_ELEMENT_TYPE =
        OuterLanguageElementType("HTML_OUTER_ELEMENT_TYPE", HTMLLanguage.INSTANCE)

    @JvmField
    val HTML_TEMPLATE = EdgeTemplateType("HTML_TEMPLATE", HTMLLanguage.INSTANCE, HTML_CONTENT, HTML_OUTER_ELEMENT_TYPE)

    @JvmField
    val JAVASCRIPT_CONTENT = EdgeElementType("JAVASCRIPT_CONTENT", "edge.parsing.element.expected.javascript_content")

    @JvmField
    val WHITESPACES = TokenSet.create(WHITE_SPACE)

    @JvmField
    val COMMENTS = TokenSet.create(COMMENT)

    @JvmField
    val STRING_LITERALS = TokenSet.create()
}