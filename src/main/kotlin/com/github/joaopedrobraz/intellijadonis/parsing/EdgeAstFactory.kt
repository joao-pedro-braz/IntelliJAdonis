package com.github.joaopedrobraz.intellijadonis.parsing

import com.intellij.lang.ASTFactory
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.templateLanguages.OuterLanguageElementImpl
import com.intellij.psi.tree.IElementType

class EdgeAstFactory : ASTFactory() {
    override fun createLeaf(type: IElementType, text: CharSequence): LeafElement? =
        if (type == EdgeTokenTypes.HTML_OUTER_ELEMENT_TYPE) OuterLanguageElementImpl(type, text)
        else super.createLeaf(type, text)
}