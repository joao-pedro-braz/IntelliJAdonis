package com.github.joaopedrobraz.intellijadonis.psi.impl

import com.github.joaopedrobraz.intellijadonis.psi.EdgeStatements
import com.intellij.json.psi.impl.JSStringLiteralEscaper
import com.intellij.lang.ASTNode
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiLanguageInjectionHost

class EdgeStatementsImpl(astNode: ASTNode) : EdgePsiElementImpl(astNode), EdgeStatements {
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