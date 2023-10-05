package com.github.joaopedrobraz.intellijadonis.pages

import com.github.joaopedrobraz.intellijadonis.Adonis
import com.github.joaopedrobraz.intellijadonis.AdonisBundle
import com.github.joaopedrobraz.intellijadonis.EdgeHighlighter
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage

private val ATTRS = run {
    val attributes =
        arrayOfNulls<AttributesDescriptor>(EdgeHighlighter.Attributes.DISPLAY_NAMES.size)
    val textAttributesKeys: Set<TextAttributesKey> = EdgeHighlighter.Attributes.DISPLAY_NAMES.keys
    val keys = textAttributesKeys.toList()
    for (i in keys.indices) {
        val key = keys[i]
        val name: String = EdgeHighlighter.Attributes.DISPLAY_NAMES[key] ?: continue
        attributes[i] = AttributesDescriptor(name, key)
    }
    attributes
}

class EdgeColorsPages : ColorSettingsPage {
    override fun getAttributeDescriptors() = ATTRS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName() = AdonisBundle.message("fileType.displayName")

    override fun getIcon() = Adonis.ICON

    override fun getHighlighter() = EdgeHighlighter()

    override fun getDemoText() = """
        {{-- this is a comment --}}
        @if(true)
            {{ aMustacheExpression }}
        @else
            {{{ aRawMustacheExpression }}}
        @endif
        <p>@{{ this is actually an escaped mustache, so it gets printed as is }}</p>
        <p>@{{{ this as well }}}</p>
        <a>Check this out: @@if (An escaped tag!)</a>
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap() = null
}