package com.github.joaopedrobraz.intellijadonis.parsing

import com.github.joaopedrobraz.intellijadonis.AdonisBundle
import com.github.joaopedrobraz.intellijadonis.lang.EdgeLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.PropertyKey

class EdgeCompositeElementType(debugName: String) :
    IElementType(debugName, EdgeLanguage.INSTANCE)