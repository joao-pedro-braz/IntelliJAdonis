package com.github.joaopedrobraz.intellijadonis.psi.impl

import com.github.joaopedrobraz.intellijadonis.psi.EdgeCommentContent
import com.intellij.lang.ASTNode

class EdgeCommentContentImpl(astNode: ASTNode) : EdgePsiElementImpl(astNode), EdgeCommentContent