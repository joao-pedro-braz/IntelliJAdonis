package com.github.joaopedrobraz.intellijadonis.format

import com.github.joaopedrobraz.intellijadonis.file.EdgeFileType
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.FileIndentOptionsProvider
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider

class EdgeFileIndentOptionsProvider : FileIndentOptionsProvider() {
    override fun getIndentOptions(settings: CodeStyleSettings, file: PsiFile): CommonCodeStyleSettings.IndentOptions? {
        if (file.fileType == EdgeFileType.Singleton.INSTANCE) {
            val provider = PsiManagerEx.getInstanceEx(file.project).findViewProvider(file.virtualFile)
            if (provider is TemplateLanguageFileViewProvider) {
                val language = provider.templateDataLanguage
                return settings.getCommonSettings(language).indentOptions
            }
        }
        return null
    }
}