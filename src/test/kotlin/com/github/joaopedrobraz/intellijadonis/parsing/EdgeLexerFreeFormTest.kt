package com.github.joaopedrobraz.intellijadonis.parsing

import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.CLOSE_COMMENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.CLOSE_CURLY_BRACES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.CLOSE_PARENTHESES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.CLOSE_RAW_CURLY_BRACES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.COMMENT_CONTENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.HTML_CONTENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.IDENTIFIER
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.JAVASCRIPT_CONTENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.KEYWORD
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.OPEN_COMMENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.OPEN_CURLY_BRACES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.OPEN_PARENTHESES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.OPEN_RAW_CURLY_BRACES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.SELF_CLOSING_TAG
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.TAG
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.WHITE_SPACE

class EdgeLexerFreeFormTest : EdgeLexerTest() {
    fun testSelfClosingTag() {
        val result = tokenize("@!component('test')")
        result.shouldMatchTokenTypes(
            SELF_CLOSING_TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            JAVASCRIPT_CONTENT,
            CLOSE_PARENTHESES
        )
        result.shouldMatchTokenContent("@!", "component", "(", "'test'", ")")
    }

    fun testOpenTag() {
        val result = tokenize("@if(true)")
        result.shouldMatchTokenTypes(
            TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            JAVASCRIPT_CONTENT,
            CLOSE_PARENTHESES
        )
        result.shouldMatchTokenContent("@", "if", "(", "true", ")")
    }

    fun testOpenTagWithCurlyBraces() {
        val result = tokenize("@if({ [myVar]: true }[myVar])")
        result.shouldMatchTokenTypes(
            TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            JAVASCRIPT_CONTENT,
            CLOSE_PARENTHESES
        )
        result.shouldMatchTokenContent("@", "if", "(", "{ [myVar]: true }[myVar]", ")")
    }

    fun testCloseTag() {
        val result = tokenize("@endif")
        result.shouldMatchTokenTypes(TAG, IDENTIFIER)
        result.shouldMatchTokenContent("@", "endif")
    }

    fun testEscapedTag() {
        val result = tokenize("@@if(false)")
        result.shouldMatchTokenTypes(HTML_CONTENT, HTML_CONTENT)
        result.shouldMatchTokenContent("@@", "if(false)")
    }

    fun testEscapedTagAroundContent() {
        val result = tokenize("<div>@@if(false)</div>")
        result.shouldMatchTokenTypes(HTML_CONTENT, HTML_CONTENT, HTML_CONTENT)
        result.shouldMatchTokenContent("<div>", "@@", "if(false)</div>")
    }

    fun testCurlyBraces() {
        val result = tokenize("{{something}}")
        result.shouldMatchTokenTypes(OPEN_CURLY_BRACES, JAVASCRIPT_CONTENT, CLOSE_CURLY_BRACES)
        result.shouldMatchTokenContent("{{", "something", "}}")
    }

    fun testEscapedCurlyBraces() {
        val result = tokenize("@{{something}}")
        result.shouldMatchTokenTypes(COMMENT_CONTENT, HTML_CONTENT, COMMENT_CONTENT)
        result.shouldMatchTokenContent("@{{", "something", "}}")
    }

    fun testRawCurlyBraces() {
        val result = tokenize("{{{something}}}")
        result.shouldMatchTokenTypes(OPEN_RAW_CURLY_BRACES, JAVASCRIPT_CONTENT, CLOSE_RAW_CURLY_BRACES)
        result.shouldMatchTokenContent("{{{", "something", "}}}")
    }

    fun testEscapedRawCurlyBraces() {
        val result = tokenize("@{{{something}}}")
        result.shouldMatchTokenTypes(COMMENT_CONTENT, HTML_CONTENT, COMMENT_CONTENT)
        result.shouldMatchTokenContent("@{{{", "something", "}}}")
    }

    fun testEmpty() {
        val result = tokenize("")
        result.shouldMatchTokenTypes()
        result.shouldMatchTokenContent()
    }

    fun testWithWhitespace() {
        val result = tokenize("    @endif")
        result.shouldMatchTokenTypes(WHITE_SPACE, TAG, IDENTIFIER)
        result.shouldMatchTokenContent("    ", "@", "endif")
    }

