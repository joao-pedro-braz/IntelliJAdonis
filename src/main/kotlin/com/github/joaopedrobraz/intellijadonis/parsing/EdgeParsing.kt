package com.github.joaopedrobraz.intellijadonis.parsing

import com.github.joaopedrobraz.intellijadonis.AdonisBundle
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.CLOSE_BLOCK_TAG
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.CLOSE_COMMENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.CLOSE_CURLY_BRACES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.CLOSE_PARENTHESES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.CLOSE_RAW_CURLY_BRACES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.COMMENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.COMMENT_CONTENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.CURLY_BRACES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.HTML_CONTENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.IDENTIFIER
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.INLINE_TAG
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.INVALID
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.JAVASCRIPT_CONTENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.KEYWORD
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.OPEN_BLOCK_TAG
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.OPEN_COMMENT
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.OPEN_CURLY_BRACES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.OPEN_PARENTHESES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.OPEN_RAW_CURLY_BRACES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.RAW_CURLY_BRACES
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.SELF_CLOSE_BLOCK_TAG
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.SELF_CLOSING_TAG
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.STATEMENTS
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.TAG
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.TAG_NAME
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.TAG_WRAPPER
import com.github.joaopedrobraz.intellijadonis.parsing.EdgeTokenTypes.WHITE_SPACE
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilder.Marker
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.PropertyKey
import java.util.*

class EdgeParsing(private val builder: PsiBuilder) {
    companion object {
        val RECOVERY_SET = setOf(
            TAG,
            OPEN_CURLY_BRACES,
            OPEN_RAW_CURLY_BRACES,
            OPEN_COMMENT,
            HTML_CONTENT,
        )

        val OPEN_TAGS_EXCEPTION_SET = setOf(
            EdgeValidTags.ELSEIF,
            EdgeValidTags.ELSE,
        )

        const val ABORT = false
        const val CONTINUE = true
    }

    // Helps us keep track of opened block tags and determine whether parsing a closing tag is okay
    private var openTagWrappers = 0

    fun parse() {
        while (!builder.eof()) {
            parseRoot(builder)

            if (builder.eof()) break

            // jumped out of the parser prematurely... try and figure out what's tripping it up,
            // then jump back in
            val problemOffset = builder.currentOffset
            if (builder.currentOffset == problemOffset) {
                val problemMark = builder.mark()
                builder.advanceLexer()
                problemMark.error(AdonisBundle.message("edge.parsing.invalid"))
            }
        }
    }

    /**
     * root
     * : program EOF
     */
    private fun parseRoot(builder: PsiBuilder) {
        parseProgram(builder)
    }

    /**
     * program
     * : statement*
     * | ""
     * ;
     */
    private fun parseProgram(builder: PsiBuilder) {
        parseStatements(builder)
    }

    /**
     * statement*
     * : statement
     * | statements statement
     * ;
     */
    private fun parseStatements(builder: PsiBuilder) {
        val statementsMarker = builder.mark()
        // parse zero or more statements (empty statements are acceptable)
        while (true) {
            val optionalStatementMarker = builder.mark()
            if (parseStatement(builder)) {
                optionalStatementMarker.drop()
            } else {
                optionalStatementMarker.rollbackTo()
                break
            }
        }
        statementsMarker.done(STATEMENTS)
    }

    /**
     * statement
     * : tag
     * | ...
     * ;
     */
    private fun parseStatement(builder: PsiBuilder): Boolean {
        val tokenType = builder.tokenType

        /**
         * tag
         * : openBlockTag program* closeBlockTag
         * | openSelfClosingTag
         * | selfClosingTag
         * ;
         */
        run {
            if (tokenType == SELF_CLOSING_TAG) {
                val tagWrapper = builder.mark()

                return parseSelfClosingTag(builder).let {
                    if (it) tagWrapper.done(TAG_WRAPPER)
                    else tagWrapper.drop()
                    it
                }
            }

            if (tokenType == TAG) {
                val tagWrapper = builder.mark()
                openTagWrappers++

                val (result, identifier) = parseOpenTag(builder)
                if (identifier.isPresent && !EdgeValidTags.END_TAGS.any { it.matches(identifier.get()) })
                    return result.let {
                        if (it) {
                            if (builder.latestDoneMarker?.tokenType == OPEN_BLOCK_TAG) {
                                parseProgram(builder)
                                // "if" tags might have several "elseif" tags and a final "else" tag
                                if (EdgeValidTags.IF.matches(identifier.get())) {
                                    while (true) {
                                        if (!parseElseIfTag(builder)) break
                                        parseProgram(builder)
                                    }
                                    if (parseElseTag(builder))
                                        parseProgram(builder)
                                }
                                parseCloseTag(builder, identifier.get())
                            }
                            tagWrapper.done(TAG_WRAPPER)
                            openTagWrappers--
                        } else {
                            tagWrapper.drop()
                            openTagWrappers--
                        }
                        it
                    }
                else {
                    if (openTagWrappers == 1) {
                        tagWrapper.error(
                            AdonisBundle.message(
                                "edge.parsing.element.expected.open_tag",
                                EdgeValidTags.ALL_VALID_TAGS.filter { !EdgeValidTags.END_TAGS.contains(it) }
                                    .joinToString(", ")
                            )
                        )
                        openTagWrappers--
                        return true
                    } else tagWrapper.rollbackTo()
                    openTagWrappers--
                }
            }
        }

        if (tokenType == OPEN_CURLY_BRACES) return parseCurlyBraces(builder)

        if (tokenType == OPEN_RAW_CURLY_BRACES) return parseRawCurlyBraces(builder)

        if (tokenType == OPEN_COMMENT) return parseComment(builder)

        if (tokenType == HTML_CONTENT) return parseLeafToken(builder, HTML_CONTENT)

        if (tokenType == WHITE_SPACE) return parseLeafToken(builder, WHITE_SPACE)

        if (tokenType == COMMENT_CONTENT) return parseLeafToken(builder, COMMENT_CONTENT)

        return false
    }

