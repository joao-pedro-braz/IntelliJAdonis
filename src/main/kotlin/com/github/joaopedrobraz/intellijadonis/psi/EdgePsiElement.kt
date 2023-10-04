package com.github.joaopedrobraz.intellijadonis.psi

import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement

interface EdgePsiElement : PsiElement {
    fun getName(): @NlsSafe String?
}