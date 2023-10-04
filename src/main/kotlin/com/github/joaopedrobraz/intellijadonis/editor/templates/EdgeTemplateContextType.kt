package com.github.joaopedrobraz.intellijadonis.editor.templates

import com.github.joaopedrobraz.intellijadonis.AdonisBundle
import com.github.joaopedrobraz.intellijadonis.EdgeHighlighter
import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile

class EdgeTemplateContextType : TemplateContextType(AdonisBundle.message("template.context.name")) {

    override fun isInContext(file: PsiFile, offset: Int) = EdgeLanguage.INSTANCE.`is`(file.language)

    override fun createHighlighter() = EdgeHighlighter()
}