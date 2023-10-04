package com.github.joaopedrobraz.intellijadonis.structure

import com.github.joaopedrobraz.intellijadonis.psi.EdgeTagWrapper
import com.github.joaopedrobraz.intellijadonis.psi.EdgePsiFile
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

class EdgeStructureViewModel(editor: Editor, psiFile: PsiFile) : TextEditorBasedStructureViewModel(editor, psiFile) {
    companion object {
        val OUR_SUITABLE_CLASSES: Array<Class<*>> = arrayOf(EdgeTagWrapper::class.java)
    }

    override fun getSuitableClasses() = OUR_SUITABLE_CLASSES

    override fun getRoot() = EdgeTreeElementFile(psiFile as EdgePsiFile)
}