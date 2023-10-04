package com.github.joaopedrobraz.intellijadonis.util

import com.intellij.openapi.application.PathManager
import java.io.File

object EdgeTestUtils {
    val BASE_TEST_DATA_PATH = findTestDataPath()

    private fun findTestDataPath(): String {
        val f = File("src/test", "data")
        return if (f.exists()) {
            f.absolutePath
        } else PathManager.getHomePath() + "/contrib/edge/test/data"
    }
}