package com.kneelawk.modpackeditor.ui

import tornadofx.*
import java.nio.file.Files
import java.nio.file.Paths

/**
 * View dedicated to showing the details about a modpack.
 */
class ModpackDetailsView : View() {
    val c: ModpackDetailsController by inject()

    override val root = form {
        fieldset("File") {
            field("Modpack Location:") {
                textfield(c.model.modpackLocation).validator { path ->
                    when {
                        path.isNullOrBlank() -> error("Modpack Location is blank.")
                        Paths.get(path).parent?.let { parent -> !Files.exists(parent) } ?: false -> error(
                            "Destination directory does not exist.")
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
        fieldset("Details") {
            field("Modpack Name:") {
                textfield(c.model.modpackName).required()
            }
            field("Modpack Author:") {
                textfield(c.model.modpackAuthor).required()
            }
            field("Modpack Version:") {
                textfield(c.model.modpackVersion).required()
            }
        }
        fieldset("Versions") {
            field("Minecraft Version:") {
                textfield(c.model.minecraftVersion) {
                    isEditable = false
                    required()
                }
                button("...") {
                    action {
                        c.selectMinecraftVersion()
                    }
                }
            }
            field("Forge Version:") {
                textfield(c.model.modLoaderVersion) {
                    isEditable = false
                    required()
                }
                button("...") {
                    action {
                        c.selectModLoader()
                    }
                }
            }
            label("Note: Curse modpacks are required to use Forge. " +
                    "In order to use a different mod loader, you can install a hijacking mod like JumpLoader.")
        }
    }
}