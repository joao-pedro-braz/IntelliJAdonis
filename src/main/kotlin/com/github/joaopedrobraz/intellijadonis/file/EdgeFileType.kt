package com.github.joaopedrobraz.intellijadonis.file

import com.github.joaopedrobraz.intellijadonis.Adonis
import com.github.joaopedrobraz.intellijadonis.AdonisBundle
import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.intellij.ide.highlighter.XmlLikeFileType
import com.intellij.openapi.fileTypes.TemplateLanguageFileType

object EdgeFileType : XmlLikeFileType(EdgeLanguage.INSTANCE), TemplateLanguageFileType {
    object Singleton {
        val INSTANCE = EdgeFileType
    }

    override fun getName(): String = AdonisBundle.message("fileType.shortName")

    override fun getDescription() = AdonisBundle.message("fileType.description")

    override fun getDefaultExtension() = AdonisBundle.message("fileType.defaultExtension")

    override fun getIcon() = Adonis.ICON
}