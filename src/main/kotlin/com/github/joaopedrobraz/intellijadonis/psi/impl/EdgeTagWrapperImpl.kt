package com.github.joaopedrobraz.intellijadonis.psi.impl

import com.github.joaopedrobraz.intellijadonis.psi.EdgeTagWrapper
import com.github.joaopedrobraz.intellijadonis.psi.EdgePsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Iconable
import com.intellij.psi.util.PsiTreeUtil
import javax.swing.Icon

class EdgeTagWrapperImpl(astNode: ASTNode) : EdgePsiElementImpl(astNode), EdgeTagWrapper {

    override fun getName(): String? {
        return getFirstValidChild()?.getName()
    }

    override fun getIcon(@Iconable.IconFlags flags: Int): Icon? {
        return getFirstValidChild()?.getIcon(flags)
    }

    private fun getFirstValidChild(): EdgePsiElement? {
        return PsiTreeUtil.findChildOfType(this, EdgePsiElement::class.java)
    }
}