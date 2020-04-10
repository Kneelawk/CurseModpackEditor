package com.kneelawk.modpackeditor.ui

import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Created by Kneelawk on 4/9/20.
 */
class ErrorOpeningModpackDialog : Fragment("Error Opening Modpack") {
    val callback: () -> Unit by param {}
    val modpackName: String by param()

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0
        label("Error Opening Modpack") {
            style {
                fontSize = 18.px
                fontWeight = FontWeight.BOLD
            }
        }
        label("Error opening $modpackName")
        label("Maybe it is not a curse modpack?")
        hbox {
            region {
                hgrow = Priority.ALWAYS
            }
            button("Ok") {
                action {
                    close()
                }
            }
        }
    }

    override fun onUndock() {
        callback()
    }
}