package com.kneelawk.modpackeditor.ui.util

import com.kneelawk.modpackeditor.cache.ResourceCaches
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.SimpleAddonId
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
import tornadofx.objectBinding
import tornadofx.runLater
import tornadofx.task

/**
 * Created by Kneelawk on 4/10/20.
 */
class ModListState : Controller() {
    private val model: ModpackModel by inject()
    private val editingMods = SimpleSetProperty<SimpleAddonId>(FXCollections.observableSet())
    private val cache: ResourceCaches by inject()

    val filterMinecraftVersion = SimpleBooleanProperty(true)
    val lowMinecraftVersion = SimpleObjectProperty(MinecraftVersion.parse(model.minecraftVersion.value))
    val highMinecraftVersion = SimpleObjectProperty(MinecraftVersion.parse(model.minecraftVersion.value))

    fun startEditing(addonId: AddonId) {
        editingMods.add(SimpleAddonId(addonId))
    }

    fun finishEditing(addonId: AddonId) {
        editingMods.remove(SimpleAddonId(addonId))
    }

    fun replaceEditing(oldAddon: AddonId, newAddon: AddonId) {
        editingMods.remove(SimpleAddonId(oldAddon))
        editingMods.add(SimpleAddonId(newAddon))
    }

    fun notEditingProperty(property: ObservableValue<out AddonId>): BooleanBinding {
        return editingMods.containsProperty(property.objectBinding { it?.let { SimpleAddonId(it) } }).not()
    }

    fun replaceAddon(oldAddon: AddonId, newAddon: FileJson) {
        val oldId = SimpleAddonId(oldAddon)
        val newId = SimpleAddonId(newAddon)
        if (oldId != newId && editingMods.contains(oldId)) {
            editingMods.remove(oldId)
            editingMods.add(newId)
        }

        model.modpackMods.replaceAll {
            if (it.projectId == oldAddon.projectId && it.fileId == oldAddon.fileId) {
                newAddon
            } else {
                it
            }
        }
    }

    fun updateAddons(updates: List<AddonUpdate>) {
        updates.forEach {
            val oldId = SimpleAddonId(it.oldVersion)
            if (editingMods.contains(oldId)) {
                editingMods.remove(oldId)
                editingMods.add(SimpleAddonId(it.newVersion))
            }
        }

        model.modpackMods.replaceAll { file ->
            updates.find {
                it.oldVersion.projectId == file.projectId && it.oldVersion.fileId == file.fileId
            }?.newVersion?.toFileJson(file.required) ?: file
        }
    }

    fun removeAddon(addonId: AddonId) {
        val condition = { it: AddonId -> it.projectId == addonId.projectId && it.fileId == addonId.fileId }
        editingMods.removeIf(condition)
        model.modpackMods.removeIf(condition)
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

    fun modInstalledProperty(projectId: ObservableValue<Long>): BooleanBinding {
        return model.modpackMods.containsWhereProperty(projectId) { file, id -> file.projectId == id }
    }

    fun modFileInstalledProperty(addonId: AddonId): BooleanBinding {
        return model.modpackMods.containsWhereProperty { it.projectId == addonId.projectId && it.fileId == addonId.fileId }
    }

    fun modFileInstalledProperty(addonId: ObservableValue<out AddonId?>): BooleanBinding {
        return model.modpackMods.containsWhereProperty(addonId) { file, id ->
            file.projectId == id?.projectId && file.fileId == id.fileId
        }
    }
}