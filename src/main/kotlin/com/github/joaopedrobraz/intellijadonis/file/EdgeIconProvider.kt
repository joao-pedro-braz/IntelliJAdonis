package com.github.joaopedrobraz.intellijadonis.file

import com.github.joaopedrobraz.intellijadonis.Adonis
import com.github.joaopedrobraz.intellijadonis.psi.EdgePsiFile
import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import javax.swing.Icon

class EdgeIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? = if (element is EdgePsiFile) Adonis.ICON else null
}