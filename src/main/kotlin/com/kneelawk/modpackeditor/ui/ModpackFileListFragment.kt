package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.cache.ResourceCaches
import com.kneelawk.modpackeditor.curse.AddonUtils
import com.kneelawk.modpackeditor.data.manifest.FileJson
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * List fragment designed to show details about a mod in a modpack.
 */
class ModpackFileListFragment : ListCellFragment<FileJson>() {
    private val cache: ResourceCaches by inject()
    private var imageLoader: ImageLoader? = null
    private var modNameLoader: LabelLoader? = null
    private var modAuthorLoader: LabelLoader? = null
    private var modFileDisplayLoader: LabelLoader? = null
    private var modFileNameLoader: LabelLoader? = null

    override val root = hbox {
        spacing = 10.0
        imageview {
            imageLoader = ImageLoader(this, itemProperty, { loadImage(it) }, { image = it })
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
                    modNameLoader = LabelLoader(this, itemProperty, { loadModName(it) }, { text = it })
                }
                label("by")
                label {
                    modAuthorLoader = LabelLoader(this, itemProperty, { loadModAuthor(it) }, { text = it })
                }
            }
            hbox {
                spacing = 10.0
                alignment = Pos.BOTTOM_LEFT
                label {
                    modFileDisplayLoader =
                            LabelLoader(this, itemProperty, { loadModFileDisplay(it) }, { text = it })
                }
                label("-")
                label {
                    modFileNameLoader = LabelLoader(this, itemProperty, { loadModFileName(it) }, { text = it })
                }
            }
        }
    }

    private fun loadImage(file: FileJson?): Image {
        return cache.imageCache[AddonUtils.getIconUrl(file?.let { cache.addonCache[it.projectId].orNull() })]
    }

    private fun loadModName(file: FileJson?): String {
        return file?.let { cache.addonCache[it.projectId].orNull()?.name } ?: "Unknown Addon"
    }

    private fun loadModAuthor(file: FileJson?): String {
        return AddonUtils.getAuthorString(file?.let { cache.addonCache[it.projectId].orNull() })
    }

    private fun loadModFileDisplay(file: FileJson?): String {
        return file?.let { cache.getAddonFile(it).orNull()?.displayName } ?: "Unknown"
    }

    private fun loadModFileName(file: FileJson?): String {
        return file?.let { cache.getAddonFile(it).orNull()?.fileName } ?: "unknown"
    }
}

typealias ImageLoader = AsynchronousLoader<FileJson?, ImageView, Image>
typealias LabelLoader = AsynchronousLoader<FileJson?, Label, String>
