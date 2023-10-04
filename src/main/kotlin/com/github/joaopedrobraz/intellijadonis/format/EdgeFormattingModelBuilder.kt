package com.github.joaopedrobraz.intellijadonis.format

import com.github.joaopedrobraz.intellijadonis.config.EdgeConfig
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes
import com.intellij.formatting.*
import com.intellij.formatting.templateLanguages.DataLanguageBlockWrapper
import com.intellij.formatting.templateLanguages.TemplateLanguageBlock
import com.intellij.formatting.templateLanguages.TemplateLanguageBlockFactory
import com.intellij.formatting.templateLanguages.TemplateLanguageFormattingModelBuilder
import com.intellij.lang.ASTNode
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.DocumentBasedFormattingModel
import com.intellij.psi.formatter.FormattingDocumentModelImpl
import com.intellij.psi.formatter.xml.HtmlPolicy
import com.intellij.psi.templateLanguages.SimpleTemplateLanguageFormattingModelBuilder

class EdgeFormattingModelBuilder : TemplateLanguageFormattingModelBuilder() {
    class EdgeTemplate(
        node: ASTNode,
        wrap: Wrap?,
        alignment: Alignment?,
        blockFactory: TemplateLanguageBlockFactory,
        settings: CodeStyleSettings,
        foreignChildren: List<DataLanguageBlockWrapper>?,
        private val htmlPolicy: HtmlPolicy,
    ) : TemplateLanguageBlock(
        node,
        wrap,
        alignment,
        blockFactory,
        settings,
        foreignChildren
    ) {
        override fun getTemplateTextElementType() = EdgeTokenTypes.HTML_CONTENT
    }

    override fun createTemplateLanguageBlock(
        node: ASTNode,
        wrap: Wrap?,
        alignment: Alignment?,
        foreignChildren: MutableList<DataLanguageBlockWrapper>?,
        codeStyleSettings: CodeStyleSettings
    ): TemplateLanguageBlock {
        val documentModel = FormattingDocumentModelImpl.createOn(node.psi.containingFile)
        val policy = HtmlPolicy(codeStyleSettings, documentModel)
        return EdgeTemplate(
            node,
            wrap,
            alignment,
            this,
            codeStyleSettings,
            foreignChildren,
            policy
        )
    }

    /**
     * We have to override [TemplateLanguageFormattingModelBuilder.createModel]
     * since after we delegate to some templated languages, those languages (xml/html for sure, potentially others)
     * delegate right back to us to format the EdgeTokenType.HTML/JAVASCRIPT_OUTER_ELEMENT_TYPE token we tell them to ignore,
     * causing a stack-overflowing loop of polite format-delegation.
     */
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        if (!EdgeConfig.isFormattingEnabled()) {
            // formatting is disabled, return the no-op formatter (note that this still delegates formatting
            // to the templated language, which lets the users manage that separately)
            return SimpleTemplateLanguageFormattingModelBuilder().createModel(formattingContext)
        }
        val file = formattingContext.containingFile
        val rootBlock: Block
        val node = formattingContext.node
        rootBlock = if (node.elementType == EdgeTokenTypes.HTML_OUTER_ELEMENT_TYPE) {
            // If we're looking at a EdgeTokenTypes.HTML_OUTER_ELEMENT_TYPE element, then we've been invoked by our templated
            // language. Make a dummy block to allow that formatter to continue
            return SimpleTemplateLanguageFormattingModelBuilder().createModel(formattingContext)
        } else {
            getRootBlock(file, file.viewProvider, formattingContext.codeStyleSettings)
        }
        return DocumentBasedFormattingModel(
            rootBlock, formattingContext.project, formattingContext.codeStyleSettings, file.fileType, file
        )
    }

    override fun dontFormatMyModel() = false
}