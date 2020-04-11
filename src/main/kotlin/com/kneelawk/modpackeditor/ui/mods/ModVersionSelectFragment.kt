package com.kneelawk.modpackeditor.ui.mods

import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.AddonFile
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.ui.ModpackEditorMainController
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import com.kneelawk.modpackeditor.ui.util.ModListState
import javafx.beans.property.SimpleLongProperty
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
class ModVersionSelectFragment : Fragment() {
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
        listview<ModVersionSelectListElement> {
            when (dialogType) {
                Type.INSTALL -> cellFragment(ModVersionInstallListFragment::class)
                Type.SELECT -> cellFragment(ModVersionSelectListFragment::class)
            }
            asyncItems { loadModList() }

            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
            maxWidth = Double.MAX_VALUE
            maxHeight = Double.MAX_VALUE
        }
    }

    private fun loadModList(): List<ModVersionSelectListElement> {
        return curseApi.getAddonFiles(projectId).orEmpty().sortedByDescending { it.fileDate }
                .map { ModVersionSelectListElement(AddonFile(projectId, it), this) }
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
//        if (dialogType == Type.INSTALL) {
//            close()
//        }
    }

    enum class Type {
        /**
         * This dialog type is where selected state depends on the mods installed in the modpack.
         *
         * Note: When the dialog is in INSTALL mode, clicking the install button closes the window.
         */
        INSTALL,

        /**
         * This dialog type is where selected state depends on the selectedFileId property.
         */
        SELECT
    }
}

/**
 * Wrapper class to pass all the necessary information to the ListCellFragments.
 */
data class ModVersionSelectListElement(val addonFile: AddonFile, val fragment: ModVersionSelectFragment)

/**
 * ListCellFragment for the INSTALL mode of the mod version select dialog.
 */
class ModVersionInstallListFragment : ListCellFragment<ModVersionSelectListElement>() {
    private val modListState: ModListState by inject()

    private val addonIdProperty = itemProperty.objectBinding { it?.addonFile }
    private val installedProperty = modListState.modFileInstalledProperty(addonIdProperty)

    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        alignment = Pos.CENTER

        label(itemProperty.stringBinding { it?.addonFile?.fileData?.displayName })
        label("-")
        label(itemProperty.stringBinding { it?.addonFile?.fileData?.fileName })
        label("for")
        label(itemProperty.stringBinding { it?.addonFile?.fileData?.gameVersion?.firstOrNull() ?: "?" })
        button("+") {
            enableWhen(itemProperty.booleanBinding { file ->
                file?.addonFile?.fileData?.gameVersion?.size?.let { it > 1 } ?: false
            })
            action {
                val loc = localToScreen(boundsInLocal)
                val tooltip = Tooltip(item.addonFile.fileData.gameVersion.joinToString())
                tooltip.isAutoHide = true
                tooltip.hideDelay = Duration.seconds(5.0)
                tooltip.show(this, loc.minX, loc.maxY)
            }
        }

        region {
            hgrow = Priority.ALWAYS
        }

        button("Details") {
            enableWhen(itemProperty.isNotNull)
            action {
                item?.fragment?.showDetails(item.addonFile)
            }
        }
        button(installedProperty.stringBinding { if (it == true) "Installed" else "Install" }) {
            enableWhen(itemProperty.isNotNull.and(installedProperty.not()))
            action {
                item?.fragment?.selectItem(item.addonFile)
            }
        }
    }
}

/**
 * ListCellFragment for the SELECT mode of the mod version select dialog.
 */
class ModVersionSelectListFragment : ListCellFragment<ModVersionSelectListElement>() {
    private val addonIdProperty = itemProperty.objectBinding { it?.addonFile }
    private val selectedItem = itemProperty.select { it?.fragment?.selectedFileId ?: SimpleLongProperty(0) }
    private val selectedProperty = addonIdProperty.booleanBinding(selectedItem) { it?.fileId == selectedItem.value }

    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        alignment = Pos.CENTER

        label(itemProperty.stringBinding { it?.addonFile?.fileData?.displayName })
        label("-")
        label(itemProperty.stringBinding { it?.addonFile?.fileData?.fileName })
        label("for")
        label(itemProperty.stringBinding { it?.addonFile?.fileData?.gameVersion?.firstOrNull() ?: "?" })
        button("+") {
            enableWhen(itemProperty.booleanBinding { file ->
                file?.addonFile?.fileData?.gameVersion?.size?.let { it > 1 } ?: false
            })
            action {
                val loc = localToScreen(boundsInLocal)
                val tooltip = Tooltip(item.addonFile.fileData.gameVersion.joinToString())
                tooltip.isAutoHide = true
                tooltip.hideDelay = Duration.seconds(5.0)
                tooltip.show(this, loc.minX, loc.maxY)
            }
        }

        region {
            hgrow = Priority.ALWAYS
        }

        button("Details") {
            enableWhen(itemProperty.isNotNull)
            action {
                item?.fragment?.showDetails(item.addonFile)
            }
        }
        button(selectedProperty.stringBinding { if (it == true) "Selected" else "Select" }) {
            enableWhen(itemProperty.isNotNull.and(selectedProperty.not()))
            action {
                item?.fragment?.selectItem(item.addonFile)
            }
        }
    }
}
