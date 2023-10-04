package com.github.joaopedrobraz.intellijadonis.structure

import com.github.joaopedrobraz.intellijadonis.psi.EdgePsiElement
import com.github.joaopedrobraz.intellijadonis.psi.EdgeStatements
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.psi.PsiElement
import com.intellij.util.ReflectionUtil
import javax.swing.Icon

class EdgeTreeElement(private val psiElement: EdgePsiElement) : PsiTreeElementBase<EdgePsiElement>(psiElement) {
    companion object {
        fun getStructureViewTreeElements(psiElement: PsiElement): List<StructureViewTreeElement> {
            val children: MutableList<StructureViewTreeElement> = ArrayList()
            for (childElement in psiElement.children) {
                if (childElement !is EdgePsiElement) {
                    continue
                }
                if (childElement is EdgeStatements) {
                    // EdgeStatements elements transparently wrap other elements, so we don't add
                    // this element to the tree, but we add its children
                    children.addAll(EdgeTreeElement(childElement as EdgePsiElement).getChildrenBase())
                }
                for (suitableClass in EdgeStructureViewModel.OUR_SUITABLE_CLASSES) {
                    if (ReflectionUtil.isAssignable(suitableClass, childElement.javaClass)) {
                        children.add(EdgeTreeElement(childElement))
                        break
                    }
                }
            }
            return children
        }
    }

    override fun getChildrenBase() = getStructureViewTreeElements(psiElement)

    override fun getPresentableText(): String? {
        return psiElement.getName()
    }

    override fun getIcon(open: Boolean): Icon {
        return psiElement.getIcon(0)
    }
}