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
}
