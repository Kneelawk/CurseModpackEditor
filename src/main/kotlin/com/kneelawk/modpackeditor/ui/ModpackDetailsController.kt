package com.kneelawk.modpackeditor.ui

import javafx.stage.FileChooser
import tornadofx.Controller
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import java.io.File
import kotlin.reflect.KProperty1

/**
 * Controller for the ModpackDetailsView.
 */
class ModpackDetailsController : Controller() {
    val model: ModpackModel by inject()

    private var previousDir = File(System.getProperty("user.home"))

    fun selectModpackLocation() {
        chooseFile("Modpack Location", arrayOf(FileChooser.ExtensionFilter("Curse Modpack Files", "*.zip")),
            previousDir, FileChooserMode.Save).firstOrNull()?.let {
            var path = it.absolutePath
            if (!path.endsWith(".zip")) {
                path += ".zip"
            }
            model.modpackLocation.value = path
            previousDir = it.parentFile
        }
    }

    fun selectMinecraftVersion() {
        find<SelectMinecraftVersionFragment>(mapOf(
            SelectMinecraftVersionFragment::callback to { result: SelectMinecraftVersionFragment.Result ->
                when (result) {
                    is SelectMinecraftVersionFragment.Result.Cancel -> {
                    }
                    is SelectMinecraftVersionFragment.Result.Select -> {
                        model.minecraftVersion.value = result.minecraft.versionString
                    }
                }
            })).openModal()
    }

    fun selectModLoader() {
        find<SelectModLoaderFragment>(mapOf<KProperty1<SelectModLoaderFragment, Any?>, Any?>(
            SelectModLoaderFragment::minecraftVersion to model.minecraftVersion.value,
            SelectModLoaderFragment::callback to { result: SelectModLoaderFragment.Result ->
                when (result) {
                    is SelectModLoaderFragment.Result.Cancel -> {
                    }
                    is SelectModLoaderFragment.Result.Select -> {
                        model.modLoaderVersion.value = result.modLoader.name
                    }
                }
            })).openModal()
    }
}