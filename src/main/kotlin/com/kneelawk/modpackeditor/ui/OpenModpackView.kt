package com.kneelawk.modpackeditor.ui

import tornadofx.*
import java.nio.file.Files
import java.nio.file.Paths

/**
 * A view for opening existing modpacks.
 */
class OpenModpackView : View("Open Existing Modpack") {
    private val c: OpenModpackController by inject()

    override val root = form {
        padding = insets(25.0)
        fieldset("File") {
            field("Modpack Location:") {
                textfield(c.model.modpackLocation).validator { path ->
                    when {
                        path.isNullOrBlank() -> error("Modpack Location is blank.")
                        !Files.exists(Paths.get(path)) -> error("Modpack file does not exist.")
                        else -> null
                    }
                }
                button("...") {
                    action {
                        c.selectModpackLocation()
                    }
                }
            }
        }
        buttonbar {
            button("Cancel") {
                action {
                    replaceWith(find<ModpackEditorStartView>())
                }
            }
            button("Open Modpack") {
                enableWhen(c.model.valid)
                action {
                    c.openModpack()
                }
            }
        }
    }
}