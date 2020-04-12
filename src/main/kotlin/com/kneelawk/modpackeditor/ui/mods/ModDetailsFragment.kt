package com.kneelawk.modpackeditor.ui.mods

import com.kneelawk.modpackeditor.asset.AssetUtils
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.ui.ModpackEditorMainController
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import javafx.stage.Modality
import tornadofx.*
import kotlin.reflect.KProperty1

/**
 * Fragment that shows a mod's description.
 */
class ModDetailsFragment : Fragment() {
    val projectId: Long by param()
    val changeVersionCallback: (AddonId) -> Unit by param { _ -> }
    val closeCallback: () -> Unit by param {}

    private val elementUtils: ElementUtils by inject()
    private val mainController: ModpackEditorMainController by inject()

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
            region {
                hgrow = Priority.ALWAYS
            }
            button("Files") {
                action {
                    find<ModVersionListFragment>(mapOf<KProperty1<ModVersionListFragment, Any>, Any>(
                        ModVersionListFragment::dialogType to ModVersionListFragment.Type.INSTALL,
                        ModVersionListFragment::projectId to projectId,
                        ModVersionListFragment::selectCallback to { newAddon: AddonId ->
                            changeVersionCallback(newAddon)
                        }
                    )).openModal(modality = Modality.NONE, owner = currentWindow)
                }
            }
        }
        webview {
            engine.userStyleSheetLocation = AssetUtils.webStylesheet
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
