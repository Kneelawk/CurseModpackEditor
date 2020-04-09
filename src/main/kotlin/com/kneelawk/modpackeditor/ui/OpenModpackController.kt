package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.curse.ModpackFile
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.nio.file.Paths

/**
 * Controller for the OpenModpackView.
 */
class OpenModpackController : Controller() {
    val model = OpenModpackModel()

    val running = SimpleBooleanProperty(false)

    private var previousDir = File(System.getProperty("user.home"))

    fun selectModpackLocation() {
        chooseFile("Modpack Location", arrayOf(FileChooser.ExtensionFilter("Curse Modpack Files", "*.zip")),
            previousDir, FileChooserMode.Single).firstOrNull()?.let {
            model.modpackLocation.value = it.absolutePath
            previousDir = it.parentFile
        }
    }

    fun openModpack() {
        running.value = true
        model.commit {
            runAsync {
                val location = model.validModpackLocation.value
                val modpack = ModpackModel(ModpackFile(Paths.get(location)))

                runLater {
                    modpack.rawModpackLocation.value = location
                    modpack.modpackLocation.value = location
                    setInScope(modpack)
                    find<OpenModpackView>().replaceWith(find<ModpackEditorMainView>())
                }
            }
        }
    }
}

class OpenModpackModel : ViewModel() {
    val validModpackLocation = SimpleStringProperty(null)
    val modpackLocation = bind { validModpackLocation }

    init {
        setDecorationProvider { CustomMessageDecorator(it.message, it.severity) }
    }
}
