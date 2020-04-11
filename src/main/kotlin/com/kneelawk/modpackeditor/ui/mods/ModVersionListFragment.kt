package com.kneelawk.modpackeditor.ui.mods

import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.AddonFile
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.SimpleAddonId
import com.kneelawk.modpackeditor.data.version.MinecraftVersion
import com.kneelawk.modpackeditor.ui.ModpackEditorMainController
import com.kneelawk.modpackeditor.ui.SelectMinecraftVersionFragment
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import com.kneelawk.modpackeditor.ui.util.ModListState
import javafx.beans.binding.Binding
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableLongValue
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import javafx.stage.Modality
import javafx.util.Duration
import tornadofx.*

/**
 * Created by Kneelawk on 4/10/20.
 */
class ModVersionListFragment : Fragment() {
    val dialogType: Type by param()
    val projectId: Long by param()
    val selectedFileId: ObservableLongValue by param()
    val selectCallback: (AddonId) -> Unit by param { _ -> }
    val closeCallback: () -> Unit by param {}

    private val curseApi: CurseApi by inject()
    private val modListState: ModListState by inject()
    private val elementUtils: ElementUtils by inject()
    private val mainController: ModpackEditorMainController by inject()

    private val modName = SimpleStringProperty("")
    private val descriptionTitle =
            mainController.modpackTitle.stringBinding(modName) { "$it - ${modName.value} - Files" }

    private val listView = tableview<ModVersionListElement> {
        readonlyColumn("File", ModVersionListElement::addonFile) {
            minWidth(600.0)
            prefWidth(600.0)
            cellFragment(ModVersionListFileFragment::class)
        }
        readonlyColumn("Game Version", ModVersionListElement::addonFile) {
            minWidth(200.0)
            prefWidth(200.0)
            cellFragment(ModVersionListGameVersionFragment::class)
        }
        column("Select", ModVersionListElement::info) {
            minWidth(200.0)
            prefWidth(200.0)
            cellFragment(ModVersionListSelectFragment::class)
        }

        columnResizePolicy = SmartResize.POLICY

        asyncItems { loadModList() }

        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
        maxWidth = Double.MAX_VALUE
        maxHeight = Double.MAX_VALUE
    }

    override val root = vbox {
        padding = insets(10.0)
        spacing = 10.0
        hbox {
            alignment = Pos.BOTTOM_LEFT
            spacing = 10.0
            imageview {
                runAsync {
                    val loaded = elementUtils.loadSmallImage(projectId)
                    runLater { image = loaded }
                }
            }
            label {
                style {
                    fontWeight = FontWeight.BOLD
                    fontSize = 16.px
                }
                runAsync {
                    val name = elementUtils.loadModName(projectId)
                    runLater {
                        modName.value = name
                        text = name
                    }
                }
            }
            label("Files")
        }
        hbox {
            spacing = 10.0
            alignment = Pos.CENTER_LEFT
            checkbox("Filter minecraft version from", modListState.filterMinecraftVersion)
            button(modListState.lowMinecraftVersion.stringBinding { it.toString() }) {
                enableWhen(modListState.filterMinecraftVersion)
                action {
                    selectLowMinecraftVersion()
                }
            }
            label("to") {
                enableWhen(modListState.filterMinecraftVersion)
            }
            button(modListState.highMinecraftVersion.stringBinding { it.toString() }) {
                enableWhen(modListState.filterMinecraftVersion)
                action {
                    selectHighMinecraftVersion()
                }
            }
            button("Reload") {
                action {
                    listView.asyncItems { loadModList() }
                }
            }
        }
        add(listView)
    }

    private fun loadModList(): List<ModVersionListElement> {
        val files = curseApi.getAddonFiles(projectId).orEmpty().sortedByDescending { it.fileDate }
        return if (modListState.filterMinecraftVersion.value) {
            files.filter { file ->
                file.gameVersion.find { version ->
                    MinecraftVersion.tryParse(version)?.let {
                        it >= modListState.lowMinecraftVersion.value && it <= modListState.highMinecraftVersion.value
                    } ?: false
                } != null
            }
        } else {
            files
        }.map { file ->
            val addonId = SimpleAddonId(projectId, file.id)
            ModVersionListElement(AddonFile(projectId, file), when (dialogType) {
                Type.INSTALL -> modListState.modFileInstalledProperty(addonId)
                        .objectBinding { ModVersionListSelectInfo(this, addonId, dialogType, it!!) }
                Type.SELECT -> selectedFileId.booleanBinding { it == file.id }
                        .objectBinding { ModVersionListSelectInfo(this, addonId, dialogType, it!!) }
            })
        }
    }

    private fun selectLowMinecraftVersion() {
        find<SelectMinecraftVersionFragment>(mapOf(
            SelectMinecraftVersionFragment::callback to { result: SelectMinecraftVersionFragment.Result ->
                when (result) {
                    is SelectMinecraftVersionFragment.Result.Cancel -> {
                    }
                    is SelectMinecraftVersionFragment.Result.Select -> {
                        val version = result.minecraft
                        modListState.lowMinecraftVersion.value = version
                        if (modListState.highMinecraftVersion.value < version) {
                            modListState.highMinecraftVersion.value = version
                        }
                    }
                }
            })).openModal()
    }

