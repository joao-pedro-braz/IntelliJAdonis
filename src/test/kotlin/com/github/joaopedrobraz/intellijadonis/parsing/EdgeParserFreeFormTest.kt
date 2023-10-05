package com.github.joaopedrobraz.intellijadonis.parsing

class EdgeParserFreeFormTest : EdgeParserTest() {
    fun testSampleFullFile1() = doTest(true)

    fun testSelfClosingTag() = doTest(true)

    fun testInlineTag() = doTest(true)

    fun testUnclosedBlockTag() = doTest(true)

    fun testBlockTag() = doTest(true)

    fun testPartialSelfClosingTag() = doTest(true)

    fun testEachTag() = doTest(true)

    fun testSwallowNewLine() = doTest(true)

    fun testIfElseIfElseTag() = doTest(true)

    fun testClosingTagErrors() = doTest(true)
}