package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.data.manifest.FileJson
import tornadofx.Controller
import kotlin.reflect.KProperty1

/**
 * Controller for the modpack mod list view.
 */
class ModpackModListController : Controller() {
    private val elementUtils: ElementUtils by inject()
    val model: ModpackModel by inject()

    init {
        subscribe<ModRemoveEvent> {
            removeMod(it.addonId)
        }
        subscribe<ModRequiredEvent> {
            changeModRequired(it.addonId, it.required)
        }
    }

    private fun removeMod(addonId: FileJson) {
        find<AreYouSureDialog>(mapOf<KProperty1<AreYouSureDialog, Any>, Any>(
            AreYouSureDialog::prompt to "Are you sure you would like to remove ${elementUtils.loadModName(addonId)}?",
            AreYouSureDialog::callback to { res: AreYouSureDialog.Result ->
                if (res == AreYouSureDialog.Result.Confirm) {
                    model.modpackMods.remove(addonId)
                }
            })).openModal()
    }

    private fun changeModRequired(addonId: FileJson, required: Boolean) {
        model.modpackMods.replaceAll {
            if (it == addonId) {
                FileJson(addonId.projectId, addonId.fileId, required)
            } else {
                it
            }
        }
    }
}