    /**
     * selfClosingTag
     * : SELF_CLOSING_TAG IDENTIFIER tagContent
     * ;
     */
    private fun parseSelfClosingTag(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        if (!parseLeafToken(builder, SELF_CLOSING_TAG)) {
            marker.drop()
            return ABORT
        }

        val identifier = parseTagIdentifier(builder)
        if (identifier.isEmpty) {
            marker.drop()
            return CONTINUE
        }

        // Self-closing tags are also Seekable tags, so they must have content.
        if (parseTagContent(builder, identifier.get())) {
            marker.done(SELF_CLOSE_BLOCK_TAG)
            return CONTINUE
        }

        // Self-closing tags must be Block Level tags.
        if (EdgeValidTags.BLOCK_LEVEL_TAGS.firstOrNull { it.matches(identifier.get()) } == null) {
            marker.error(AdonisBundle.message("edge.parsing.element.invalid.block_level", identifier.get()))
            return CONTINUE
        }

        marker.drop()
        return ABORT
    }

    /**
     * openTag
     * : OPEN_BLOCK_TAG
     *   : TAG TAG_IDENTIFIER tagContent?
     * | INLINE_TAG
     *   : TAG TAG_IDENTIFIER tagContent?
     * ;
     */
    private fun parseOpenTag(builder: PsiBuilder): Pair<Boolean, Optional<String>> {
        val marker = builder.mark()

        if (!parseLeafToken(builder, TAG)) {
            marker.drop()
            return Pair(ABORT, Optional.empty())
        }

        val identifier = parseTagIdentifier(builder)
        if (identifier.isEmpty) {
            marker.drop()
            return Pair(CONTINUE, identifier)
        } else if (OPEN_TAGS_EXCEPTION_SET.any { it.matches(identifier.get()) }) {
            marker.rollbackTo()
            return Pair(ABORT, Optional.empty())
        }

        val isSeekable = EdgeValidTags.SEEKABLE_TAGS.firstOrNull { it.matches(identifier.get()) } != null
        val isBlockLevel =
            EdgeValidTags.BLOCK_LEVEL_TAGS.firstOrNull { it.matches(identifier.get()) } != null

        // Seekable tags must have content.
        if (isSeekable && !parseTagContent(builder, identifier.get())) {
            marker.drop()
            return Pair(CONTINUE, identifier)
        }

        if (EdgeValidTags.END_TAGS.any { it.matches(identifier.get()) }) {
            marker.drop()
            return Pair(CONTINUE, identifier)
        }

        marker.done(if (isBlockLevel) OPEN_BLOCK_TAG else INLINE_TAG)
        return Pair(CONTINUE, identifier)
    }

    private fun parseElseIfTag(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        if (!parseLeafToken(builder, TAG, false)) {
            marker.drop()
            return ABORT
        }

        val identifier = parseTagIdentifier(builder)
        if (identifier.isEmpty) {
            marker.drop()
            return ABORT
        } else if (!EdgeValidTags.ELSEIF.matches(identifier.get())) {
            if (!EdgeValidTags.ELSE.matches(identifier.get())
                && EdgeValidTags.MATCHING_END_TAG_PAIRS[EdgeValidTags.IF]
                    ?.none { it.matches(identifier.get()) } != false
            )
                marker.error(AdonisBundle.message("edge.parsing.element.expected.else_elseif_end_endif"))
            else marker.rollbackTo()
            return ABORT
        }

        if (!parseTagContent(builder, identifier.get())) {
            marker.drop()
            return CONTINUE
        }

        marker.done(INLINE_TAG)
        return CONTINUE
    }

