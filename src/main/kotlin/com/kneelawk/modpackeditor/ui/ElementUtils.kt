package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.cache.ResourceCaches
import com.kneelawk.modpackeditor.curse.AddonUtils
import com.kneelawk.modpackeditor.data.manifest.FileJson
import javafx.scene.image.Image
import tornadofx.Controller

/**
 * Controller that handles some utility functions mainly for loading element data.
 */
class ElementUtils : Controller() {
    private val cache: ResourceCaches by inject()

    fun loadImage(file: FileJson?): Image {
        return cache.imageCache[AddonUtils.getIconUrl(file?.let { cache.addonCache[it.projectId].orNull() })]
    }

    fun loadModName(file: FileJson?): String {
        return file?.let { cache.addonCache[it.projectId].orNull()?.name } ?: "Unknown Addon"
    }

    fun loadModAuthor(file: FileJson?): String {
        return AddonUtils.getAuthorString(file?.let { cache.addonCache[it.projectId].orNull() })
    }

    fun loadModFileDisplay(file: FileJson?): String {
        return file?.let { cache.getAddonFile(it).orNull()?.displayName } ?: "Unknown"
    }

    fun loadModFileName(file: FileJson?): String {
        return file?.let { cache.getAddonFile(it).orNull()?.fileName } ?: "unknown"
    }
}
