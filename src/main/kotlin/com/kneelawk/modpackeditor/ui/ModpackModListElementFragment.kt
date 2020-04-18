package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.data.manifest.FileJson
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import com.kneelawk.modpackeditor.ui.util.ModListState
import com.kneelawk.modpackeditor.ui.util.asyncExpression
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * List fragment designed to show details about a mod in a modpack.
 */
class ModpackModListElementFragment : ListCellFragment<FileJson>() {
    val modRequireCallback: (FileJson, Boolean) -> Unit by param()
    val modDetailsCallback: (FileJson) -> Unit by param()
    val modRemoveCallback: (FileJson) -> Unit by param()
    val modFileDetailsCallback: (FileJson) -> Unit by param()
    val modChangeVersionCallback: (FileJson) -> Unit by param()

    private val modListState: ModListState by inject()
    private val elementUtils: ElementUtils by inject()

    private val notEditingProperty = modListState.notEditingProperty(itemProperty.objectBinding { it?.projectId })

    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        alignment = Pos.CENTER
        imageview(itemProperty.asyncExpression({ null }, { elementUtils.loadImage(it) }))
        vbox {
            spacing = 10.0
            alignment = Pos.CENTER_LEFT
            hbox {
                spacing = 10.0
                alignment = Pos.BOTTOM_LEFT
                label(itemProperty.asyncExpression({ it?.projectId?.toString() ?: "" },
                    { elementUtils.loadModName(it) })) {
                    style {
                        fontWeight = FontWeight.BOLD
                        fontSize = 16.px
                    }
                }
                label("by")
                label(itemProperty.asyncExpression({ "Loading..." }, { elementUtils.loadModAuthor(it) }))
            }
            hbox {
                spacing = 10.0
                alignment = Pos.BOTTOM_LEFT
                label(itemProperty.asyncExpression({ it?.fileId?.toString() ?: "" },
                    { elementUtils.loadModFileDisplay(it) }))
                label("-")
                label(itemProperty.asyncExpression({ "loading..." }, { elementUtils.loadModFileName(it) }))
            }
            hbox {
                spacing = 10.0
                alignment = Pos.CENTER_LEFT
                label(itemProperty.stringBinding {
                    when (it?.required) {
                        true -> {
                            "Status: Enabled"
                        }
                        false -> {
                            "Status: Disabled"
                        }
                        else -> {
                            "Status: Unknown"
                        }
                    }
                }) {
                    itemProperty.onChange {
                        if (it?.required == true) {
                            addPseudoClass("mod-enabled")
                        } else {
                            removePseudoClass("mod-enabled")
                        }
                        if (it?.required == false) {
                            addPseudoClass("mod-disabled")
                        } else {
                            removePseudoClass("mod-disabled")
                        }
                    }
                }
                button(itemProperty.stringBinding {
                    when (it?.required) {
                        true -> {
                            "Disable"
                        }
                        false -> {
                            "Enable"
                        }
                        else -> {
                            "Unknown"
                        }
                    }
                }) {
                    enableWhen(itemProperty.isNotNull.and(notEditingProperty))
                    action {
                        modRequireCallback(item, !item.required)
                    }
                }
            }
        }
        region {
            hgrow = Priority.ALWAYS
        }
        gridpane {
            alignment = Pos.CENTER
            hgap = 5.0
            vgap = 5.0
            row {
                button("Details") {
                    maxWidth = Double.MAX_VALUE
                    enableWhen(itemProperty.isNotNull.and(notEditingProperty))
                    action {
                        modDetailsCallback(item)
                    }
                }
                button("Remove") {
                    maxWidth = Double.MAX_VALUE
                    enableWhen(itemProperty.isNotNull.and(notEditingProperty))
                    action {
                        modRemoveCallback(item)
                    }
                }
            }
            row {
                button("File Details") {
                    maxWidth = Double.MAX_VALUE
                    enableWhen(itemProperty.isNotNull.and(notEditingProperty))
                    action {
                        modFileDetailsCallback(item)
                    }
                }
                button("Change Version") {
                    maxWidth = Double.MAX_VALUE
                    enableWhen(itemProperty.isNotNull.and(notEditingProperty))
                    action {
                        modChangeVersionCallback(item)
                    }
                }
            }
        }
    }
}
