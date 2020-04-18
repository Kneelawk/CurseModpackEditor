package com.kneelawk.modpackeditor.curse

import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.curseapi.*
import tornadofx.Controller
import tornadofx.Rest
import tornadofx.queryString
import tornadofx.toModel
import java.time.LocalDateTime

/**
 * Controller that interfaces with the Curse API.
 */
class CurseApi : Controller() {
    private val rest: Rest by inject()

    /**
     * Gets info about an addon.
     */
    fun getAddon(projectId: Long): AddonJson? {
        val response = rest.get("https://addons-ecs.forgesvc.net/api/v2/addon/$projectId")
        return if (response.status == Rest.Response.Status.OK) {
            response.one().toModel()
        } else {
            null
        }
    }

    /**
     * Gets an addon's details string.
     */
    fun getAddonDetails(projectId: Long): String? {
        val response = rest.get("https://addons-ecs.forgesvc.net/api/v2/addon/$projectId/description")
        return if (response.status == Rest.Response.Status.OK) {
            String(response.bytes())
        } else {
            null
        }
    }

    /**
     * Gets info about an addon file.
     */
    fun getAddonFile(addonId: AddonId): AddonFileJson? {
        val response =
                rest.get("https://addons-ecs.forgesvc.net/api/v2/addon/${addonId.projectId}/file/${addonId.fileId}")
        return if (response.status == Rest.Response.Status.OK) {
            response.one().toModel()
        } else {
            null
        }
    }

    /**
     * Gets info about an addon file.
     */
    fun getAddonFileChangelog(addonId: AddonId): String? {
        val response =
                rest.get(
                    "https://addons-ecs.forgesvc.net/api/v2/addon/${addonId.projectId}/file/${addonId.fileId}/changelog")
        return if (response.status == Rest.Response.Status.OK) {
            String(response.bytes())
        } else {
            null
        }
    }

    /**
     * Gets a list of addon files for the given project id.
     */
    fun getAddonFiles(projectId: Long): List<AddonFileJson>? {
        val response = rest.get("https://addons-ecs.forgesvc.net/api/v2/addon/$projectId/files")
        return if (response.status == Rest.Response.Status.OK) {
            response.list().toModel()
        } else {
            null
        }
    }

    /**
     * Gets the most recently released addon file for the given project id.
     */
    fun getLatestAddonFile(projectId: Long, minecraftVersion: String): AddonFileJson? {
        var newest: AddonFileJson? = null
        var newestDate: LocalDateTime? = null

        getAddonFiles(projectId)?.forEach { file ->
            if (file.gameVersion.contains(minecraftVersion) && (newestDate == null || file.fileDate.isAfter(
                        newestDate))) {
                newest = file
                newestDate = file.fileDate
            }
        }

        return newest
    }

    /**
     * Attempts to get the specified addon file but if it can't be found then it returns the latest addon file for the
     * addon's project.
     */
    fun getAddonFileOrLatest(addonId: AddonId, minecraftVersion: String): AddonFileJson? {
        return getAddonFile(addonId) ?: getLatestAddonFile(addonId.projectId, minecraftVersion)
    }

    /**
     * Gets info about a single category.
     */
    fun getCategoryInfo(categoryId: Long): CategoryListElementData? {
        val response = rest.get("https://addons-ecs.forgesvc.net/api/v2/category/$categoryId")
        return if (response.ok()) {
            response.one().toModel<CategoryListElementJson>()
        } else {
            null
        }
    }

    /**
     * Gets Curse's list of categories.
     */
    fun getCategoryList(): List<CategoryListElementData> {
        return rest.get("https://addons-ecs.forgesvc.net/api/v2/category").list().toModel<CategoryListElementJson>()
    }

    /**
     * Gets the list of minecraft versions.
     */
    fun getMinecraftVersionList(): List<MinecraftVersionJson> {
        return rest.get("https://addons-ecs.forgesvc.net/api/v2/minecraft/version").list().toModel()
    }

    /**
     * Gets the list of mod loaders.
     */
    fun getModLoaderList(): List<ModLoaderListElementJson> {
        return rest.get("https://addons-ecs.forgesvc.net/api/v2/minecraft/modloader").list().toModel()
    }

    /**
     * Searches Curse's addon database using specified criteria.
     */
    fun getCurseAddonSearch(gameId: Long = 432, gameVersion: String? = null, sectionId: Long? = null,
                            categoryId: Long? = null, searchFilter: String? = null, sort: Long = 1, pageSize: Long = 20,
                            index: Long = 0): List<AddonData> {
        val queryParams = hashMapOf<String, Any>()
        queryParams += "gameId" to gameId
        gameVersion?.let { queryParams += "gameVersion" to it }
        sectionId?.let { queryParams += "sectionId" to it }
        categoryId?.let { queryParams += "categoryId" to it }
        searchFilter?.let { queryParams += "searchFilter" to it }
        queryParams += "sort" to sort
        queryParams += "pageSize" to pageSize
        queryParams += "index" to index

        return rest.get("https://addons-ecs.forgesvc.net/api/v2/addon/search${queryParams.queryString}").list()
                .toModel<AddonJson>()
    }
}