    fun testWithContent() {
        val result = tokenize("anything    @endif")
        result.shouldMatchTokenTypes(HTML_CONTENT, HTML_CONTENT)
        result.shouldMatchTokenContent("anything    @", "endif")
    }

    fun testWithHTML() {
        val result = tokenize("<h1>{{something}}</h1>")
        result.shouldMatchTokenTypes(
            HTML_CONTENT,
            OPEN_CURLY_BRACES,
            JAVASCRIPT_CONTENT,
            CLOSE_CURLY_BRACES,
            HTML_CONTENT
        )
        result.shouldMatchTokenContent("<h1>", "{{", "something", "}}", "</h1>")
    }

    fun testPartialBraces() {
        val result = tokenize(
            """<div>
            @if(true)
            {{something}}
            @endif
            {{
            </div>""".trimIndent()
        )
        result.shouldMatchTokenTypes(
            HTML_CONTENT,
            TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            JAVASCRIPT_CONTENT,
            CLOSE_PARENTHESES,
            WHITE_SPACE,
            OPEN_CURLY_BRACES,
            JAVASCRIPT_CONTENT,
            CLOSE_CURLY_BRACES,
            WHITE_SPACE,
            TAG,
            IDENTIFIER,
            WHITE_SPACE,
            OPEN_CURLY_BRACES,
            WHITE_SPACE,
            HTML_CONTENT
        )
        result.shouldMatchTokenContent(
            "<div>\n            ",
            "@",
            "if",
            "(",
            "true",
            ")",
            "\n            ",
            "{{",
            "something",
            "}}",
            "\n            ",
            "@",
            "endif",
            "\n            ",
            "{{",
            "\n            ",
            "</div>"
        )
    }

    fun testComment() {
        val result = tokenize("{{-- this is a nice comment --}}")
        result.shouldMatchTokenTypes(
            OPEN_COMMENT,
            COMMENT_CONTENT,
            CLOSE_COMMENT
        )
        result.shouldMatchTokenContent("{{--", " this is a nice comment ", "--}}")
    }

    fun testMultilineComment() {
        val result = tokenize(
            """{{-- 
		psiElement.lastChild
		 --}}""".trimIndent()
        )
        result.shouldMatchTokenTypes(OPEN_COMMENT, COMMENT_CONTENT, CLOSE_COMMENT)
        result.shouldMatchTokenContent("{{--", " \n\t\tpsiElement.lastChild\n\t\t ", "--}}")
    }

    fun testPartialComment() {
        val result = tokenize("{{-- this is a nice comment")
        result.shouldMatchTokenTypes(
            OPEN_COMMENT,
            HTML_CONTENT,
        )
        result.shouldMatchTokenContent("{{--", " this is a nice comment")
    }

    fun testMultipleBracesInTheSameLine() {
        val result = tokenize("id=\"{{ tableId }}\" name=\"filter-{{ column['key'] }}\"")
        result.shouldMatchTokenTypes(
            HTML_CONTENT,
            OPEN_CURLY_BRACES,
            JAVASCRIPT_CONTENT,
            CLOSE_CURLY_BRACES,
            HTML_CONTENT,
            OPEN_CURLY_BRACES,
            JAVASCRIPT_CONTENT,
            CLOSE_CURLY_BRACES,
            HTML_CONTENT
        )
        result.shouldMatchTokenContent(
            "id=\"",
            "{{",
            " tableId ",
            "}}",
            "\" name=\"filter-",
            "{{",
            " column['key'] ",
            "}}",
            "\""
        )
    }

    fun testTagBeforeAnotherTagOnDifferentLine() {
        val result = tokenize(
            """
            @
            @each()
            @end
        """.trimIndent()
        )
        result.shouldMatchTokenTypes(
            TAG,
            WHITE_SPACE,
            TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            CLOSE_PARENTHESES,
            WHITE_SPACE,
            TAG,
            IDENTIFIER
        )
        result.shouldMatchTokenContent(
            "@",
            "\n",
            "@",
            "each",
            "(",
            ")",
            "\n",
            "@",
            "end"
        )
    }

    fun testTagWithoutIdentifier() {
        val result = tokenize("@ ")
        result.shouldMatchTokenTypes(TAG, WHITE_SPACE)
        result.shouldMatchTokenContent("@", " ")
    }

