package com.github.joaopedrobraz.intellijadonis.lang

import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.lang.InjectableLanguage
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.templateLanguages.TemplateLanguage

class EdgeLanguage : Language(NAME), TemplateLanguage, InjectableLanguage {
    companion object {
        val INSTANCE = EdgeLanguage()
        const val NAME = "Edge"

        fun getDefaultTemplateLang(): LanguageFileType = HtmlFileType.INSTANCE
    }
}