package com.github.joaopedrobraz.intellijadonis.editor.templates

import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.intellij.codeInsight.template.emmet.generators.XmlZenCodingGeneratorImpl
import com.intellij.lang.Language

class EdgeEmmetGenerator : XmlZenCodingGeneratorImpl() {
    override fun isMyLanguage(language: Language): Boolean {
        return language.isKindOf(EdgeLanguage.INSTANCE)
    }
}