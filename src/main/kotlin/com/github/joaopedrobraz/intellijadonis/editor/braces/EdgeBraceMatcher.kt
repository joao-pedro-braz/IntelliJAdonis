package com.github.joaopedrobraz.intellijadonis.editor.braces

import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes
import com.intellij.codeInsight.highlighting.BraceMatcher
import com.intellij.openapi.editor.highlighter.HighlighterIterator
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class EdgeBraceMatcher : BraceMatcher {

    private object MatchingBraces {
        @JvmStatic
        val LEFT_BRACES = setOf(
            EdgeTokenTypes.OPEN_CURLY_BRACES,
            EdgeTokenTypes.OPEN_RAW_CURLY_BRACES,
            EdgeTokenTypes.OPEN_PARENTHESES
        )

        @JvmStatic
        val RIGHT_BRACES = mapOf(
            Pair(
                EdgeTokenTypes.CLOSE_CURLY_BRACES,
                EdgeTokenTypes.OPEN_CURLY_BRACES,
            ),
            Pair(
                EdgeTokenTypes.CLOSE_RAW_CURLY_BRACES,
                EdgeTokenTypes.OPEN_RAW_CURLY_BRACES,
            ),
            Pair(EdgeTokenTypes.CLOSE_PARENTHESES, arrayOf(EdgeTokenTypes.OPEN_PARENTHESES))
        )
    }

    override fun getBraceTokenGroupId(tokenType: IElementType) = 1

    override fun isLBraceToken(iterator: HighlighterIterator, fileText: CharSequence, fileType: FileType) =
        MatchingBraces.LEFT_BRACES.contains(iterator.tokenType)

    override fun isRBraceToken(iterator: HighlighterIterator, fileText: CharSequence, fileType: FileType): Boolean {
        if (!MatchingBraces.RIGHT_BRACES.contains(iterator.tokenType)) return false

        val opener = MatchingBraces.RIGHT_BRACES[iterator.tokenType] ?: return false
        var iteratorRetreatCount = 0
        var isRBraceToken = false
        while (true) {
            iterator.retreat()
            iteratorRetreatCount++

            if (iterator.atEnd()) break

            if (iterator.tokenType == opener) {
                isRBraceToken = true
                break
            }
        }

        // reset the given iterator before returning
        while (iteratorRetreatCount-- > 0) {
            iterator.advance()
        }

        return isRBraceToken
    }

    override fun isPairBraces(tokenType: IElementType, tokenType2: IElementType) =
        MatchingBraces.LEFT_BRACES.contains(tokenType) && MatchingBraces.RIGHT_BRACES.contains(tokenType2)
                || MatchingBraces.RIGHT_BRACES.contains(tokenType) && MatchingBraces.LEFT_BRACES.contains(tokenType2)

    override fun isStructuralBrace(iterator: HighlighterIterator, text: CharSequence, fileType: FileType) = false

    override fun getOppositeBraceTokenType(type: IElementType) = null

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int) = openingBraceOffset
}