package com.github.joaopedrobraz.intellijadonis.psi.impl

import com.github.joaopedrobraz.intellijadonis.psi.EdgeJavascriptContent
import com.intellij.json.psi.impl.JSStringLiteralEscaper
import com.intellij.lang.ASTNode
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiLanguageInjectionHost

class EdgeJavascriptContentImpl(astNode: ASTNode) : EdgePsiElementImpl(astNode), EdgeJavascriptContent {
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