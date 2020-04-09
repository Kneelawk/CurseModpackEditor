package com.kneelawk.modpackeditor.ui

import tornadofx.Controller
import tornadofx.booleanProperty
import tornadofx.runLater

/**
 * Controller for the modpack creation view.
 */
class CreateModpackController : Controller() {
    val model: ModpackModel by inject()
    val running = booleanProperty(false)

    fun createModpack() {
        runAsync {
            model.commit {
                runLater {
                    find<CreateModpackView>().replaceWith(find<ModpackEditorMainView>())
                }
            }
        } ui {
            running.value = false
        }
    }
}
