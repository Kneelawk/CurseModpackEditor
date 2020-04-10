package com.kneelawk.modpackeditor.ui

import javafx.scene.control.Button
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Custom dialog for confirming something.
 */
class AreYouSureDialog : Fragment("Are You Sure?") {
    val prompt: String by param("Are You Sure?")
    val callback: (Result) -> Unit by param { _ -> }

    var confirmButton: Button? = null

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0
        label("Are You Sure?") {
            style {
                fontSize = 18.px
                fontWeight = FontWeight.BOLD
            }
        }
        label(prompt)
        hbox {
            spacing = 10.0
            region {
                hgrow = Priority.ALWAYS
            }
            button("Cancel") {
                action {
                    close()
                    callback(Result.Cancel)
                }
            }
            confirmButton = button("Confirm") {
                isDefaultButton = true
                action {
                    close()
                    callback(Result.Confirm)
                }
            }
        }
    }

    override fun onDock() {
        confirmButton!!.requestFocus()
    }

    sealed class Result {
        object Confirm : Result()
        object Cancel : Result()
    }
}