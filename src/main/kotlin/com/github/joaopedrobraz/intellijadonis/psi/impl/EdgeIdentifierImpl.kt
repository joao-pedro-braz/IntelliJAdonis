package com.github.joaopedrobraz.intellijadonis.psi.impl

import com.github.joaopedrobraz.intellijadonis.psi.EdgeIdentifier
import com.intellij.lang.ASTNode

class EdgeIdentifierImpl(astNode: ASTNode) : EdgePsiElementImpl(astNode), EdgeIdentifier
