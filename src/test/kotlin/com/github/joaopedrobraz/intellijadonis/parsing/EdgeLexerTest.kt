package com.github.joaopedrobraz.intellijadonis.parsing

import com.intellij.lexer.Lexer
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.PlatformLiteFixture
import kotlin.math.min

abstract class EdgeLexerTest : PlatformLiteFixture() {
    private lateinit var _lexer: Lexer

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        _lexer = EdgeLexer()
    }

    fun tokenize(string: String?): TokenizerResult {
        val tokens: MutableList<Token> = ArrayList()
        lateinit var currentElement: IElementType
        _lexer.start(string!!)
        while (_lexer.tokenType?.also { currentElement = it } != null) {
            tokens.add(Token(currentElement, _lexer.tokenText))
            _lexer.advance()
        }
        return TokenizerResult(tokens)
    }

    class Token(val elementType: IElementType, val elementContent: String) {
        override fun toString() = "$elementType: $elementContent"
    }

    class TokenizerResult(private val _tokens: List<Token>) {
        /**
         * @param tokenTypes The token types expected for the tokens in this TokenizerResult, in the order they are expected
         */
        fun shouldMatchTokenTypes(vararg tokenTypes: IElementType?) {
            // compare tokens as far as we can (which is ideally all of them).  We'll check that
            // these have the same length next - doing the content compare first yields more illuminating failures
            // in the case of a mis-match
            for (i in 0 until min(_tokens.size.toDouble(), tokenTypes.size.toDouble()).toInt()) {
                assertEquals("Bad token at position $i", tokenTypes[i], _tokens[i].elementType)
            }
            assertEquals(tokenTypes.size, _tokens.size)
        }

        /**
         * @param tokenContent The content string expected for the tokens in this TokenizerResult, in the order they are expected
         */
        fun shouldMatchTokenContent(vararg tokenContent: String?) {
            // compare tokens as far as we can (which is ideally all of them).  We'll check that
            // these have the same length next - doing the content compare first yields more illuminating failures
            // in the case of a mis-match
            for (i in 0 until min(_tokens.size.toDouble(), tokenContent.size.toDouble()).toInt()) {
                assertEquals(tokenContent[i], _tokens[i].elementContent)
            }
            assertEquals(tokenContent.size, _tokens.size)
        }
    }
}
