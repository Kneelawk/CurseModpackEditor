package com.kneelawk.modpackeditor.ui.mods

import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.ui.ModpackEditorMainController
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Fragment that show's a mod file's changelog.
 */
class ModFileDetailsFragment : Fragment() {
    val dialogType: Type by param(Type.NONE)
    val addonId: AddonId by param()
    val selectedProperty: BooleanExpression by param()
    val selectCallback: () -> Unit by param {}
    val closeCallback: () -> Unit by param {}

    private val elementUtils: ElementUtils by inject()
    private val mainController: ModpackEditorMainController by inject()

    private val webStylesheet = javaClass.getResource("/com/kneelawk/modpackeditor/web.css").toExternalForm()
    private val fileName = SimpleStringProperty("")
    private val descriptionTitle =
            mainController.modpackTitle.stringBinding(fileName) { "$it - ${fileName.value} - Changelog" }

    override val root = vbox {
        padding = insets(10.0)
        spacing = 10.0
        hbox {
            alignment = Pos.BOTTOM_LEFT
            spacing = 10.0
            imageview {
                runAsync {
                    val loaded = elementUtils.loadImage(addonId)
                    runLater { image = loaded }
                }
            }
            label {
                runAsync {
                    val display = elementUtils.loadModFileDisplay(addonId)
                    runLater {
                        text = display
                    }
                }
            }
            label("-")
            label {
                runAsync {
                    val file = elementUtils.loadModFileName(addonId)
                    runLater {
                        fileName.value = file
                        text = file
                    }
                }
            }
            region {
                hgrow = Priority.ALWAYS
            }
            vbox {
                spacing = 5.0
                label("Game Versions")
                listview<String> {
                    asyncItems { elementUtils.loadModFileGameVersions(addonId) }
                    minHeight = 50.0
                    prefHeight = 50.0
                    maxHeight = Double.MAX_VALUE
                    vgrow = Priority.ALWAYS
                }
            }
            if (dialogType != Type.NONE) {
                button(when (dialogType) {
                    Type.INSTALL -> selectedProperty.stringBinding { if (it == true) "Installed" else "Install" }
                    Type.SELECT -> selectedProperty.stringBinding { if (it == true) "Selected" else "Select" }
                    else -> SimpleStringProperty("")
                }) {
                    enableWhen(selectedProperty.not())
                    action {
                        selectCallback()
                    }
                }
            }
        }
        label("Changelog") {
            style {
                fontWeight = FontWeight.BOLD
                fontSize = 14.px
            }
        }
        webview {
            engine.userStyleSheetLocation = webStylesheet
            runAsync {
                val details = elementUtils.loadModFileChangelog(addonId)
                runLater { engine.loadContent(details) }
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

    override fun onUndock() {
        closeCallback()
    }

    enum class Type {
        NONE,
        INSTALL,
        SELECT
    }
}