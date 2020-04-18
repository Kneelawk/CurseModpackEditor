package com.kneelawk.modpackeditor.ui.util

import com.kneelawk.modpackeditor.cache.ResourceCaches
import com.kneelawk.modpackeditor.curse.AddonUtils
import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.CategoryList
import com.kneelawk.modpackeditor.data.curseapi.AddonData
import javafx.scene.image.Image
import tornadofx.Controller

/**
 * Controller that handles some utility functions mainly for loading element data.
 */
class ElementUtils : Controller() {
    private val cache: ResourceCaches by inject()
    private val curseApi: CurseApi by inject()

    fun loadTinyImage(url: String?): Image {
        return cache.tinyImageCache[AddonUtils.getIconUrl(url)]
    }

    fun loadSmallImage(url: String?): Image {
        return cache.smallImageCache[AddonUtils.getIconUrl(url)]
    }

    fun loadSmallImage(projectId: Long?): Image {
        return cache.smallImageCache[AddonUtils.getIconUrl(projectId?.let { cache.addonCache[it].orNull() })]
    }

    fun loadImage(url: String?): Image {
        return cache.imageCache[AddonUtils.getIconUrl(url)]
    }

    fun loadImage(addon: AddonData?): Image {
        return cache.imageCache[AddonUtils.getIconUrl(addon)]
    }

    fun loadImage(projectId: Long?): Image {
        return loadImage(projectId?.let { cache.addonCache[it].orNull() })
    }

    fun loadImage(file: AddonId?): Image {
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

    fun loadModName(file: AddonId?): String {
        return loadModName(file?.projectId)
    }

    fun loadModAuthor(projectId: Long?): String {
        return AddonUtils.getAuthorString(projectId?.let { cache.addonCache[it].orNull() })
    }

    fun loadModAuthor(file: AddonId?): String {
        return loadModAuthor(file?.projectId)
    }

    fun loadModFileChangelog(file: AddonId?): String {
        return file?.let { id -> cache.getAddonFileChangelog(id).orNull()?.let { unescape(it) } }
                ?: "Unable to load changelog."
    }

    fun loadModFileDisplay(file: AddonId?): String {
        return file?.let { cache.getAddonFile(it).orNull()?.displayName } ?: "Unknown"
    }

    fun loadModFileGameVersions(file: AddonId?): List<String> {
        return file?.let { cache.getAddonFile(it).orNull()?.gameVersion } ?: listOf("Unknown")
    }

    fun loadModFileName(file: AddonId?): String {
        return file?.let { cache.getAddonFile(it).orNull()?.fileName } ?: "unknown"
    }

    fun loadModCategories(): CategoryList {
        val categories = curseApi.getCategoryList()
        return CategoryList(categories.find { it.id == 6L }!!, categories.filter { it.rootGameCategoryId == 6L })
    }
}
