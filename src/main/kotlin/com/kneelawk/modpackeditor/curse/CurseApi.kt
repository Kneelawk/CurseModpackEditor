package com.kneelawk.modpackeditor.curse

import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.curseapi.AddonFileJson
import com.kneelawk.modpackeditor.data.curseapi.AddonJson
import com.kneelawk.modpackeditor.data.curseapi.MinecraftVersionJson
import com.kneelawk.modpackeditor.data.curseapi.ModLoaderListElementJson
import tornadofx.Controller
import tornadofx.Rest
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
}