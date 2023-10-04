package com.github.joaopedrobraz.intellijadonis

import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.ex.util.LayerDescriptor
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings


class EdgeTemplateHighlighter(project: Project?, file: VirtualFile?, colors: EditorColorsScheme) :
    LayeredLexerEditorHighlighter(EdgeHighlighter(), colors) {
    init {
        // highlighter for outer lang
        var type: FileType? = null
        if (project == null || file == null) {
            type = FileTypes.PLAIN_TEXT
        } else {
            val language = TemplateDataLanguageMappings.getInstance(project).getMapping(file)
            if (language != null) type = language.associatedFileType
            if (type == null) type = EdgeLanguage.getDefaultTemplateLang()
        }

        val outerHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(type!!, project, file)
        registerLayer(EdgeTokenTypes.HTML_CONTENT, LayerDescriptor(outerHighlighter!!, ""))
    }
}