package com.kneelawk.modpackeditor.ui.update

import com.google.common.collect.ImmutableList
import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.SimpleAddonId
import com.kneelawk.modpackeditor.data.version.MinecraftVersion
import com.kneelawk.modpackeditor.ui.ModpackModel
import com.kneelawk.modpackeditor.ui.mods.ModFileDetailsFragment
import com.kneelawk.modpackeditor.ui.mods.ModVersionListFragment
import com.kneelawk.modpackeditor.ui.util.AddonUpdate
import com.kneelawk.modpackeditor.ui.util.ModListState
import com.kneelawk.modpackeditor.ui.util.ObjectPropertyWrapper
import com.kneelawk.modpackeditor.ui.util.mapProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.stage.Modality
import tornadofx.Controller
import tornadofx.runLater
import tornadofx.task

/**
 * Controller for managing a modpack update.
 */
class ModpackUpdateController : Controller() {
    val model: ModpackModel by inject()
    private val curseApi: CurseApi by inject()
    private val modListState: ModListState by inject()

    val running = SimpleBooleanProperty(false)
    val collectProgress = SimpleDoubleProperty(0.0)
    private var collectProgressWrapper: Number by ObjectPropertyWrapper(collectProgress)
    val collectStatus = SimpleStringProperty("Not started.")
    private var collectStatusWrapper: String by ObjectPropertyWrapper(collectStatus)
    val updateElements = SimpleListProperty<ModpackUpdateListElement>(FXCollections.observableArrayList())
    private val selectedAddonIds = updateElements.mapProperty { it.update.newVersion }

    fun collectModUpdates() {
        val mods = ImmutableList.copyOf(model.modpackMods)

        collectProgressWrapper = 0.0
        task {
            val collected = ArrayList<ModpackUpdateListElement>()

            mods.forEachIndexed { index, file ->
                collectStatusWrapper = "Getting info about project ${file.projectId}."

                getModUpdate(file)?.let {
                    collected += ModpackUpdateListElement(SimpleBooleanProperty(true), it)
                }

                collectProgressWrapper = (index.toDouble() + 1) / mods.size.toDouble()
            }

            collectStatusWrapper = "Updates collected."
            collectProgressWrapper = 1.0

            runLater {
                updateElements.setAll(collected)
                running.value = false
            }
        }
    }

    private fun getModUpdate(addonId: AddonId): AddonUpdate? {
        val file = curseApi.getAddonFiles(addonId.projectId).orEmpty().filter { file ->
            file.gameVersion.find { version ->
                MinecraftVersion.tryParse(version)?.let {
                    it >= modListState.lowMinecraftVersion.value && it <= modListState.highMinecraftVersion.value
                } ?: false
            } != null
        }.maxBy { it.fileDate }

        return if (file != null && file.id != addonId.fileId) {
            AddonUpdate(addonId, SimpleAddonId(addonId.projectId, file.id))
        } else {
            null
        }
    }

    fun showModDetails(addonId: AddonId) {
        find<ModFileDetailsFragment>(
            ModFileDetailsFragment::addonId to addonId
        ).openModal(modality = Modality.NONE)
    }

    fun changeModVersion(addonId: AddonId) {
        var currentAddonId = addonId
        find<ModVersionListFragment>(
            ModVersionListFragment::dialogType to ModVersionListFragment.Type.SELECT,
            ModVersionListFragment::projectId to addonId.projectId,
            ModVersionListFragment::selectedFileIds to selectedAddonIds,
            ModVersionListFragment::selectCallback to { newAddonId: AddonId ->
                updateElements.replaceAll {
                    if (it.update.newVersion.projectId == currentAddonId.projectId && it.update.newVersion.fileId == currentAddonId.fileId) {
                        ModpackUpdateListElement(it.enabled, AddonUpdate(it.update.oldVersion, newAddonId))
                    } else {
                        it
                    }
                }
                currentAddonId = newAddonId
            }
        ).openModal()
    }

    fun applyUpdates() {
        val updates = ArrayList<AddonUpdate>()
        updateElements.forEach {
            if (it.enabled.value) {
                updates += it.update
            }
        }
        updateElements.clear()

        modListState.updateAddons(updates)
    }
}