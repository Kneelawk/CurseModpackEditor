package com.kneelawk.modpackeditor.ui.util

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
import tornadofx.Controller
import tornadofx.objectBinding

/**
 * Created by Kneelawk on 4/10/20.
 */
class ModListState : Controller() {
    private val model: ModpackModel by inject()
    private val editingMods = SimpleSetProperty<SimpleAddonId>(FXCollections.observableSet())

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
        model.modpackMods.replaceAll {
            if (it.projectId == oldAddon.projectId && it.fileId == oldAddon.fileId) {
                newAddon
            } else {
                it
            }
        }
    }

    fun modInstalledProperty(projectId: ObservableValue<Long>): BooleanBinding {
        return model.modpackMods.containsWhereProperty(projectId) { file, id -> file.projectId == id }
    }

    fun modFileInstalledProperty(addonId: ObservableValue<out AddonId?>): BooleanBinding {
        return model.modpackMods.containsWhereProperty(addonId) { file, id ->
            file.projectId == id?.projectId && file.fileId == id.fileId
        }
    }
}