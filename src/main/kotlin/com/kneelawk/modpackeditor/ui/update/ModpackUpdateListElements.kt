package com.kneelawk.modpackeditor.ui.update

import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.ui.util.AddonUpdate
import com.kneelawk.modpackeditor.ui.util.AsynchronousLoader
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import javafx.beans.property.BooleanProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.util.Duration
import tornadofx.*

typealias LabelLoader = AsynchronousLoader<AddonUpdate?, Label, String>
typealias EnableLoader = AsynchronousLoader<AddonUpdate?, Node, Boolean>

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

    private var oldDisplayLoader: LabelLoader? = null
    private var oldNameLoader: LabelLoader? = null
    private var newDisplayLoader: LabelLoader? = null
    private var newNameLoader: LabelLoader? = null

    override val root = gridpane {
        padding = insets(5.0)
        hgap = 10.0
        vgap = 10.0
        alignment = Pos.CENTER_LEFT

        row {
            label("Old:")
            label {
                oldDisplayLoader = LabelLoader(this, itemProperty, { text = it?.oldVersion?.fileId?.toString() ?: "" },
                    { elementUtils.loadModFileDisplay(it?.oldVersion) }, { text = it })
            }
            label("-")
            label {
                oldNameLoader = LabelLoader(this, itemProperty, { text = "loading..." },
                    { elementUtils.loadModFileName(it?.oldVersion) }, { text = it })
            }
        }
        row {
            label("New:")
            label {
                newDisplayLoader = LabelLoader(this, itemProperty, { text = it?.newVersion?.fileId?.toString() ?: "" },
                    { elementUtils.loadModFileDisplay(it?.newVersion) }, { text = it })
            }
            label("-")
            label {
                newNameLoader = LabelLoader(this, itemProperty, { text = "loading..." },
                    { elementUtils.loadModFileName(it?.newVersion) }, { text = it })
            }
        }
    }
}

/**
 * Shows game version details about the mod being updated.
 */
class ModpackUpdateListGameVersionFragment : TableCellFragment<ModpackUpdateListElement, AddonUpdate>() {
    private val elementUtils: ElementUtils by inject()

    private var oldGameVersionLoader: LabelLoader? = null
    private var oldGameVersionEnableLoader: EnableLoader? = null
    private var newGameVersionLoader: LabelLoader? = null
    private var newGameVersionEnableLoader: EnableLoader? = null

    override val root = gridpane {
        padding = insets(5.0)
        hgap = 5.0
        vgap = 5.0
        alignment = Pos.CENTER_LEFT

        row {
            label("Old:")
            label {
                oldGameVersionLoader = LabelLoader(this, itemProperty, { text = "loading..." },
                    { elementUtils.loadModFileGameVersions(it?.oldVersion)[0] }, { text = it })
            }
            button("+") {
                oldGameVersionEnableLoader = EnableLoader(this, itemProperty, { isDisable = true },
                    { elementUtils.loadModFileGameVersions(it?.oldVersion).size > 1 }, { isDisable = !it })
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
            label {
                newGameVersionLoader = LabelLoader(this, itemProperty, { text = "loading..." },
                    { elementUtils.loadModFileGameVersions(it?.newVersion)[0] }, { text = it })
            }
            button("+") {
                newGameVersionEnableLoader = EnableLoader(this, itemProperty, { isDisable = true },
                    { elementUtils.loadModFileGameVersions(it?.newVersion).size > 1 }, { isDisable = !it })
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
