package com.github.joaopedrobraz.intellijadonis.psi

import com.intellij.psi.util.PsiTreeUtil

interface EdgeTagWrapper : EdgePsiElement {
    fun getIdentifier(): String? {
        return PsiTreeUtil.findChildOfType(this, EdgeIdentifier::class.java)?.text
    }
}