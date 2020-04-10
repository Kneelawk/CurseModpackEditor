package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.data.manifest.FileJson
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
    private val elementUtils: ElementUtils by inject()
    private var imageLoader: ImageLoader? = null
    private var modNameLoader: LabelLoader? = null
    private var modAuthorLoader: LabelLoader? = null
    private var modFileDisplayLoader: LabelLoader? = null
    private var modFileNameLoader: LabelLoader? = null

    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        imageview {
            imageLoader = ImageLoader(this, itemProperty, { elementUtils.loadImage(it) }, { image = it })
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
                    modNameLoader = LabelLoader(this, itemProperty, { elementUtils.loadModName(it) }, { text = it })
                }
                label("by")
                label {
                    modAuthorLoader = LabelLoader(this, itemProperty, { elementUtils.loadModAuthor(it) }, { text = it })
                }
            }
            hbox {
                spacing = 10.0
                alignment = Pos.BOTTOM_LEFT
                label {
                    modFileDisplayLoader =
                            LabelLoader(this, itemProperty, { elementUtils.loadModFileDisplay(it) }, { text = it })
                }
                label("-")
                label {
                    modFileNameLoader =
                            LabelLoader(this, itemProperty, { elementUtils.loadModFileName(it) }, { text = it })
                }
            }
            checkbox("Required") {
                item?.let { isSelected = it.required }
                itemProperty.onChange { it?.let { isSelected = it.required } }
                action {
                    val required = isSelected
                    if (required != item.required) {
                        fire(ModRequiredEvent(item, isSelected, scope))
                    }
                }
            }
        }
        region {
            hgrow = Priority.ALWAYS
        }
        hbox {
            alignment = Pos.CENTER
            button("Remove") {
                enableWhen(itemProperty.isNotNull)
                action {
                    fire(ModRemoveEvent(item, scope))
                }
            }
        }
    }
}

typealias ImageLoader = AsynchronousLoader<FileJson?, ImageView, Image>
typealias LabelLoader = AsynchronousLoader<FileJson?, Label, String>
