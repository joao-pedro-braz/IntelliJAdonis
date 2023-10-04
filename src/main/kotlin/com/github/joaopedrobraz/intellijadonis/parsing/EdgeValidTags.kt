package com.github.joaopedrobraz.intellijadonis.parsing

enum class EdgeValidTags(val tagName: String) {

    COMPONENT("component"),
    DEBUGGER("debugger"),
    EACH("each"),
    ELSE("else"),
    ELSEIF("elseif"),
    IF("if"),
    INCLUDE("include"),
    INCLUDE_IF("includeIf"),
    INJECT("inject"),
    LAYOUT("layout"),
    NEW_ERROR("newError"),
    SECTION("section"),
    SET("set"),
    SLOT("slot"),
    SUPER("super"),
    UNLESS("unless"),

    END("end"),
    END_COMPONENT("end${COMPONENT.tagName}"),
    END_EACH("end${EACH.tagName}"),
    END_IF("end${IF.tagName}"),
    END_SECTION("end${SECTION.tagName}"),
    END_SLOT("end${SLOT.tagName}");

    companion object {
        @JvmField
        val ALL_VALID_TAGS = listOf(
            COMPONENT,
            DEBUGGER,
            EACH,
            ELSE,
            ELSEIF,
            IF,
            INCLUDE,
            INCLUDE_IF,
            INJECT,
            LAYOUT,
            NEW_ERROR,
            SECTION,
            SET,
            SLOT,
            SUPER,
            UNLESS,
            END,
            END_COMPONENT,
            END_EACH,
            END_IF,
            END_SECTION,
            END_SLOT,
        )

        @JvmField
        val SEEKABLE_TAGS = listOf(
            COMPONENT,
            EACH,
            ELSEIF,
            IF,
            INCLUDE,
            INCLUDE_IF,
            INJECT,
            LAYOUT,
            NEW_ERROR,
            SECTION,
            SET,
            SLOT,
            UNLESS
        )

        @JvmField
        val BLOCK_LEVEL_TAGS = listOf(
            COMPONENT,
            EACH,
            IF,
            SECTION,
            SLOT,
            UNLESS
        )

        @JvmField
        val END_TAGS = listOf(
            END,
            END_COMPONENT,
            END_EACH,
            END_IF,
            END_SECTION,
            END_SLOT,
        )

        const val LARGEST_TAG_SIZE = 12; // endcomponent

        @JvmStatic
        fun fromString(value: String): EdgeValidTags? {
            return EdgeValidTags.ALL_VALID_TAGS.firstOrNull { it.matches(value) }
        }
    }

    fun matches(value: String?) = value?.lowercase()?.trim() == tagName

    override fun toString() = tagName
}