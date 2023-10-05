package com.github.joaopedrobraz.intellijadonis.file

import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes
import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider
import com.intellij.psi.tree.IElementType

class EdgeFileViewProvider(
    manager: PsiManager,
    file: VirtualFile,
    physical: Boolean,
) : MultiplePsiFilesPerDocumentFileViewProvider(manager, file, physical), TemplateLanguageFileViewProvider {

    private var myLanguages: MutableSet<Language> = mutableSetOf()

    override fun getBaseLanguage() = EdgeLanguage.INSTANCE

    override fun getTemplateDataLanguage(): HTMLLanguage = HTMLLanguage.INSTANCE

    override fun getLanguages(): Set<Language> {
        if (myLanguages.isEmpty()) {
            myLanguages.add(baseLanguage)
            myLanguages.add(templateDataLanguage)
        }
        return myLanguages
    }

    override fun cloneInner(fileCopy: VirtualFile) =
        EdgeFileViewProvider(manager, fileCopy, false)

    override fun createFile(lang: Language): PsiFile? {
        val parserDefinition = getDefinition(lang) ?: return null

        if (lang.`is`(templateDataLanguage)) {
            val file = parserDefinition.createFile(this)
            val type = getContentElementType(lang)
            if (type != null) {
                (file as PsiFileImpl).setContentElementType(type)
            }
            return file
        } else if (lang.isKindOf(baseLanguage)) {
            return parserDefinition.createFile(this)
        }
        return null
    }

    override fun getContentElementType(lang: Language): IElementType? {
        if (lang === getTemplateDataLanguage()) {
            return EdgeTokenTypes.HTML_TEMPLATE
        }
        return null
    }

    private fun getDefinition(lang: Language) = if (lang.isKindOf(baseLanguage)) {
        LanguageParserDefinitions.INSTANCE.forLanguage(if (lang.`is`(baseLanguage)) lang else baseLanguage)
    } else {
        LanguageParserDefinitions.INSTANCE.forLanguage(lang)
    }
}