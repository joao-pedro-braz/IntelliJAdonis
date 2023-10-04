package com.github.joaopedrobraz.intellijadonis.structure

import com.github.joaopedrobraz.intellijadonis.psi.EdgePsiFile
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase

class EdgeTreeElementFile(private val psiFile: EdgePsiFile) : PsiTreeElementBase<EdgePsiFile>(psiFile) {

    override fun getPresentableText() = psiFile.name

    override fun getChildrenBase() = EdgeTreeElement.getStructureViewTreeElements(psiFile)
}