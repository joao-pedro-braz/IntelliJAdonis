package com.github.joaopedrobraz.intellijadonis.parsing

import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.github.joaopedrobraz.intellijadonis.psi.EdgePsiFile
import com.github.joaopedrobraz.intellijadonis.psi.impl.*
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.PsiFileStub
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.IStubFileElementType

private val FILE_ELEMENT_TYPE: IFileElementType =
    IStubFileElementType<PsiFileStub<*>>("edge", EdgeLanguage.INSTANCE)

class EdgeParseDefinition : ParserDefinition {

    override fun createLexer(project: Project?) = EdgeLexer()

    override fun createParser(project: Project?) = EdgeParser()

    override fun getFileNodeType() = FILE_ELEMENT_TYPE

    override fun getCommentTokens() = EdgeTokenTypes.COMMENTS

    override fun getWhitespaceTokens() = EdgeTokenTypes.WHITESPACES

    override fun getStringLiteralElements() = EdgeTokenTypes.STRING_LITERALS

    override fun createElement(node: ASTNode?): PsiElement {
        val elementType = node!!.elementType

        if (elementType == EdgeTokenTypes.TAG_WRAPPER) {
            return EdgeTagWrapperImpl(node)
        }

        if (elementType == EdgeTokenTypes.STATEMENTS) {
            return EdgeStatementsImpl(node)
        }

        if (elementType == EdgeTokenTypes.CURLY_BRACES) {
            return EdgeCurlyBracesImpl(node)
        }

        if (elementType == EdgeTokenTypes.RAW_CURLY_BRACES) {
            return EdgeRawCurlyBracesImpl(node)
        }

        if (elementType == EdgeTokenTypes.TAG) {
            return EdgeTagImpl(node)
        }

        if (elementType == EdgeTokenTypes.IDENTIFIER) {
            return EdgeIdentifierImpl(node)
        }

        if (elementType == EdgeTokenTypes.INLINE_TAG) {
            return EdgeInlineTagImpl(node)
        }

        if (elementType == EdgeTokenTypes.OPEN_BLOCK_TAG) {
            return EdgeOpenBlockTagImpl(node)
        }

        if (elementType == EdgeTokenTypes.CLOSE_BLOCK_TAG) {
            return EdgeCloseBlockTagImpl(node)
        }

        if (elementType == EdgeTokenTypes.SELF_CLOSE_BLOCK_TAG) {
            return EdgeSelfCloseBlockTagImpl(node)
        }

        if (elementType == EdgeTokenTypes.OPEN_PARENTHESES) {
            return EdgeOpenParenthesesImpl(node)
        }

        if (elementType == EdgeTokenTypes.CLOSE_PARENTHESES) {
            return EdgeCloseParenthesesImpl(node)
        }

        if (elementType == EdgeTokenTypes.JAVASCRIPT_CONTENT) {
            return EdgeJavascriptContentImpl(node)
        }

        if (elementType == EdgeTokenTypes.HTML_CONTENT) {
            return EdgeHTMLContentImpl(node)
        }

        if (elementType == EdgeTokenTypes.COMMENT) {
            return EdgeCommentImpl(node)
        }

        if (elementType == EdgeTokenTypes.OPEN_COMMENT) {
            return EdgeOpenCommentImpl(node)
        }

        if (elementType == EdgeTokenTypes.COMMENT_CONTENT) {
            return EdgeCommentContentImpl(node)
        }

        if (elementType == EdgeTokenTypes.CLOSE_COMMENT) {
            return EdgeCloseCommentImpl(node)
        }

        if (elementType == EdgeTokenTypes.KEYWORD) {
            return EdgeKeywordImpl(node)
        }

        if (elementType == EdgeTokenTypes.TAG_NAME) {
            return EdgeTagNameImpl(node)
        }

        return EdgePsiElementImpl(node)
    }

    override fun createFile(viewProvider: FileViewProvider) = EdgePsiFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements {
        // TODO: Revise this
        return ParserDefinition.SpaceRequirements.MAY
    }
}