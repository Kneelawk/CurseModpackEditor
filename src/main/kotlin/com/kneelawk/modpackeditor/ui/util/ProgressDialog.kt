package com.kneelawk.modpackeditor.ui.util

import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Created by Kneelawk on 4/12/20.
 */
class ProgressDialog : Fragment() {
    val titleString: String by param("Progress...")
    val progressProperty: ObservableValue<Number> by param()
    val statusProperty: ObservableValue<String> by param()
    val openProperty: ObservableValue<Boolean> by param()
    val cancelCallback: (() -> Unit)? by param<(() -> Unit)?>(null)

    var cancelled = false

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0
        minWidth = 500.0

        label(titleString) {
            style {
                fontSize = 18.px
                fontWeight = FontWeight.BOLD
            }
        }
        label(statusProperty)
        progressbar(progressProperty) {
            maxWidth = Double.MAX_VALUE
        }
        cancelCallback?.let {
            hbox {
                region {
                    hgrow = Priority.ALWAYS
                }
                button("Cancel") {
                    action {
                        cancelled = true
                        cancelCallback!!()
                        close()
                    }
                }
            }
        }
    }

    override fun onDock() {
        openProperty.onChange {
            if (it == false) {
                close()
            }
        }
        titleProperty.bind(SimpleStringProperty(titleString))
    }

    override fun onUndock() {
        if (!cancelled && cancelCallback != null) {
            cancelCallback!!()
        }
    }
}
