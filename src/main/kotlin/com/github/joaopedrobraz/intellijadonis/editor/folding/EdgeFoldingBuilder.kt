package com.github.joaopedrobraz.intellijadonis.editor.folding

import com.github.joaopedrobraz.intellijadonis.parsing.EdgeValidTags
import com.github.joaopedrobraz.intellijadonis.psi.*
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.toArray
import io.ktor.util.reflect.*
import kotlin.math.min

class EdgeFoldingBuilder : FoldingBuilder, DumbAware {

    override fun buildFoldRegions(node: ASTNode, document: Document): Array<FoldingDescriptor> {
        val descriptors: MutableList<FoldingDescriptor> = ArrayList()
        PsiTreeUtil.findChildrenOfType(node.psi, EdgePsiElement::class.java)
            .forEach { appendDescriptors(it, descriptors, document) }
        return descriptors.toArray(FoldingDescriptor.EMPTY)
    }

    override fun getPlaceholderText(node: ASTNode) = "..."

    override fun isCollapsedByDefault(node: ASTNode) = false

    private fun appendDescriptors(psi: PsiElement, descriptors: MutableList<FoldingDescriptor>, document: Document) {
        if (isSingleLine(psi, document)) return

        if (psi is EdgeComment) return appendDescriptorsForComment(psi, descriptors)
        if (psi is EdgeCurlyBraces) return appendDescriptorsForCurlyBraces(psi, descriptors)
        if (psi is EdgeTagWrapper) return appendDescriptorsForTags(psi, descriptors, document)
    }

    private fun appendDescriptorsForComment(
        psi: EdgeComment,
        descriptors: MutableList<FoldingDescriptor>
    ) {
        val commentNode = psi.node
        val commentText = commentNode.text

        // comment might be unclosed or too short (a one character fold triggers an invalid range assertion error),
        // so do a bit of sanity checking on its length and whether it's got the requisite open/close
        // tags before we allow folding.
        if (commentText.length > 10 && commentText.startsWith("{{--") && commentText.endsWith("--}}")) {
            val range = TextRange(commentNode.textRange.startOffset + 4, commentNode.textRange.endOffset - 4)
            descriptors.add(FoldingDescriptor(commentNode, range))
        }
    }

    private fun appendDescriptorsForTags(
        wrapper: EdgeTagWrapper,
        descriptors: MutableList<FoldingDescriptor>,
        document: Document
    ) {
        val closingElementForTopTag = getClosingElementForTag(wrapper, true)
        val closingElementForBottomTag = getClosingElementForTag(wrapper, false)

        // If we've got a well-formed block with the open and close elems we need, define a region to fold.
        if (closingElementForTopTag != null && closingElementForBottomTag != null) {
            val endOfFirstOpenTagLine =
                document.getLineEndOffset(document.getLineNumber(wrapper.textRange.startOffset))
            // We set the start of the text we'll fold to be just before the closing parens of the open tag,
            //     or, if the open tag spans multiple lines, to the end of the first line.
            val foldingRangeStartOffset = min(closingElementForTopTag.textRange.startOffset, endOfFirstOpenTagLine)
            // We set the end of the text we'll fold to be just before the final close tag in this block
            val foldingRangeEndOffset = closingElementForBottomTag.textRange.startOffset
            val range = TextRange(foldingRangeStartOffset + 1, foldingRangeEndOffset - 1)
            descriptors.add(FoldingDescriptor(wrapper, range))
        }
    }

    private fun appendDescriptorsForCurlyBraces(
        psi: EdgeCurlyBraces,
        descriptors: MutableList<FoldingDescriptor>
    ) {
        // if we've got a well-formed block with the open and close elems we need, define a region to fold
        if (psi.firstChild != null && psi.lastChild != null) {
            descriptors.add(FoldingDescriptor(psi, psi.textRange))
        }
    }

    private fun getClosingElementForTag(wrapper: EdgeTagWrapper, isOpening: Boolean): PsiElement? {
        val psiElement = if (isOpening) wrapper.firstChild else wrapper.lastChild

        if (psiElement is EdgeSelfCloseBlockTag) return null // Self-closing tags can't be folded
        if (isOpening && psiElement !is EdgeOpenBlockTag) return null // If we're opening, the tag must be an opening one
        if (!isOpening && psiElement !is EdgeCloseBlockTag) return null // If we're closing, the tag must be a closing one

        val identifier = wrapper.getIdentifier() ?: return null
        // A tag must be a block level tag in order for it to be foldable
        if (psiElement is EdgeOpenBlockTag &&
            EdgeValidTags.BLOCK_LEVEL_TAGS.firstOrNull { it.matches(identifier) } == null
        ) return null

        val expectingType = if (isOpening) EdgeCloseParentheses::class else EdgeTagName::class
        val lastChild = psiElement.lastChild
        return if (lastChild.instanceOf(expectingType)) {
            lastChild
        } else null
    }

    private fun isSingleLine(element: PsiElement, document: Document): Boolean {
        val range = element.textRange
        return document.getLineNumber(range.startOffset) == document.getLineNumber(range.endOffset)
    }
}