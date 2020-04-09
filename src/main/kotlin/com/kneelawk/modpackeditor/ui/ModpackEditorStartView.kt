package com.kneelawk.modpackeditor.ui

import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Modpack Editor Main View.
 */
class ModpackEditorStartView : View("Modpack Editor") {
    private val c: ModpackEditorStartController by inject()

    init {
        with(primaryStage) {
            width = 1280.0
            height = 800.0
            minWidth = 500.0
            minHeight = 400.0
        }
    }

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0
        alignment = Pos.CENTER

        enableWhen(c.running.not())

        label("Modpack Editor") {
            style {
                fontSize = 36.px
                fontWeight = FontWeight.BOLD
            }
        }

        button("Create New Modpack") {
            prefWidth = 300.0
            action {
                replaceWith(find<CreateModpackView>())
            }
        }

        button("Open Existing Modpack") {
            prefWidth = 300.0
            action {
                c.openModpack()
            }
        }
    }
}