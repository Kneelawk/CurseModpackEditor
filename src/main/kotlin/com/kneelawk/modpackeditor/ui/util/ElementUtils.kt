package com.kneelawk.modpackeditor.ui.util

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

    fun loadImage(projectId: Long?): Image {
        return cache.imageCache[AddonUtils.getIconUrl(projectId?.let { cache.addonCache[it].orNull() })]
    }

    fun loadImage(file: FileJson?): Image {
        return loadImage(file?.projectId)
    }

    fun unescape(str: String): String {
        var newStr = str

        // remove quotation marks
        if (newStr.startsWith("\"")) {
            newStr = newStr.substring(1)
        }
        if (newStr.endsWith("\"")) {
            newStr = newStr.substring(0, newStr.lastIndex)
        }

        newStr = newStr.replace("\\n", "\n")
        newStr = newStr.replace("\\r", "\r")
        newStr = newStr.replace("\\t", "\t")
        newStr = newStr.replace("\\\"", "\"")
        newStr = newStr.replace("\\\\", "\\")

        return newStr
    }

    fun loadModDetails(projectId: Long?): String {
        return projectId?.let { id -> cache.detailsCache[id].orNull()?.let { unescape(it) } }
                ?: "Unable to load details."
    }

    fun loadModName(projectId: Long?): String {
        return projectId?.let { cache.addonCache[it].orNull()?.name } ?: "Unknown Addon"
    }

    fun loadModName(file: FileJson?): String {
        return loadModName(file?.projectId)
    }

    fun loadModAuthor(projectId: Long?): String {
        return AddonUtils.getAuthorString(projectId?.let { cache.addonCache[it].orNull() })
    }

    fun loadModAuthor(file: FileJson?): String {
        return loadModAuthor(file?.projectId)
    }

    fun loadModFileDisplay(file: FileJson?): String {
        return file?.let { cache.getAddonFile(it).orNull()?.displayName } ?: "Unknown"
    }

    fun loadModFileName(file: FileJson?): String {
        return file?.let { cache.getAddonFile(it).orNull()?.fileName } ?: "unknown"
    }
}
