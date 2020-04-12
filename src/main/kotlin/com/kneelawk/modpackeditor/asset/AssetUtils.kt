package com.kneelawk.modpackeditor.asset

import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Created by Kneelawk on 4/11/20.
 */
object AssetUtils {
    val webStylesheet: String = run {
        // The WebEngine can't seem to handle jrt:/ urls so we copy the stylesheet to and external file.
        val stylesheetPath = Files.createTempFile("web", ".css")
        javaClass.getResourceAsStream("/com/kneelawk/modpackeditor/web.css").use {
            Files.copy(it, stylesheetPath, StandardCopyOption.REPLACE_EXISTING)
        }
        stylesheetPath.toFile().deleteOnExit()
        stylesheetPath.toUri().toURL().toExternalForm()
    }
}