    private fun parseElseTag(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        if (!parseLeafToken(builder, TAG, false)) {
            marker.drop()
            return ABORT
        }

        val identifier = parseTagIdentifier(builder)
        if (identifier.isEmpty) {
            marker.drop()
            return ABORT
        } else if (!EdgeValidTags.ELSE.matches(identifier.get())) {
            if (EdgeValidTags.MATCHING_END_TAG_PAIRS[EdgeValidTags.IF]
                    ?.none { it.matches(identifier.get()) } != false
            )
                marker.error(AdonisBundle.message("edge.parsing.element.expected.else_end_endif"))
            else marker.rollbackTo()
            return ABORT
        }

        marker.done(INLINE_TAG)
        return CONTINUE
    }

    /**
     * CLOSE_BLOCK_TAG
     * : TAG TAG_IDENTIFIER COMMENT_CONTENT?
     * ;
     */
    private fun parseCloseTag(builder: PsiBuilder, openingIdentifier: String): Boolean {
        val marker = builder.mark()
        val matchingEndTagPair = EdgeValidTags.MATCHING_END_TAG_PAIRS[EdgeValidTags.fromString(openingIdentifier)]

        if (!parseLeafToken(
                builder, TAG, true,
                AdonisBundle.message(
                    "edge.parsing.element.expected.close_tag",
                    (matchingEndTagPair ?: EdgeValidTags.END_TAGS).joinToString(", ")
                )
            )
        ) {
            marker.drop()
            return ABORT
        }

        val identifier = parseTagIdentifier(builder)
        if (identifier.isEmpty) {
            marker.drop()
            return CONTINUE
        } else if (EdgeValidTags.END_TAGS.none { it.matches(identifier.get()) }) {
            marker.drop()
            return CONTINUE
        } else if (matchingEndTagPair != null && matchingEndTagPair.none { it.matches(identifier.get()) }) {
            marker.error(
                AdonisBundle.message(
                    "edge.parsing.element.expected.close_tag",
                    matchingEndTagPair.joinToString(", ")
                )
            )
            return CONTINUE
        }

        parseLeafToken(builder, COMMENT_CONTENT, false)

        marker.done(CLOSE_BLOCK_TAG)
        return CONTINUE
    }

    /**
     * TAG_NAME
     * : IDENTIFIER
     * ;
     */
    private fun parseTagIdentifier(builder: PsiBuilder): Optional<String> {
        val tagNameMarker = builder.mark()
        val identifier = builder.tokenText // Just in case we actually have an identifier.
        if (!parseLeafToken(builder, IDENTIFIER, true,
                AdonisBundle.message(
                    "edge.parsing.element.expected.open_tag",
                    EdgeValidTags.ALL_VALID_TAGS.filter { !EdgeValidTags.END_TAGS.contains(it) }
                        .joinToString(", ")
                )) || identifier == null
        ) {
            tagNameMarker.drop()
            return Optional.empty()
        }
        tagNameMarker.done(TAG_NAME)

        return Optional.of(identifier)
    }

    /**
     * tagContent
     * : standardTagContent
     * | eachTagContent
     * | setTagContent
     * ;
     */
    private fun parseTagContent(builder: PsiBuilder, identifier: String): Boolean {
        return if (EdgeValidTags.EACH.matches(identifier)) {
            parseEachTagContent(builder)
        } else {
            parseStandardTagContent(builder)
        }
    }

    /**
     * standardTagContent
     * : OPEN_PARENTHESES JAVASCRIPT_CONTENT? CLOSE_PARENTHESES
     * ;
     */
    private fun parseStandardTagContent(builder: PsiBuilder): Boolean {
        parseLeafToken(builder, OPEN_PARENTHESES)
        parseLeafToken(builder, JAVASCRIPT_CONTENT, false)
        parseLeafToken(builder, CLOSE_PARENTHESES)
        parseLeafToken(builder, COMMENT_CONTENT, false)

        return CONTINUE
    }

    /**
     * eachTagContent
     * : OPEN_PARENTHESES OPEN_PARENTHESES IDENTIFIER WHITE_SPACE IDENTIFIER CLOSE_PARENTHESES KEYWORD JAVASCRIPT_CONTENT CLOSE_PARENTHESES COMMENT_CONTENT?
     * | OPEN_PARENTHESES IDENTIFIER KEYWORD JAVASCRIPT_CONTENT CLOSE_PARENTHESES COMMENT_CONTENT?
     * ;
     */
    private fun parseEachTagContent(builder: PsiBuilder): Boolean {
        parseLeafToken(builder, OPEN_PARENTHESES)

        var hasIndex = true
        if (!parseLeafToken(builder, OPEN_PARENTHESES, false)) {
            hasIndex = false
            if (builder.tokenType != IDENTIFIER) {
                // Only bail if the token isn't an Identifier, given having a single identifier is valid.
                recordLeafTokenError(OPEN_PARENTHESES, builder.mark())
                return ABORT
            }
        }

        parseLeafToken(builder, IDENTIFIER)

        if (hasIndex) {
            parseLeafToken(builder, IDENTIFIER)
            parseLeafToken(builder, CLOSE_PARENTHESES)
        }

        parseLeafToken(builder, KEYWORD.with(arrayOf("in")))
        parseLeafToken(builder, JAVASCRIPT_CONTENT)
        parseLeafToken(builder, CLOSE_PARENTHESES)
        parseLeafToken(builder, COMMENT_CONTENT, false)

        return CONTINUE
    }

