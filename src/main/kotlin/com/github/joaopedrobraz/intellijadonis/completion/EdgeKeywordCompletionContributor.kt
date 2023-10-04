package com.github.joaopedrobraz.intellijadonis.completion

import com.github.joaopedrobraz.intellijadonis.Adonis
import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeValidTags
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.DumbAware

private val VALID_TAG_PATTERN: Regex = Regex("@(\\s+|$)")

class EdgeKeywordCompletionContributor : CompletionContributor(), DumbAware {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        if (parameters.originalFile.language == EdgeLanguage.INSTANCE
            && parameters.completionType == CompletionType.BASIC
            && isValidContext(parameters)
        )
            EdgeValidTags.ALL_VALID_TAGS.forEach {
                result.addElement(LookupElementBuilder.create(it.tagName).withIcon(Adonis.ICON).withTypeText("Edge"))
            }
        return super.fillCompletionVariants(parameters, result)
    }

    private fun isValidContext(parameters: CompletionParameters): Boolean =
        parameters.editor.document.immutableCharSequence.subSequence(
            parameters.offset - 1,
            parameters.offset + 1
        )
            .matches(VALID_TAG_PATTERN)
}