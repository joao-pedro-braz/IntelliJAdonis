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
        val ALL_VALID_TAGS = setOf(
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
        val SEEKABLE_TAGS = setOf(
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
        val BLOCK_LEVEL_TAGS = setOf(
            COMPONENT,
            EACH,
            IF,
            SECTION,
            SLOT,
            UNLESS
        )

        @JvmField
        val END_TAGS = setOf(
            END,
            END_COMPONENT,
            END_EACH,
            END_IF,
            END_SECTION,
            END_SLOT,
        )

        @JvmField
        val MATCHING_END_TAG_PAIRS = mapOf(
            Pair(COMPONENT, setOf(END, END_COMPONENT)),
            Pair(SECTION, setOf(END, END_SECTION)),
            Pair(EACH, setOf(END, END_EACH)),
            Pair(IF, setOf(END, END_IF)),
            Pair(SLOT, setOf(END, END_SLOT)),
        )

        @JvmStatic
        fun fromString(value: String): EdgeValidTags? {
            return EdgeValidTags.ALL_VALID_TAGS.firstOrNull { it.matches(value) }
        }
    }

    fun matches(value: String?) = value?.lowercase()?.trim() == tagName

    override fun toString() = tagName
}