package com.github.joaopedrobraz.intellijadonis.lang.injection

import com.github.joaopedrobraz.intellijadonis.parsing.EdgeValidTags
import com.github.joaopedrobraz.intellijadonis.psi.*
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.javascript.JavascriptLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import org.apache.commons.lang3.tuple.MutablePair
import org.intellij.lang.annotations.Language

@Language("JavaScript")
private val GLOBALS = """
/** @typedef {{qs?: Record<string, any>, domain?: string, prefixUrl?: string, disableRouteLookup?: boolean} & Record<string, any>} MakeUrlOptions */

/** @typedef {{qs?: Record<string, any>, domain?: string, prefixUrl?: string, disableRouteLookup?: boolean} & Record<string, any> & {expiresIn?: string | number, purpose?: string}} MakeSignedUrlOptions */

/**
 * Makes url to a registered route by looking it up with the route pattern, name or the controller.method
 * @param {string} routeIdentifier
 * @param {any[]|MakeUrlOptions|undefined} params
 * @param {MakeUrlOptions|undefined} options
 * @return string
 */
function route(routeIdentifier, params, options) {}

/**
 * Makes a signed url, which can be confirmed for it's integrity without relying on any sort of backend storage.
 * @param {string} routeIdentifier
 * @param {any[]|MakeSignedUrlOptions|undefined} params
 * @param {MakeSignedUrlOptions|undefined} options
 * @return string
 */
function signedRoute(routeIdentifier, params, options) {}

/**
 * @type {ApplicationContract}
 */
let app;

/**
 * Get configuration from a config file and optionally access the object properties using the dot notation.
 * @param {string} key
 * @param {any|undefined} defaultValue
 */
function config(key, defaultValue) {}

/**
 * Get value for a given environment variable.
 * @param {string} key
 * @param {any|undefined} defaultValue
 */
function env(key, defaultValue) {}

/**
 * @param {string} location
 * @param {keyof DisksList|undefined} disk
 */
function driveSignedUrl(location, disk) {}

/**
* The inspect helper pretty prints the value in the same output. You can think of this method as Node.js util.inspect, but instead it output HTML vs writing the output to the console.
* @param {any} toInspect
*/
function inspect(toInspect) {}

/**
* @type {Record<string, Function>}
*/
const ${'$'}slots = {};

/**
* @type {Record<any, any>}
*/
const ${'$'}context = {};

const FUNCTIONS = {};
"""

@Language("JavaScript")
private val SET_FUNCTION = """
/**
* @param {any} key
* @param {any} value
* @private
*/
function set(key, value) {}
"""

@Language("JavaScript")
private val COMPONENT_FUNCTION = """
/**
* @param {any} key
* @param {any} value
* @private
*/
function component(key, value) {}
"""

@Language("JavaScript")
private val INCLUDE_FUNCTION = """
/**
* @param {string} value
* @private
*/
function include(value) {}
"""

@Language("JavaScript")
private val SECTION_FUNCTION = """
/**
* @param {string} value
* @private
*/
function section(value) {}
"""

@Language("JavaScript")
private val LAYOUT_FUNCTION = """
/**
* @param {string} value
* @private
*/
function layout(value) {}
"""

@Language("JavaScript")
private val UNLESS_FUNCTION = """
/**
* @param {boolean} value
* @private
*/
function unless(value) {}
"""

@Language("JavaScript")
private val INCLUDE_IF_FUNCTION = """
/**
* @param {boolean} condition
* @param {string} value
* @private
*/
function includeIf(condition, value) {}
"""

