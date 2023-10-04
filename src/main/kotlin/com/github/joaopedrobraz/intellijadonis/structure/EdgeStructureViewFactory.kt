package com.github.joaopedrobraz.intellijadonis.structure

import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.impl.TemplateLanguageStructureViewBuilder
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.psi.PsiFile

class EdgeStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder {
        return TemplateLanguageStructureViewBuilder.create(psiFile) { file, editor ->
            EdgeStructureViewModel(
                editor,
                file
            )
        }
    }
}