    fun testBracesAndTagOnSameLine() {
        val result = tokenize(
            """
            data-index="{{ index }}" class="@each()
        """.trimIndent()
        )
        result.shouldMatchTokenTypes(
            HTML_CONTENT,
            OPEN_CURLY_BRACES,
            JAVASCRIPT_CONTENT,
            CLOSE_CURLY_BRACES,
            HTML_CONTENT,
            HTML_CONTENT
        )
        result.shouldMatchTokenContent("data-index=\"", "{{", " index ", "}}", "\" class=\"@", "each()")
    }

    fun testInvalidTag() {
        val result = tokenize("@mousedown=\"(\$event.ctrlKey || \$event.shiftKey) && \$event.preventDefault()\"")
        result.shouldMatchTokenTypes(HTML_CONTENT, HTML_CONTENT)
        result.shouldMatchTokenContent(
            "@", "mousedown=\"(\$event.ctrlKey || \$event.shiftKey) && \$event.preventDefault()\""
        )
    }

    fun testEachTagCustomBehavior() {
        val result = tokenize("@each(myVar in myArr)")
        result.shouldMatchTokenTypes(
            TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            IDENTIFIER,
            KEYWORD,
            JAVASCRIPT_CONTENT,
            CLOSE_PARENTHESES
        )
        result.shouldMatchTokenContent("@", "each", "(", "myVar", " in ", "myArr", ")")
    }

    fun testEachTagCustomBehaviorWithIndex() {
        val result = tokenize("@each((myVar, myIndex) in myArr)")
        result.shouldMatchTokenTypes(
            TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            OPEN_PARENTHESES,
            IDENTIFIER,
            WHITE_SPACE,
            IDENTIFIER,
            CLOSE_PARENTHESES,
            KEYWORD,
            JAVASCRIPT_CONTENT,
            CLOSE_PARENTHESES
        )
        result.shouldMatchTokenContent("@", "each", "(", "(", "myVar", ", ", "myIndex", ")", " in ", "myArr", ")")
    }

    fun testPartialSelfClosingTag() {
        val result = tokenize(
            """
            <div>
            @!component('components/button',
            <div>
        """.trimIndent()
        )
        result.shouldMatchTokenTypes(
            HTML_CONTENT,
            SELF_CLOSING_TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            HTML_CONTENT
        )
        result.shouldMatchTokenContent("<div>\n", "@!", "component", "(", "'components/button',\n<div>")
    }

    fun testTagWithWhiteSpaceInTheEnd() {
        val result = tokenize("@set('asd' ') ")
        result.shouldMatchTokenTypes(
            TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            JAVASCRIPT_CONTENT,
            CLOSE_PARENTHESES,
            WHITE_SPACE
        )
        result.shouldMatchTokenContent("@", "set", "(", "'asd' '", ")", " ")
    }

    fun testEachTagWithWhiteSpaceInTheEnd() {
        val result = tokenize("@each(myObj in myArr) ")
        result.shouldMatchTokenTypes(
            TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            IDENTIFIER,
            KEYWORD,
            JAVASCRIPT_CONTENT,
            CLOSE_PARENTHESES,
            WHITE_SPACE
        )
        result.shouldMatchTokenContent("@", "each", "(", "myObj", " in ", "myArr", ")", " ")
    }

    fun testSwallowNewLines() {
        val result = tokenize("@if(username)~")
        result.shouldMatchTokenTypes(
            TAG,
            IDENTIFIER,
            OPEN_PARENTHESES,
            JAVASCRIPT_CONTENT,
            CLOSE_PARENTHESES,
            COMMENT_CONTENT
        )
        result.shouldMatchTokenContent(
            "@",
            "if",
            "(",
            "username",
            ")",
            "~"
        )
    }

    fun testSwallowNewLinesWithoutParentheses() {
        val result = tokenize("@endif~")
        result.shouldMatchTokenTypes(TAG, IDENTIFIER, COMMENT_CONTENT)
        result.shouldMatchTokenContent("@", "endif", "~")
    }

    fun testPartialTagWithContentBehind() {
        val result = tokenize("<div>\n@")
        result.shouldMatchTokenTypes(HTML_CONTENT, TAG)
        result.shouldMatchTokenContent("<div>\n", "@")
    }
}