package com.kneelawk.modpackeditor.ui.update

import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.ui.ModpackEditorMainController
import com.kneelawk.modpackeditor.ui.util.MinecraftVersionFilterFragment
import com.kneelawk.modpackeditor.ui.util.paramCellFragment
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Fragment for configuring a modpack update.
 */
class ModpackUpdateView : View() {
    private val c: ModpackUpdateController by inject()
    private val mainController: ModpackEditorMainController by inject()

    private val descriptionTitle =
            mainController.modpackTitle.stringBinding { "$it - Update Configuration" }

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0
        alignment = Pos.TOP_LEFT
        label("Modpack Updater").style {
            fontSize = 18.px
            fontWeight = FontWeight.BOLD
        }
        hbox {
            spacing = 10.0
            enableWhen(c.running.not())
            label("Minecraft version filter:")
            add<MinecraftVersionFilterFragment>()
        }
        button("Collect Mod Updates") {
            enableWhen(c.running.not())
            maxWidth = Double.MAX_VALUE
            action {
                c.collectModUpdates()
            }
        }
        label(c.collectStatus)
        progressbar(c.collectProgress) {
            maxWidth = Double.MAX_VALUE
        }
        tableview(c.updateElements) {
            column("Apply", ModpackUpdateListElement::enabled) {
                prefWidth(50.0)
                minWidth(50.0)
                cellFragment(ModpackUpdateListEnableFragment::class)
                makeEditable()
            }
            readonlyColumn("Update", ModpackUpdateListElement::update) {
                prefWidth(800.0)
                minWidth(500.0)
                cellFragment(ModpackUpdateListFileFragment::class)
            }
            readonlyColumn("Game Version", ModpackUpdateListElement::update) {
                prefWidth(200.0)
                minWidth(180.0)
                cellFragment(ModpackUpdateListGameVersionFragment::class)
            }
            readonlyColumn("Configure", ModpackUpdateListElement::update) {
                prefWidth(250.0)
                minWidth(250.0)
                paramCellFragment(scope, ModpackUpdateListConfigureFragment::class,
                    ModpackUpdateListConfigureFragment::oldDetailsCallback to { addonId: AddonId ->
                        c.showModDetails(addonId)
                    },
                    ModpackUpdateListConfigureFragment::newDetailsCallback to { addonId: AddonId ->
                        c.showModDetails(addonId)
                    },
                    ModpackUpdateListConfigureFragment::changeVersionCallback to { addonId: AddonId ->
                        c.changeModVersion(addonId)
                    }
                )
            }

            columnResizePolicy = SmartResize.POLICY

            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
            maxWidth = Double.MAX_VALUE
            maxHeight = Double.MAX_VALUE
        }
        button("Apply Updates") {
            enableWhen(c.updateElements.booleanBinding { it?.let { it.size > 0 } ?: false })
            maxWidth = Double.MAX_VALUE
            action {
                close()
                c.applyUpdates()
            }
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
}