package com.kneelawk.modpackeditor.ui.mods

import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.curseapi.AddonData
import com.kneelawk.modpackeditor.data.version.MinecraftVersion
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import com.kneelawk.modpackeditor.ui.util.ModListState
import com.kneelawk.modpackeditor.ui.util.asyncExpression
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * ListCellFragment for displaying mods in the mod search menu.
 */
class AddModsElementFragment : ListCellFragment<AddonData>() {
    private val elementUtils: ElementUtils by inject()
    private val curseApi: CurseApi by inject()
    private val modListState: ModListState by inject()

    val detailsCallback: (AddonData) -> Unit by param()
    val filesCallback: (AddonData) -> Unit by param()
    val installLatestCallback: (AddonData) -> Unit by param()

    private val projectId = itemProperty.objectBinding { it?.id }
    private val installed = modListState.modInstalledProperty(projectId)
    private val compatibilityCheck = itemProperty.objectBinding(modListState.lowMinecraftVersion,
        modListState.highMinecraftVersion) {
        it?.let {
            AddonCompatibilityCheck(it, modListState.lowMinecraftVersion.value, modListState.highMinecraftVersion.value)
        }
    }
    private val isCompatible = compatibilityCheck.asyncExpression({ AddonCompatibilityState.LOADING }) { maybeCheck ->
        val found = maybeCheck?.let { check ->
            val files = curseApi.getAddonFiles(check.addonData.id).orEmpty()
            files.find { file ->
                file.gameVersion.find { version ->
                    MinecraftVersion.tryParse(version)?.let {
                        it >= check.lowMinecraftVersion && it <= check.highMinecraftVersion
                    } ?: false
                } != null
            } != null
        }
        if (found == true) {
            AddonCompatibilityState.COMPATIBLE
        } else {
            AddonCompatibilityState.INCOMPATIBLE
        }
    }

    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        alignment = Pos.CENTER

        imageview(itemProperty.asyncExpression({ null }, { elementUtils.loadImage(it) }))

        vbox {
            spacing = 10.0
            alignment = Pos.TOP_LEFT

            label(itemProperty.stringBinding { it?.name ?: "Loading..." }) {
                style {
                    fontWeight = FontWeight.BOLD
                    fontSize = 16.px
                }
            }
            label(itemProperty.stringBinding { it?.summary ?: "Loading..." })
        }

        region {
            hgrow = Priority.ALWAYS
        }

        gridpane {
            alignment = Pos.CENTER
            hgap = 5.0
            vgap = 5.0
            enableWhen(modListState.notEditingProperty(itemProperty.objectBinding { it?.id }))
            row {
                button(installed.stringBinding(isCompatible) {
                    when {
                        isCompatible.value == AddonCompatibilityState.LOADING -> "Loading..."
                        it == true -> "Installed"
                        isCompatible.value == AddonCompatibilityState.INCOMPATIBLE -> "Incompatible"
                        else -> "Install Latest"
                    }
                }) {
                    enableWhen(
                        installed.not().and(isCompatible.booleanBinding { it == AddonCompatibilityState.COMPATIBLE }))
                    action {
                        installLatestCallback(item)
                    }
                    gridpaneConstraints {
                        columnSpan = 2
                    }
                    maxWidth = Double.MAX_VALUE
                }
            }
            row {
                button("Details") {
                    action {
                        detailsCallback(item)
                    }
                }
                button("Files") {
                    action {
                        filesCallback(item)
                    }
                }
            }
        }
    }
}

data class AddonCompatibilityCheck(val addonData: AddonData, val lowMinecraftVersion: MinecraftVersion,
                                   val highMinecraftVersion: MinecraftVersion)

enum class AddonCompatibilityState {
    LOADING,
    INCOMPATIBLE,
    COMPATIBLE
}
