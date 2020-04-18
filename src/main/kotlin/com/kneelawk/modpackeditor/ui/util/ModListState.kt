package com.kneelawk.modpackeditor.ui.util

import com.kneelawk.modpackeditor.cache.ResourceCaches
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.SimpleAddonId
import com.kneelawk.modpackeditor.data.curseapi.AddonFileData
import com.kneelawk.modpackeditor.data.manifest.FileJson
import com.kneelawk.modpackeditor.data.version.MinecraftVersion
import com.kneelawk.modpackeditor.ui.ModpackModel
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleSetProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.concurrent.Task
import tornadofx.Controller
import tornadofx.runLater
import tornadofx.task

/**
 * Created by Kneelawk on 4/10/20.
 */
class ModListState : Controller() {
    private val model: ModpackModel by inject()
    private val cache: ResourceCaches by inject()

    private val editingMods = SimpleSetProperty<Long>(FXCollections.observableSet())
    val filterMinecraftVersion = SimpleBooleanProperty(true)
    val lowMinecraftVersion = SimpleObjectProperty(MinecraftVersion.parse(model.minecraftVersion.value))
    val highMinecraftVersion = SimpleObjectProperty(MinecraftVersion.parse(model.minecraftVersion.value))

    fun startEditing(projectId: Long) {
        editingMods.add(projectId)
    }

    fun finishEditing(projectId: Long) {
        editingMods.remove(projectId)
    }

    fun notEditingProperty(property: ObservableValue<Long?>): BooleanBinding {
        return editingMods.containsProperty(property).not()
    }

    fun addAddon(addonId: AddonId) {
        val oldAddon = model.modpackMods.find { it.projectId == addonId.projectId }
        if (oldAddon == null) {
            model.modpackMods.add(addonId.toFileJson(true))
        } else {
            replaceAddon(oldAddon.projectId, addonId.toFileJson(oldAddon.required))
        }
    }

    fun replaceAddon(oldProject: Long, newAddon: FileJson) {
        model.modpackMods.replaceAll {
            if (it.projectId == oldProject) {
                newAddon
            } else {
                it
            }
        }
    }

    fun updateAddons(updates: List<AddonUpdate>) {
        model.modpackMods.replaceAll { file ->
            val update = updates.find {
                it.projectId == file.projectId
            }
            update?.let { FileJson(it.projectId, it.newFileId, true) } ?: file
        }
    }

    fun removeAddon(projectId: Long) {
        editingMods.removeIf { it == projectId }
        model.modpackMods.removeIf { it.projectId == projectId }
    }

    fun sortAddons(): Task<Unit> {
        val editingList = ArrayList(model.modpackMods)
        return task {
            val sortCached = HashSet<SimpleAddonId>()
            editingList.sortBy {
                if (!isCancelled) {
                    updateMessage("Getting info: ${it.projectId}/${it.fileId}")
                    val name = cache.addonCache[it.projectId].orNull()?.name?.toLowerCase() ?: ""
                    sortCached.add(SimpleAddonId(it))
                    updateProgress(sortCached.size.toLong(), editingList.size.toLong())
                    name
                } else {
                    it.projectId.toString()
                }
            }

            updateMessage("Sorting finished.")
            updateProgress(editingList.size.toLong(), editingList.size.toLong())

            runLater {
                if (!isCancelled) {
                    model.modpackMods.setAll(editingList)
                }
            }
        }
    }

    fun modInstalledProperty(projectId: ObservableValue<out Number?>): BooleanBinding {
        return model.modpackMods.containsWhereProperty(projectId) { file, id -> file.projectId == id?.toLong() }
    }

    fun modFileInstalledProperty(addonId: AddonId): BooleanBinding {
        return model.modpackMods.containsWhereProperty { it.projectId == addonId.projectId && it.fileId == addonId.fileId }
    }

    fun modFileInstalledProperty(addonId: ObservableValue<out AddonId?>): BooleanBinding {
        return model.modpackMods.containsWhereProperty(addonId) { file, id ->
            file.projectId == id?.projectId && file.fileId == id.fileId
        }
    }

    fun containsByMinecraftVersion(files: List<AddonFileData>): Boolean {
        val low = lowMinecraftVersion.value
        val high = highMinecraftVersion.value
        return files.find { file ->
            file.gameVersion.find { version ->
                MinecraftVersion.tryParse(version)?.let {
                    it >= low && it <= high
                } ?: false
            } != null
        } != null
    }

    fun filterByMinecraftVersion(files: List<AddonFileData>): List<AddonFileData> {
        val low = lowMinecraftVersion.value
        val high = highMinecraftVersion.value
        return files.filter { file ->
            file.gameVersion.find { version ->
                MinecraftVersion.tryParse(version)?.let {
                    it >= low && it <= high
                } ?: false
            } != null
        }
    }

    fun maybeFilterByMinecraftVersion(files: List<AddonFileData>): List<AddonFileData> {
        return if (filterMinecraftVersion.value) {
            filterByMinecraftVersion(files)
        } else {
            files
        }
    }
}