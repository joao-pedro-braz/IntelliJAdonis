package com.github.joaopedrobraz.intellijadonis.config

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

object EdgeConfig {

    fun isAutoGenerateCloseTagEnabled(): Boolean {
        return getBooleanPropertyValue(Property.AUTO_GENERATE_CLOSE_TAG)
    }

    fun setAutoGenerateCloseTagEnabled(enabled: Boolean) {
        setBooleanPropertyValue(Property.AUTO_GENERATE_CLOSE_TAG, enabled)
    }

    fun isAutocompleteBracesEnabled(): Boolean {
        return getBooleanPropertyValue(Property.AUTOCOMPLETE_BRACES)
    }

    fun setAutocompleteBracesEnabled(enabled: Boolean) {
        setBooleanPropertyValue(Property.AUTOCOMPLETE_BRACES, enabled)
    }

    fun isFormattingEnabled(): Boolean {
        return getBooleanPropertyValue(Property.FORMATTER)
    }

    fun setFormattingEnabled(enabled: Boolean) {
        setBooleanPropertyValue(Property.FORMATTER, enabled)
    }

    fun isAutoCollapseBlocksEnabled(): Boolean {
        return getBooleanPropertyValue(Property.AUTO_COLLAPSE_BLOCKS)
    }

    fun setAutoCollapseBlocks(enabled: Boolean) {
        setBooleanPropertyValue(Property.AUTO_COLLAPSE_BLOCKS, enabled)
    }

    private fun getStringPropertyValue(property: Property, project: Project?): String {
        return getProperties(project).getValue(property.getStringName(), property.getDefault())
    }

    private fun getProperties(project: Project?): PropertiesComponent {
        return if (project == null) PropertiesComponent.getInstance() else PropertiesComponent.getInstance(project)
    }

    private fun setStringPropertyValue(property: Property, value: String?, project: Project?) {
        getProperties(project).setValue(property.getStringName(), value, property.getDefault())
    }

    private fun getStringPropertyValue(property: Property): String {
        return getStringPropertyValue(property, null)
    }

    private fun setStringPropertyValue(property: Property, value: String) {
        setStringPropertyValue(property, value, null)
    }

    private fun getBooleanPropertyValue(property: Property): Boolean {
        return Property.ENABLED == getStringPropertyValue(property)
    }

    private fun setBooleanPropertyValue(property: Property, enabled: Boolean, project: Project?) {
        setStringPropertyValue(property, if (enabled) Property.ENABLED else Property.DISABLED, project)
    }

    private fun setBooleanPropertyValue(property: Property, enabled: Boolean) {
        setBooleanPropertyValue(property, enabled, null)
    }
}