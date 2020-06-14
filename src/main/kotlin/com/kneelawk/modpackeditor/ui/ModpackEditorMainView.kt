package com.kneelawk.modpackeditor.ui

import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * The main view for the modpack editor.
 */
class ModpackEditorMainView : View("Modpack Editor") {
    private val c: ModpackEditorMainController by inject()

    override val root = vbox {
        padding = insets(0.0)
        menubar {
            menu("File") {
                item("New...", "Shortcut+N") {
                    action {
                        c.newModpack()
                    }
                }
                item("Save", "Shortcut+S") {
                    action {
                        c.saveModpack()
                    }
                }
                item("Save As...", "Shortcut+Shift+S") {
                    action {
                        c.saveModpackAs()
                    }
                }
                item("Open...", "Shortcut+O") {
                    action {
                        c.openModpack()
                    }
                }
                item("Duplicate...") {
                    action {
                        c.duplicateModpack()
                    }
                }
                item("Close Project", "Shortcut+W") {
                    action {
                        close()
                    }
                }
            }
            menu("Tools") {
                item("Scan Mod Dependencies...") {
                    action {
                        c.scanModDependencies()
                    }
                }
                item("Sort Mods By Name...") {
                    action {
                        c.sortMods()
                    }
                }
                item("Update Modpack...") {
                    action {
                        c.runModpackUpdater()
                    }
                }
            }
        }

        vbox {
            padding = insets(25.0)
            spacing = 10.0
            maxHeight = Double.MAX_VALUE
            vgrow = Priority.ALWAYS

            label(c.model.modpackName).style {
                fontSize = 36.px
                fontWeight = FontWeight.BOLD
            }

            tabpane {
                vgrow = Priority.ALWAYS
                enableWhen(c.running.not())
                tab<ModpackDetailsView> {
                    closableProperty().unbind()
                    isClosable = false
                    textProperty().unbind()
                    text = "Details"
                }
                tab<ModpackModListView> {
                    closableProperty().unbind()
                    isClosable = false
                    textProperty().unbind()
                    text = "Mod List"
                }
            }
        }
    }

    init {
        titleProperty.bind(c.modpackTitle)
    }

    override fun onBeforeShow() {
        with(currentStage!!) {
            width = 1280.0
            height = 800.0
            minWidth = 500.0
            minHeight = 400.0
        }
    }
}