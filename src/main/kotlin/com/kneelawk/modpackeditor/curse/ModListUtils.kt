package com.kneelawk.modpackeditor.curse

import arrow.core.Either
import arrow.core.Left
import arrow.core.rightIfNotNull
import com.kneelawk.modpackeditor.cache.ResourceCaches
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.SimpleAddonId
import com.kneelawk.modpackeditor.data.curseapi.AddonFileData
import com.kneelawk.modpackeditor.data.version.MinecraftVersion
import javafx.concurrent.Task
import tornadofx.Controller
import tornadofx.task

/**
 * Stateless utilities for working with mod lists and related things.
 */
class ModListUtils : Controller() {
    private val cache: ResourceCaches by inject()
    private val curseApi: CurseApi by inject()

    fun <A : AddonId> sortAddonsTask(addons: Collection<A>): Task<List<A>> {
        val editingList = addons.toMutableList()
        return task {
            updateMessage("Sorting addons...")
            updateProgress(0, editingList.size.toLong())

            val sortCached = HashSet<SimpleAddonId>()
            editingList.sortBy {
                if (!isCancelled) {
                    val name = cache.addonCache[it.projectId].orNull()?.name ?: ""
                    sortCached.add(SimpleAddonId(it))
                    updateMessage("Sorting, got info: $name")
                    updateProgress(sortCached.size.toLong(), editingList.size.toLong())
                    name.toLowerCase()
                } else {
                    it.projectId.toString()
                }
            }

            updateMessage("Sorting finished.")
            updateProgress(editingList.size.toLong(), editingList.size.toLong())

            editingList
        }
    }

//    fun collectDependenciesTask(addons: List<AddonId>, selectedVersions: Map<Long, Long>, ignored: Set<Long>,
//                                lowMinecraftVersion: MinecraftVersion,
//                                highMinecraftVersion: MinecraftVersion): Task<CollectDependenciesResult> {
//        return CollectDependenciesTask(addons, selectedVersions, ignored, lowMinecraftVersion,
//            highMinecraftVersion).flatMap { res ->
//            sortAddonsTask(res.required).map {
//                CollectDependenciesResult(it, res.unresolved)
//            }
//        }.execAsync()
//    }

    fun containsByMinecraftVersion(files: List<AddonFileData>, low: MinecraftVersion, high: MinecraftVersion): Boolean {
        return files.find { file ->
            file.gameVersion.find { version ->
                MinecraftVersion.tryParse(version)?.let {
                    it in low..high
                } ?: false
            } != null
        } != null
    }

    fun hasForMinecraftVersion(projectId: Long, low: MinecraftVersion, high: MinecraftVersion): Boolean {
        val files = curseApi.getAddonFiles(projectId).orEmpty()
        return containsByMinecraftVersion(files, low, high)
    }

    fun filterByMinecraftVersion(
        files: List<AddonFileData>, low: MinecraftVersion,
        high: MinecraftVersion
    ): List<AddonFileData> {
        return files.filter { file ->
            file.gameVersion.find { version ->
                MinecraftVersion.tryParse(version)?.let {
                    it in low..high
                } ?: false
            } != null
        }
    }

    fun latestByMinecraftVersion(
        files: List<AddonFileData>, low: MinecraftVersion,
        high: MinecraftVersion
    ): AddonFileData? {
        return filterByMinecraftVersion(files, low, high).maxBy { it.fileDate }
    }

    fun latestByMinecraftVersion(
        projectId: Long, low: MinecraftVersion,
        high: MinecraftVersion
    ): Either<AddonVersionSelectionError, AddonFileData> {
        val files = curseApi.getAddonFiles(projectId) ?: return Left(AddonVersionSelectionError.UnknownAddon)
        if (files.isEmpty()) return Left(AddonVersionSelectionError.NoFiles)
        return latestByMinecraftVersion(files, low, high).rightIfNotNull {
            AddonVersionSelectionError.DifferentMinecraftVersion(
                files.maxBy { it.fileDate }!!.gameVersion.mapNotNull { MinecraftVersion.tryParse(it) })
        }
    }
}