class EdgeJavaScriptContentInjector : MultiHostInjector {

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context is EdgeJavascriptContent) {
            val (prefix, suffix) = getPrefixAndSuffixForJavaScriptContent(context)
            registrar.startInjecting(JavascriptLanguage.INSTANCE)
                .addPlace(
                    prefix,
                    suffix,
                    context as PsiLanguageInjectionHost,
                    TextRange(0, context.textLength)
                )
                .doneInjecting()
        }
    }

    private fun getPrefixAndSuffixForJavaScriptContent(
        content: EdgeJavascriptContent,
    ): Pair<String?, String?> {
        val result: MutablePair<String?, String?> = MutablePair(GLOBALS, "")

        // Apply scoped typings, like those derived from "each" tags.
        PsiTreeUtil.collectParents(
            content,
            EdgeTagWrapper::class.java,
            false
        ) { it is EdgePsiFile }.forEach {
            if (EdgeValidTags.EACH.matches(it.getIdentifier())) {
                addTypingToEachTag(it, result)
            }
        }

        // Apply tag specific typings.
        if (content.parent is EdgeTag)
            content.parentOfType<EdgeTagWrapper>(false)?.let { wrapper ->
                if (EdgeValidTags.SET.matches(wrapper.getIdentifier())) {
                    addTypingToSetTag(result)
                } else if (EdgeValidTags.COMPONENT.matches(wrapper.getIdentifier())) {
                    addTypingToComponentTag(result)
                } else if (EdgeValidTags.INCLUDE.matches(wrapper.getIdentifier())) {
                    addTypingToIncludeTag(result)
                } else if (EdgeValidTags.IF.matches(wrapper.getIdentifier())) {
                    addTypingToIfTag(result)
                } else if (EdgeValidTags.ELSEIF.matches(wrapper.getIdentifier())) {
                    addTypingToIfTag(result)
                } else if (EdgeValidTags.SECTION.matches(wrapper.getIdentifier())) {
                    addTypingToSectionTag(result)
                } else if (EdgeValidTags.LAYOUT.matches(wrapper.getIdentifier())) {
                    addTypingToLayoutTag(result)
                } else if (EdgeValidTags.UNLESS.matches(wrapper.getIdentifier())) {
                    addTypingToUnlessTag(result)
                } else if (EdgeValidTags.INCLUDE_IF.matches(wrapper.getIdentifier())) {
                    addTypingToIncludeIfTag(result)
                }
            }

        return result.toPair()
    }

    override fun elementsToInjectIn() = listOf(EdgeJavascriptContent::class.java)

    /**
     * "each" tags introduce one or two temporary variables within them.
     */
    private fun addTypingToEachTag(
        wrapper: EdgeTagWrapper,
        result: MutablePair<String?, String?>
    ) {
        val identifiers = PsiTreeUtil.findChildrenOfType(wrapper.firstChild, EdgeIdentifier::class.java)
            .filter { it.parent !is EdgeTagName }
        val sourceValue = PsiTreeUtil.findChildOfType(wrapper, EdgeJavascriptContent::class.java)!!
        result.left += "\nlet ${sourceValue.text};"
        identifiers.forEach {
            result.left += "\nlet ${it.text} = ${sourceValue.text}['${it.text}'];"
        }
    }

    /**
     * "set" tags are functions that receives two arguments, we type that here.
     */
    private fun addTypingToSetTag(
        result: MutablePair<String?, String?>
    ) {
        result.left += "\n{"
        result.left += SET_FUNCTION
        result.left += "\nset("
        result.right += ");\n}"
    }

    /**
     * "component" tags are functions that receives two arguments.
     */
    private fun addTypingToComponentTag(
        result: MutablePair<String?, String?>
    ) {
        result.left += "\n{"
        result.left += COMPONENT_FUNCTION
        result.left += "\ncomponent("
        result.right += ");\n}"
    }

    /**
     * "include" tags are functions that receives a single string argument.
     */
    private fun addTypingToIncludeTag(
        result: MutablePair<String?, String?>
    ) {
        result.left += "\n{"
        result.left += INCLUDE_FUNCTION
        result.left += "\ninclude("
        result.right += ");\n}"
    }

    /**
     * "if" tags are functions that receives a single boolean argument.
     */
    private fun addTypingToIfTag(
        result: MutablePair<String?, String?>
    ) {
        result.left += "if("
        result.right += ");\n}"
    }

    /**
     * "section" tags are functions that receives a single string argument.
     */
    private fun addTypingToSectionTag(
        result: MutablePair<String?, String?>
    ) {
        result.left += "\n{"
        result.left += SECTION_FUNCTION
        result.left += "\nsection("
        result.right += ");\n}"
    }

    /**
     * "layout" tags are functions that receives a single string argument.
     */
    private fun addTypingToLayoutTag(
        result: MutablePair<String?, String?>
    ) {
        result.left += "\n{"
        result.left += LAYOUT_FUNCTION
        result.left += "\nlayout("
        result.right += ");\n}"
    }

    /**
     * "unless" tags are functions that receives a single boolean argument.
     */
    private fun addTypingToUnlessTag(
        result: MutablePair<String?, String?>
    ) {
        result.left += "\n{"
        result.left += UNLESS_FUNCTION
        result.left += "\nunless("
        result.right += ");\n}"
    }

    /**
     * "unless" tags are functions that receives a single boolean argument.
     */
    private fun addTypingToIncludeIfTag(
        result: MutablePair<String?, String?>
    ) {
        result.left += "\n{"
        result.left += INCLUDE_IF_FUNCTION
        result.left += "\nincludeIf("
        result.right += ");\n}"
    }
}