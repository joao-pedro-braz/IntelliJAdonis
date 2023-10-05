package com.github.joaopedrobraz.intellijadonis.templates

import com.github.joaopedrobraz.intellijadonis.file.EdgeFileType
import com.github.joaopedrobraz.intellijadonis.psi.EdgePsiFile
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.TemplateSettings
import com.intellij.openapi.application.impl.NonBlockingReadActionImpl
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.ui.UIUtil

class EdgeEmmetTest : BasePlatformTestCase() {
    fun testSimpleTags() {
        myFixture.configureByText(EdgeFileType.Singleton.INSTANCE, "div>span<caret>")
        WriteCommandAction.runWriteCommandAction(
            project
        ) {
            TemplateManager.getInstance(project)
                .startTemplate(myFixture.editor, TemplateSettings.TAB_CHAR)
        }
        checkResult()
    }

    fun testSimpleTagsWithHtmlSubstitutor() {
        val file = myFixture.configureByText("test.edge", "div>span<caret>")
        assertInstanceOf(file, EdgePsiFile::class.java)
        WriteCommandAction.runWriteCommandAction(
            project
        ) {
            TemplateManager.getInstance(project)
                .startTemplate(myFixture.editor, TemplateSettings.TAB_CHAR)
        }
        checkResult()
    }

    private fun checkResult() {
        NonBlockingReadActionImpl.waitForAsyncTaskCompletion()
        UIUtil.dispatchAllInvocationEvents()
        myFixture.checkResult("<div><span></span></div>")
    }
}