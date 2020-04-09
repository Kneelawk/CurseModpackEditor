package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.curse.ModpackFile
import javafx.beans.property.SimpleBooleanProperty
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * The controller for the main modpack editor view.
 */
class ModpackEditorMainController : Controller() {
    val model: ModpackModel by inject()

    val running = SimpleBooleanProperty(false)

    private var previousDir: File = Path.of(model.modpackLocation.value).toAbsolutePath().parent.toFile()

    fun newModpack() {
        val newScope = Scope()
        setInScope(ModpackModel(), newScope)
        find<CreateModpackView>(newScope).openWindow(escapeClosesWindow = false, owner = null)
    }

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

            runAsync {
                model.commit()
                runLater { running.value = false }
            }
        } ?: run {
            running.value = false
        }
    }

    fun openModpack() {
        chooseFile("Open Modpack", arrayOf(FileChooser.ExtensionFilter("Curse Modpack Files", "*.zip")),
            previousDir, FileChooserMode.Single).firstOrNull()?.let {
            val location = it.absolutePath
            previousDir = it.parentFile

            runAsync {
                val newModpack = ModpackModel(ModpackFile(it.toPath()))

                runLater {
                    val newScope = Scope()

                    newModpack.rawModpackLocation.value = location
                    newModpack.modpackLocation.value = location

                    setInScope(newModpack, newScope)
                    find<ModpackEditorMainView>(newScope).openWindow(escapeClosesWindow = false, owner = null)
                }
            }
        }
    }

    fun duplicateModpack() {
        running.value = true
        chooseFile("Duplicate Modpack Destination",
            arrayOf(FileChooser.ExtensionFilter("Curse Modpack Files", "*.zip")),
            previousDir, FileChooserMode.Save).firstOrNull()?.let {
            var path = it.absolutePath
            if (!path.endsWith(".zip")) {
                path += ".zip"
            }

            runAsync {
                val newModpack = ModpackModel(model.openModpack.clone(Paths.get(path)))

                runLater {
                    val newScope = Scope()

                    newModpack.rawModpackLocation.value = path
                    newModpack.modpackLocation.value = path

                    setInScope(newModpack, newScope)
                    find<ModpackEditorMainView>(newScope).openWindow(escapeClosesWindow = false, owner = null)

                    running.value = false
                }
            }
        } ?: run {
            running.value = false
        }
    }
}
