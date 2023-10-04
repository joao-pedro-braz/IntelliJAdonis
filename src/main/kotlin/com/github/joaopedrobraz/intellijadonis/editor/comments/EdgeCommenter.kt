package com.github.joaopedrobraz.intellijadonis.editor.comments

import com.intellij.lang.Commenter

class EdgeCommenter : Commenter {
    override fun getLineCommentPrefix() = null

    override fun getBlockCommentPrefix() = "{{--"

    override fun getBlockCommentSuffix() = "--}}"

    override fun getCommentedBlockCommentPrefix() = null

    override fun getCommentedBlockCommentSuffix() = null
}