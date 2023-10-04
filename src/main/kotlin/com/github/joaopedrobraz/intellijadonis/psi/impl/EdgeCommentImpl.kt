package com.github.joaopedrobraz.intellijadonis.psi.impl

import com.github.joaopedrobraz.intellijadonis.psi.EdgeComment
import com.intellij.lang.ASTNode

class EdgeCommentImpl(astNode: ASTNode) : EdgePsiElementImpl(astNode), EdgeComment