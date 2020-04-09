package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.curse.ModpackFile
import javafx.beans.property.SimpleBooleanProperty
import javafx.stage.FileChooser
import tornadofx.Controller
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import tornadofx.runLater
import java.io.File
import java.nio.file.Path
import kotlin.reflect.KProperty1

/**
 * The controller for the main modpack editor view.
 */
class ModpackEditorMainController : Controller() {
    val model: ModpackModel by inject()

    val running = SimpleBooleanProperty(false)

    var previousDir = Path.of(model.modpackLocation.value).toAbsolutePath().parent.toFile()

    fun saveModpack() {
        running.value = true
        runAsync {
            model.commit()
            runLater { running.value = false }
        }
    }

    fun saveModpackAs() {
        running.value = true
        chooseFile("Save Modpack As", arrayOf(FileChooser.ExtensionFilter("Curse Modpack Files", "*.zip")),
            previousDir, FileChooserMode.Save).firstOrNull()?.let {
            var path = it.absolutePath
            if (!path.endsWith(".zip")) {
                path += ".zip"
            }
            model.modpackLocation.value = path
            previousDir = it.parentFile
        }
        runAsync {
            model.commit()
            runLater { running.value = false }
        }
    }
}
