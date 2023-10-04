package com.github.joaopedrobraz.intellijadonis.file

import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.FileViewProviderFactory
import com.intellij.psi.PsiManager

class EdgeFileViewProviderFactory : FileViewProviderFactory {
    override fun createFileViewProvider(
        file: VirtualFile,
        language: Language,
        manager: PsiManager,
        eventSystemEnabled: Boolean
    ): FileViewProvider {
        assert(language.isKindOf(EdgeLanguage.INSTANCE))
        return EdgeFileViewProvider(manager, file, eventSystemEnabled)
    }

}