package com.kneelawk.modpackeditor.ui

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Paths

/**
 * View used to specify the initial details of a modpack.
 */
class CreateModpackView : View("Create Modpack") {
    private val c: CreateModpackController by inject()

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0
        enableWhen(c.running.not())
        add<ModpackDetailsView>()
        buttonbar {
            button("Cancel") {
                action {
                    replaceWith(find<ModpackEditorStartView>())
                }
            }
            button("Create Modpack") {
                enableWhen(c.model.valid)
                action {
                    c.createModpack()
                }
            }
        }
    }

    init {
        c.model.validate(decorateErrors = false)
    }

    override fun onBeforeShow() {
        with(currentStage!!) {
            width = 1280.0
            height = 800.0
            minWidth = 500.0
            minHeight = 400.0
        }
    }
}