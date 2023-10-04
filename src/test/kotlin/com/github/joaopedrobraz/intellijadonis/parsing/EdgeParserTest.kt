package com.github.joaopedrobraz.intellijadonis.parsing

import com.github.joaopedrobraz.intellijadonis.util.EdgeTestUtils
import com.intellij.ide.util.BasePropertyService
import com.intellij.ide.util.PropertiesComponent
import com.intellij.lang.ParserDefinition
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings
import com.intellij.psi.templateLanguages.TemplateDataLanguagePatterns
import com.intellij.testFramework.ParsingTestCase
import com.intellij.util.ArrayUtil

abstract class EdgeParserTest(vararg additionalDefinitions: ParserDefinition) : ParsingTestCase("parser", "edge", *ArrayUtil.prepend(EdgeParseDefinition(), additionalDefinitions)) {

    override fun getTestDataPath(): String {
        return EdgeTestUtils.BASE_TEST_DATA_PATH
    }

    override fun checkAllPsiRoots(): Boolean {
        return false
    }

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        val app = application
        project.registerService(TemplateDataLanguageMappings::class.java, TemplateDataLanguageMappings::class.java)

        // PropertiesComponent is used by EdgeConfig
        app.registerService(PropertiesComponent::class.java, BasePropertyService::class.java)
        project.registerService(PropertiesComponent::class.java, BasePropertyService::class.java)
        app.registerService(TemplateDataLanguagePatterns::class.java, TemplateDataLanguagePatterns())
        registerParserDefinition(EdgeParseDefinition())
    }
}