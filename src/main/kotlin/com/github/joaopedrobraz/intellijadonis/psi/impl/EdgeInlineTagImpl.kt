package com.github.joaopedrobraz.intellijadonis.psi.impl

import com.github.joaopedrobraz.intellijadonis.psi.EdgeInlineTag
import com.github.joaopedrobraz.intellijadonis.psi.EdgeOpenBlockTag
import com.intellij.lang.ASTNode

class EdgeInlineTagImpl(astNode: ASTNode) : EdgePsiElementImpl(astNode), EdgeInlineTag