package com.github.joaopedrobraz.intellijadonis.file

import com.github.joaopedrobraz.intellijadonis.EdgeTemplateHighlighter
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.fileTypes.EditorHighlighterProvider
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class EdgeHighlighterProvider : EditorHighlighterProvider {
    override fun getEditorHighlighter(
        project: Project?,
        fileType: FileType,
        virtualFile: VirtualFile?,
        colors: EditorColorsScheme,
    ) = EdgeTemplateHighlighter(project, virtualFile, colors)
}