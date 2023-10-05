package com.github.joaopedrobraz.intellijadonis.parsing

import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.intellij.psi.tree.IElementType

class EdgeCompositeElementType(debugName: String) :
    IElementType(debugName, EdgeLanguage.INSTANCE)