    private fun selectHighMinecraftVersion() {
        find<SelectMinecraftVersionFragment>(mapOf(
            SelectMinecraftVersionFragment::callback to { result: SelectMinecraftVersionFragment.Result ->
                when (result) {
                    is SelectMinecraftVersionFragment.Result.Cancel -> {
                    }
                    is SelectMinecraftVersionFragment.Result.Select -> {
                        val version = result.minecraft
                        modListState.highMinecraftVersion.value = version
                        if (modListState.lowMinecraftVersion.value > version) {
                            modListState.lowMinecraftVersion.value = version
                        }
                    }
                }
            })).openModal()
    }

    override fun onBeforeShow() {
        with(currentStage!!) {
            width = 1280.0
            height = 720.0
            minWidth = 500.0
            minHeight = 400.0
        }
    }

    override fun onDock() {
        titleProperty.bind(descriptionTitle)
    }

    override fun onUndock() {
        closeCallback()
    }

    fun showDetails(addonId: AddonId) {
        find<ModFileDetailsFragment>(mapOf(
            ModFileDetailsFragment::dialogType to when (dialogType) {
                Type.INSTALL -> ModFileDetailsFragment.Type.INSTALL
                Type.SELECT -> ModFileDetailsFragment.Type.SELECT
            },
            ModFileDetailsFragment::addonId to addonId,
            ModFileDetailsFragment::selectedProperty to when (dialogType) {
                Type.INSTALL -> modListState.modFileInstalledProperty(SimpleObjectProperty(addonId))
                Type.SELECT -> selectedFileId.booleanBinding { it == addonId.fileId }
            },
            ModFileDetailsFragment::selectCallback to {
                selectCallback(addonId)
            }
        )).openModal(modality = Modality.NONE, owner = currentWindow)
    }

    fun selectItem(addonId: AddonId) {
        selectCallback(addonId)
    }

    enum class Type {
        /**
         * This dialog type is where selected state depends on the mods installed in the modpack.
         */
        INSTALL,

        /**
         * This dialog type is where selected state depends on the selectedFileId property.
         */
        SELECT
    }
}

/**
 * Wrapper class to pass all the necessary information to the TableCellFragments.
 */
data class ModVersionListElement(val addonFile: AddonFile, val info: Binding<ModVersionListSelectInfo?>)

/**
 * Wrapper providing information specifically for the selection table cell fragment.
 */
data class ModVersionListSelectInfo(val fragment: ModVersionListFragment, val addonId: AddonId,
                                    val dialogType: ModVersionListFragment.Type, val selected: Boolean)

/**
 * Table cell fragment for displaying file details like display name and file name.
 */
class ModVersionListFileFragment : TableCellFragment<ModVersionListElement, AddonFile>() {
    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        alignment = Pos.CENTER_LEFT

        label(itemProperty.stringBinding { it?.fileData?.displayName })
        label("-")
        label(itemProperty.stringBinding { it?.fileData?.fileName })
    }
}

/**
 * Table cell fragment for displaying a file's game versions.
 */
class ModVersionListGameVersionFragment : TableCellFragment<ModVersionListElement, AddonFile>() {
    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        alignment = Pos.CENTER

        label(itemProperty.stringBinding { it?.fileData?.gameVersion?.firstOrNull() ?: "?" })
        button("+") {
            enableWhen(itemProperty.booleanBinding { file ->
                file?.fileData?.gameVersion?.size?.let { it > 1 } ?: false
            })
            action {
                val loc = localToScreen(boundsInLocal)
                val tooltip = Tooltip(item.fileData.gameVersion.joinToString(",\n"))
                tooltip.isAutoHide = true
                tooltip.hideDelay = Duration.seconds(5.0)
                tooltip.show(this, loc.minX, loc.maxY)
            }
        }
    }
}

/**
 * Table cell fragment for displaying the file selection and details buttons.
 */
class ModVersionListSelectFragment : TableCellFragment<ModVersionListElement, ModVersionListSelectInfo?>() {
    private val notSelected = itemProperty.booleanBinding { it?.selected ?: true }.not()

    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        alignment = Pos.CENTER

        button("Details") {
            enableWhen(itemProperty.isNotNull)
            action {
                item?.fragment?.showDetails(item!!.addonId)
            }
        }
        button(itemProperty.stringBinding(notSelected) {
            when (it?.dialogType) {
                ModVersionListFragment.Type.INSTALL -> if (notSelected.value == true) "Install" else "Installed"
                ModVersionListFragment.Type.SELECT -> if (notSelected.value == true) "Select" else "Selected"
                null -> ""
            }
        }) {
            enableWhen(itemProperty.isNotNull.and(notSelected))
            action {
                item?.fragment?.selectItem(item!!.addonId)
            }
        }
    }
}
