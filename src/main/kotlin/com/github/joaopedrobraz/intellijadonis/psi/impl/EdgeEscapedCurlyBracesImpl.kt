package com.github.joaopedrobraz.intellijadonis.psi.impl

import com.github.joaopedrobraz.intellijadonis.psi.EdgeEscapedCurlyBraces
import com.intellij.lang.ASTNode

class EdgeEscapedCurlyBracesImpl(astNode: ASTNode) : EdgeCurlyBracesImpl(astNode), EdgeEscapedCurlyBraces