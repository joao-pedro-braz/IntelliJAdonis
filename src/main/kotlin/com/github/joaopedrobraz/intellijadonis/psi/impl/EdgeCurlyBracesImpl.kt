package com.github.joaopedrobraz.intellijadonis.psi.impl

import com.github.joaopedrobraz.intellijadonis.psi.EdgeCurlyBraces
import com.intellij.lang.ASTNode

open class EdgeCurlyBracesImpl(astNode: ASTNode) : EdgePsiElementImpl(astNode), EdgeCurlyBraces