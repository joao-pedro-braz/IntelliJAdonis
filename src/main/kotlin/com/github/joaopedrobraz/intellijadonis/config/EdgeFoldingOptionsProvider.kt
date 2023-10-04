package com.github.joaopedrobraz.intellijadonis.config

import com.github.joaopedrobraz.intellijadonis.AdonisBundle
import com.intellij.application.options.editor.CodeFoldingOptionsProvider
import com.intellij.openapi.options.BeanConfigurable

class EdgeFoldingOptionsProvider : BeanConfigurable<EdgeFoldingOptionsProvider.EdgeCodeFoldingOptionsBean>(
    EdgeCodeFoldingOptionsBean(),
    AdonisBundle.message("border.title.adonis.edge")
), CodeFoldingOptionsProvider {
    class EdgeCodeFoldingOptionsBean {
        fun isAutoCollapseBlocks() = EdgeConfig.isAutoCollapseBlocksEnabled()

        fun setAutoCollapseBlocks(value: Boolean) = EdgeConfig.setAutoCollapseBlocks(value)
    }

    init {
        val settings: EdgeCodeFoldingOptionsBean? = instance
        checkBox(AdonisBundle.message("edge.pages.folding.auto.collapse.blocks"),
            { settings?.isAutoCollapseBlocks() }
        ) { value ->
            settings?.setAutoCollapseBlocks(value)
        }
    }
}