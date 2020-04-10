package com.kneelawk.modpackeditor.ui.mods

import com.kneelawk.modpackeditor.ui.ModpackEditorMainController
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Fragment that shows a mod's description.
 */
class ModDetailsFragment : Fragment() {
    val projectId: Long by param()
    val closeCallback: () -> Unit by param {}

    private val elementUtils: ElementUtils by inject()
    private val mainController: ModpackEditorMainController by inject()

    private val webStylesheet = javaClass.getResource("/com/kneelawk/modpackeditor/web.css").toExternalForm()
    private val modName = SimpleStringProperty("")
    private val descriptionTitle = mainController.modpackTitle.stringBinding(modName) { "$it - ${modName.value}" }

    override val root = vbox {
        padding = insets(10.0)
        spacing = 10.0
        hbox {
            alignment = Pos.BOTTOM_LEFT
            spacing = 10.0
            imageview {
                runAsync {
                    val loaded = elementUtils.loadImage(projectId)
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
            label("by")
            label {
                runAsync {
                    val author = elementUtils.loadModAuthor(projectId)
                    runLater { text = author }
                }
            }
        }
        webview {
            engine.userStyleSheetLocation = webStylesheet
            runAsync {
                val details = elementUtils.loadModDetails(projectId)
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
}
