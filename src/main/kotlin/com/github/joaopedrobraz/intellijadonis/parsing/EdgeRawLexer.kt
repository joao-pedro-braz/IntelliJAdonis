package com.github.joaopedrobraz.intellijadonis.parsing

import com.intellij.lexer.FlexAdapter

class EdgeRawLexer : FlexAdapter(_EdgeLexer(null))