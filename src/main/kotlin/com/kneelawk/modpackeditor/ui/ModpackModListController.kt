package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.manifest.FileJson
import com.kneelawk.modpackeditor.ui.mods.ModDetailsFragment
import com.kneelawk.modpackeditor.ui.mods.ModFileDetailsFragment
import com.kneelawk.modpackeditor.ui.mods.ModVersionListFragment
import com.kneelawk.modpackeditor.ui.util.*
import javafx.stage.Modality
import tornadofx.Controller
import kotlin.reflect.KProperty1

/**
 * Controller for the modpack mod list view.
 */
class ModpackModListController : Controller() {
    private val elementUtils: ElementUtils by inject()
    private val modListState: ModListState by inject()
    val model: ModpackModel by inject()

    fun removeMod(addonId: FileJson) {
        modListState.startEditing(addonId)
        find<ModpackEditorMainView>().openInternalWindow(
            find<AreYouSureDialog>(mapOf<KProperty1<AreYouSureDialog, Any>, Any>(
                AreYouSureDialog::prompt to "Are you sure you would like to remove ${elementUtils.loadModName(
                    addonId)}?",
                AreYouSureDialog::callback to { res: AreYouSureDialog.Result ->
                    if (res == AreYouSureDialog.Result.Confirm) {
                        modListState.removeAddon(addonId)
                    }
                },
                AreYouSureDialog::closeCallback to {
                    modListState.finishEditing(addonId)
                }
            ))
        )
    }

    fun changeModVersion(addonId: FileJson) {
        modListState.startEditing(addonId)
        var currentAddon: AddonId = addonId
        find<ModVersionListFragment>(mapOf<KProperty1<ModVersionListFragment, Any>, Any>(
            ModVersionListFragment::dialogType to ModVersionListFragment.Type.INSTALL,
            ModVersionListFragment::projectId to addonId.projectId,
            ModVersionListFragment::selectCallback to { newAddon: AddonId ->
                modListState.replaceAddon(currentAddon, newAddon.toFileJson(addonId.required))
                currentAddon = newAddon
            },
            ModVersionListFragment::closeCallback to {
                modListState.finishEditing(currentAddon)
            }
        )).openModal(modality = Modality.NONE)
    }

    fun changeModRequired(addonId: FileJson, required: Boolean) {
        modListState.startEditing(addonId)
        modListState.replaceAddon(addonId, addonId.toFileJson(required))
        modListState.finishEditing(addonId)
    }

    fun showModDetails(addonId: FileJson) {
        modListState.startEditing(addonId)
        var currentAddon: AddonId = addonId
        find<ModDetailsFragment>(mapOf<KProperty1<ModDetailsFragment, Any>, Any>(
            ModDetailsFragment::projectId to addonId.projectId,
            ModDetailsFragment::changeVersionCallback to { newAddon: AddonId ->
                modListState.replaceAddon(currentAddon, newAddon.toFileJson(addonId.required))
                currentAddon = newAddon
            },
            ModDetailsFragment::closeCallback to {
                modListState.finishEditing(currentAddon)
            }
        )).openModal(modality = Modality.NONE)
    }

    fun showModFileDetails(addonId: FileJson) {
        modListState.startEditing(addonId)
        find<ModFileDetailsFragment>(mapOf<KProperty1<ModFileDetailsFragment, Any>, Any>(
            ModFileDetailsFragment::addonId to addonId,
            ModFileDetailsFragment::closeCallback to {
                modListState.finishEditing(addonId)
            }
        )).openModal(modality = Modality.NONE)
    }
}
