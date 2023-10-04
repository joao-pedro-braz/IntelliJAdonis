package com.github.joaopedrobraz.intellijadonis.psi

import com.github.joaopedrobraz.intellijadonis.file.EdgeFileType
import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.json.psi.impl.JSStringLiteralEscaper
import com.intellij.psi.ElementManipulators
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiLanguageInjectionHost

class EdgePsiFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, EdgeLanguage.INSTANCE),
    PsiLanguageInjectionHost {
    override fun getFileType() = EdgeFileType.Singleton.INSTANCE

    override fun toString() = "EdgeFile:$name"

    override fun isValidHost() = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        return ElementManipulators.handleContentChange(this, text)
    }

    override fun createLiteralTextEscaper() = object : JSStringLiteralEscaper<PsiLanguageInjectionHost?>(this) {
        override fun isRegExpLiteral(): Boolean {
            return false
        }
    }
}