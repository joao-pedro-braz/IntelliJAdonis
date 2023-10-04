package com.github.joaopedrobraz.intellijadonis.psi.impl

import com.github.joaopedrobraz.intellijadonis.psi.EdgeCloseParentheses
import com.intellij.lang.ASTNode

class EdgeCloseParenthesesImpl(astNode: ASTNode) : EdgePsiElementImpl(astNode), EdgeCloseParentheses