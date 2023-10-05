package com.github.joaopedrobraz.intellijadonis.config

enum class Property {

    AUTO_GENERATE_CLOSE_TAG {
        override fun getStringName() = "EdgeAutoGenerateCloseTag"

        override fun getDefault() = ENABLED
    },
    AUTOCOMPLETE_BRACES {
        override fun getStringName() = "EdgeAutocompleteBraces"

        override fun getDefault() = ENABLED
    },
    FORMATTER {
        override fun getStringName() = "EdgeFormatter"

        override fun getDefault() = DISABLED
    },
    AUTO_COLLAPSE_BLOCKS {
        override fun getStringName() = "EdgeAutoCollapseBlocks"

        override fun getDefault() = ENABLED
    };

    companion object {
        const val ENABLED = "enabled"

        const val DISABLED = "disabled"
    }

    /**
     * The String which will actually be persisted in a user's properties using [com.intellij.ide.util.PropertiesComponent].
     * This value must be unique amongst Property entries
     * IMPORTANT: these should probably never change so that we don't lose a user's preferences between releases.
     */
    abstract fun getStringName(): String

    /**
     * The default/initial value for a user
     */
    abstract fun getDefault(): String
}