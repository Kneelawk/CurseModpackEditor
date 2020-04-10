package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.data.manifest.FileJson
import com.kneelawk.modpackeditor.ui.util.*
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * List fragment designed to show details about a mod in a modpack.
 */
class ModpackFileListFragment : ListCellFragment<FileJson>() {
    private val c: ModpackModListController by inject()
    private val elementUtils: ElementUtils by inject()
    private var imageLoader: ImageLoader? = null
    private var modNameLoader: LabelLoader? = null
    private var modAuthorLoader: LabelLoader? = null
    private var modFileDisplayLoader: LabelLoader? = null
    private var modFileNameLoader: LabelLoader? = null

    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        alignment = Pos.CENTER
        imageview {
            imageLoader =
                    ImageLoader(this, itemProperty, { image = null }, { elementUtils.loadImage(it) }, { image = it })
        }
        vbox {
            spacing = 10.0
            alignment = Pos.CENTER_LEFT
            hbox {
                spacing = 10.0
                alignment = Pos.BOTTOM_LEFT
                label {
                    style {
                        fontWeight = FontWeight.BOLD
                        fontSize = 16.px
                    }
                    modNameLoader = LabelLoader(this, itemProperty, { text = it?.projectId?.toString() ?: "" },
                        { elementUtils.loadModName(it) }, { text = it })
                }
                label("by")
                label {
                    modAuthorLoader =
                            LabelLoader(this, itemProperty, { text = "Loading..." }, { elementUtils.loadModAuthor(it) },
                                { text = it })
                }
            }
            hbox {
                spacing = 10.0
                alignment = Pos.BOTTOM_LEFT
                label {
                    modFileDisplayLoader =
                            LabelLoader(this, itemProperty, { text = it?.fileId?.toString() ?: "" },
                                { elementUtils.loadModFileDisplay(it) }, { text = it })
                }
                label("-")
                label {
                    modFileNameLoader =
                            LabelLoader(this, itemProperty, { text = "loading..." },
                                { elementUtils.loadModFileName(it) }, { text = it })
                }
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
                    enableWhen(itemProperty.isNotNull.and(c.notEditingProperty(itemProperty)))
                    action {
                        c.startEditing(item)
                        fire(
                            ModRequiredEvent(item, !item.required,
                                scope))
                    }
                }
            }
        }
        region {
            hgrow = Priority.ALWAYS
        }
        hbox {
            alignment = Pos.CENTER
            vbox {
                alignment = Pos.CENTER
                button("Details") {
                    maxWidth = Double.MAX_VALUE
                    enableWhen(itemProperty.isNotNull.and(c.notEditingProperty(itemProperty)))
                    action {
                        c.startEditing(item)
                        fire(ModDetailsEvent(item, scope))
                    }
                }
                button("Changelog") {
                    maxWidth = Double.MAX_VALUE
                    enableWhen(itemProperty.isNotNull.and(c.notEditingProperty(itemProperty)))
                    action {
                        c.startEditing(item)
                        fire(ModChangelogEvent(item, scope))
                    }
                }
            }
            button("Remove") {
                enableWhen(itemProperty.isNotNull.and(c.notEditingProperty(itemProperty)))
                action {
                    c.startEditing(item)
                    fire(ModRemoveEvent(item, scope))
                }
            }
        }
    }
}

typealias ImageLoader = AsynchronousLoader<FileJson?, ImageView, Image>
typealias LabelLoader = AsynchronousLoader<FileJson?, Label, String>
