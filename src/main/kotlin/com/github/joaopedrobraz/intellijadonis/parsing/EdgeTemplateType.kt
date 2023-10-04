package com.github.joaopedrobraz.intellijadonis.parsing

import com.intellij.lang.Language
import com.intellij.psi.templateLanguages.TemplateDataElementType
import com.intellij.psi.tree.IElementType

class EdgeTemplateType(
    debugName: String,
    lang: Language,
    templateElementType: IElementType,
    outerElementType: IElementType
) :
    TemplateDataElementType(
        debugName,
        lang,
        templateElementType,
        outerElementType
    )