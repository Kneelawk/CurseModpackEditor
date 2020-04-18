package com.kneelawk.modpackeditor.ui.update

import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.ui.util.AddonUpdate
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import com.kneelawk.modpackeditor.ui.util.asyncExpression
import javafx.beans.property.BooleanProperty
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.util.Duration
import tornadofx.*

/**
 * Represents an element in a modpack update mod list.
 */
data class ModpackUpdateListElement(val enabled: BooleanProperty, val update: AddonUpdate)

/**
 * Allows the user to enable or disable a mod update.
 */
class ModpackUpdateListEnableFragment : TableCellFragment<ModpackUpdateListElement, Boolean>() {
    override val root = vbox {
        padding = insets(5.0)
        alignment = Pos.CENTER

        checkbox(property = itemProperty)
    }
}

/**
 * Shows version details about the mod being updated.
 */
class ModpackUpdateListFileFragment : TableCellFragment<ModpackUpdateListElement, AddonUpdate>() {
    private val elementUtils: ElementUtils by inject()

    override val root = gridpane {
        padding = insets(5.0)
        hgap = 10.0
        vgap = 10.0
        alignment = Pos.CENTER_LEFT

        row {
            label("Old:")
            label(itemProperty.asyncExpression({ it?.oldVersion?.fileId?.toString() ?: "" },
                { elementUtils.loadModFileDisplay(it?.oldVersion) }))
            label("-")
            label(itemProperty.asyncExpression({ "loading..." }, { elementUtils.loadModFileName(it?.oldVersion) }))
        }
        row {
            label("New:")
            label(itemProperty.asyncExpression({ it?.newVersion?.fileId?.toString() ?: "" },
                { elementUtils.loadModFileDisplay(it?.newVersion) }))
            label("-")
            label(itemProperty.asyncExpression({ "loading..." }, { elementUtils.loadModFileName(it?.newVersion) }))
        }
    }
}

/**
 * Shows game version details about the mod being updated.
 */
class ModpackUpdateListGameVersionFragment : TableCellFragment<ModpackUpdateListElement, AddonUpdate>() {
    private val elementUtils: ElementUtils by inject()

    override val root = gridpane {
        padding = insets(5.0)
        hgap = 5.0
        vgap = 5.0
        alignment = Pos.CENTER_LEFT

        row {
            label("Old:")
            label(itemProperty.asyncExpression({ "loading..." },
                { elementUtils.loadModFileGameVersions(it?.oldVersion)[0] }))
            button("+") {
                enableWhen(itemProperty.asyncExpression({ false },
                    { elementUtils.loadModFileGameVersions(it?.oldVersion).size > 1 }))
                action {
                    runAsync {
                        elementUtils.loadModFileGameVersions(item.oldVersion)
                    } success {
                        val loc = localToScreen(boundsInLocal)
                        val tooltip = Tooltip(it.joinToString(",\n"))
                        tooltip.isAutoHide = true
                        tooltip.hideDelay = Duration.seconds(5.0)
                        tooltip.show(this, loc.minX, loc.maxY)
                    }
                }
            }
        }

        row {
            label("New:")
            label(itemProperty.asyncExpression({ "loading..." },
                { elementUtils.loadModFileGameVersions(it?.newVersion)[0] }))
            button("+") {
                enableWhen(itemProperty.asyncExpression({ false },
                    { elementUtils.loadModFileGameVersions(it?.newVersion).size > 1 }))
                action {
                    runAsync {
                        elementUtils.loadModFileGameVersions(item.newVersion)
                    } success {
                        val loc = localToScreen(boundsInLocal)
                        val tooltip = Tooltip(it.joinToString(",\n"))
                        tooltip.isAutoHide = true
                        tooltip.hideDelay = Duration.seconds(5.0)
                        tooltip.show(this, loc.minX, loc.maxY)
                    }
                }
            }
        }
    }
}

/**
 * Allows the user to view file details and change the new file's version.
 */
class ModpackUpdateListConfigureFragment : TableCellFragment<ModpackUpdateListElement, AddonUpdate>() {
    val oldDetailsCallback: (AddonId) -> Unit by param()
    val newDetailsCallback: (AddonId) -> Unit by param()
    val changeVersionCallback: (AddonId) -> Unit by param()

    override val root = gridpane {
        padding = insets(5.0)
        hgap = 5.0
        vgap = 5.0
        alignment = Pos.CENTER

        row {
            button("Old Details") {
                enableWhen(itemProperty.isNotNull)
                action {
                    oldDetailsCallback(item.oldVersion)
                }
            }
        }

        row {
            button("New Details") {
                enableWhen(itemProperty.isNotNull)
                action {
                    newDetailsCallback(item.newVersion)
                }
            }
            button("Change Version") {
                enableWhen(itemProperty.isNotNull)
                action {
                    changeVersionCallback(item.newVersion)
                }
            }
        }
    }
}
