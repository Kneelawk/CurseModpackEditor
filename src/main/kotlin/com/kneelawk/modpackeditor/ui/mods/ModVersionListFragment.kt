package com.kneelawk.modpackeditor.ui.mods

import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.AddonFile
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.SimpleAddonId
import com.kneelawk.modpackeditor.data.version.MinecraftVersion
import com.kneelawk.modpackeditor.ui.ModpackEditorMainController
import com.kneelawk.modpackeditor.ui.util.*
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
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
    val selectedFileIds: ObservableList<AddonId> by param(FXCollections.emptyObservableList())
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
        readonlyColumn("Select", ModVersionListElement::addonFile) {
            minWidth(200.0)
            prefWidth(200.0)
            paramCellFragment(scope, ModVersionListSelectFragment::class,
                ModVersionListSelectFragment::frag to this@ModVersionListFragment)
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
            add<MinecraftVersionFilterFragment>(
                MinecraftVersionFilterFragment::enableProperty to modListState.filterMinecraftVersion)
            button("Reload") {
                action {
                    listView.asyncItems { loadModList() }
                }
            }
        }
        add(listView)
    }

    private fun modFileSelectedProperty(addonId: AddonId): BooleanBinding {
        return selectedFileIds.containsWhereProperty { it.projectId == addonId.projectId && it.fileId == addonId.fileId }
    }

    fun modFileSelectedProperty(addonId: ObservableValue<out AddonId>): BooleanBinding {
        return selectedFileIds.containsWhereProperty(
            addonId) { a, b -> a.projectId == b?.projectId && a.fileId == b.fileId }
    }

    private fun loadModList(): List<ModVersionListElement> {
        val files = curseApi.getAddonFiles(projectId).orEmpty()
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
        }.sortedByDescending { it.fileDate }.map { file ->
            ModVersionListElement(AddonFile(projectId, file))
        }
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
                Type.INSTALL -> modListState.modFileInstalledProperty(addonId)
                Type.SELECT -> modFileSelectedProperty(addonId)
            },
            ModFileDetailsFragment::selectCallback to {
                selectCallback(addonId)
            }
        )).openModal(modality = Modality.NONE, owner = currentWindow)
    }

    fun selectItem(addonId: AddonId) {
        selectCallback(SimpleAddonId(addonId))
    }

    enum class Type {
        /**
         * This dialog type is where selected state depends on the mods installed in the modpack.
         */
        INSTALL,

        /**
         * This dialog type is where selected state depends on the selectedFileIds property.
         */
        SELECT
    }
}

/**
 * Wrapper class to pass all the necessary information to the TableCellFragments.
 */
data class ModVersionListElement(val addonFile: AddonFile)

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
class ModVersionListSelectFragment : TableCellFragment<ModVersionListElement, AddonFile>() {
    val frag: ModVersionListFragment by param()

    private val modListState: ModListState by inject()

    private val selected = frag.modFileSelectedProperty(itemProperty)
    private val installed = modListState.modFileInstalledProperty(itemProperty)

    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        alignment = Pos.CENTER

        button("Details") {
            enableWhen(itemProperty.isNotNull)
            action {
                item?.let { frag.showDetails(it) }
            }
        }
        button(installed.stringBinding(selected) {
            when (frag.dialogType) {
                ModVersionListFragment.Type.INSTALL -> if (it == false) "Install" else "Installed"
                ModVersionListFragment.Type.SELECT -> {
                    when {
                        it == true -> "Installed"
                        selected.value == true -> "Selected"
                        else -> "Select"
                    }
                }
            }
        }) {
            enableWhen(itemProperty.isNotNull.and(selected.not()).and(installed.not()))
            action {
                item?.let { frag.selectItem(it) }
            }
        }
    }
}
