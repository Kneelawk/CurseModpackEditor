package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.curse.ModpackFile
import javafx.beans.property.SimpleBooleanProperty
import javafx.stage.FileChooser
import tornadofx.Controller
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import java.io.File

/**
 * Created by Kneelawk on 4/8/20.
 */
class ModpackEditorStartController : Controller() {
    private var previousDir = File(System.getProperty("user.home"))

    val running = SimpleBooleanProperty(false)

    fun openModpack() {
        running.value = true
        chooseFile("Modpack Location", arrayOf(FileChooser.ExtensionFilter("Curse Modpack Files", "*.zip")),
            previousDir, FileChooserMode.Single).firstOrNull()?.let {
            val location = it.absolutePath
            previousDir = it.parentFile

            runAsync {
                val modpack = ModpackModel(ModpackFile(it.toPath()))

                tornadofx.runLater {
                    modpack.rawModpackLocation.value = location
                    modpack.modpackLocation.value = location
                    setInScope(modpack)
                    find<ModpackEditorStartView>().replaceWith(find<ModpackEditorMainView>())
                    running.value = false
                }
            }
        } ?: run {
            running.value = false
        }
    }
}