    /**
     * curlyBraces
     * : OPEN_CURLY_BRACES JAVASCRIPT_CONTENT? CLOSE_CURLY_BRACES
     * ;
     */
    private fun parseCurlyBraces(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        if (!parseLeafToken(builder, OPEN_CURLY_BRACES)) {
            marker.rollbackTo()
            return ABORT
        }

        parseLeafToken(builder, JAVASCRIPT_CONTENT, false)
        parseLeafTokenGreedy(builder, CLOSE_CURLY_BRACES)

        marker.done(CURLY_BRACES)
        return CONTINUE
    }

    /**
     * rawCurlyBraces
     * : OPEN_RAW_CURLY_BRACES JAVASCRIPT_CONTENT? CLOSE_RAW_CURLY_BRACES
     * ;
     */
    private fun parseRawCurlyBraces(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        if (!parseLeafToken(builder, OPEN_RAW_CURLY_BRACES)) {
            marker.rollbackTo()
            return ABORT
        }

        parseLeafToken(builder, JAVASCRIPT_CONTENT, false)
        parseLeafTokenGreedy(builder, CLOSE_RAW_CURLY_BRACES)

        marker.done(RAW_CURLY_BRACES)
        return CONTINUE
    }

    /**
     * comment
     * : OPEN_COMMENT COMMENT_CONTENT? CLOSE_COMMENT
     * ;
     */
    private fun parseComment(builder: PsiBuilder): Boolean {
        val marker = builder.mark()

        if (!parseLeafToken(builder, OPEN_COMMENT)) {
            marker.rollbackTo()
            return ABORT
        }

        parseLeafToken(builder, COMMENT_CONTENT, false)
        parseLeafTokenGreedy(builder, CLOSE_COMMENT)

        marker.done(COMMENT)
        return CONTINUE
    }

    /**
     * Tries to parse the given token, marking an error if any other token is found
     */
    private fun parseLeafToken(
        builder: PsiBuilder,
        leafTokenType: IElementType,
        error: Boolean = true,
        @PropertyKey(resourceBundle = AdonisBundle.BUNDLE)
        errorOverride: String? = null
    ): Boolean {
        val leafTokenMark = builder.mark()
        return when (builder.tokenType) {
            leafTokenType -> {
                builder.advanceLexer()
                leafTokenMark.done(leafTokenType)
                true
            }

            INVALID -> {
                while (!builder.eof() && builder.tokenType == INVALID) {
                    builder.advanceLexer()
                }
                recordLeafTokenError(INVALID, leafTokenMark, errorOverride)
                false
            }

            else -> if (error) {
                recordLeafTokenError(leafTokenType, leafTokenMark, errorOverride)
                false
            } else {
                leafTokenMark.drop()
                false
            }
        }
    }

    /**
     * Eats tokens until it finds the expected token, marking errors along the way.
     * Will also stop if it encounters a [.RECOVERY_SET] token
     */
    private fun parseLeafTokenGreedy(builder: PsiBuilder, expectedToken: IElementType) {
        // failed to parse expected token... chew up tokens marking this error until we encounter
        // a token which give the parser a good shot at resuming
        if (builder.tokenType !== expectedToken) {
            val unexpectedTokensMarker = builder.mark()
            while (!builder.eof() && builder.tokenType !== expectedToken && !RECOVERY_SET.contains(builder.tokenType)) {
                builder.advanceLexer()
            }
            recordLeafTokenError(expectedToken, unexpectedTokensMarker)
        }
        if (!builder.eof() && builder.tokenType == expectedToken) {
            parseLeafToken(builder, expectedToken)
        }
    }

    /**
     * Error the given marker with the appropriate error message.
     */
    private fun recordLeafTokenError(
        expectedToken: IElementType,
        unexpectedTokensMarker: Marker,
        @PropertyKey(resourceBundle = AdonisBundle.BUNDLE)
        errorOverride: String? = null
    ) {
        if (expectedToken is EdgeElementType) {
            unexpectedTokensMarker.error(errorOverride ?: expectedToken.parseExpectedMessage())
        } else {
            unexpectedTokensMarker.error(AdonisBundle.message("edge.parsing.invalid"))
        }
    }
}