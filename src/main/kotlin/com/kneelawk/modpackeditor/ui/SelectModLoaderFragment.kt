package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.curseapi.ModLoaderListElementData
import com.kneelawk.modpackeditor.data.version.ForgeVersion
import javafx.collections.FXCollections
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Dialog to select a mod loader.
 */
class SelectModLoaderFragment : Fragment("Select a Forge Version") {
    val minecraftVersion: String? by param<String?>(null)
    val callback: (Result) -> Unit by param { _ -> }

    private val modLoaderList = listProperty<ModLoaderListElementData>(FXCollections.observableArrayList())
    private val selectedVersion = objectProperty<ModLoaderListElementData>(null)
    private val onlyCompatible = booleanProperty(true)

    private val curseApi: CurseApi by inject()
    private val model: ModpackModel by inject()

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0
        label("Select a Forge Version") {
            style {
                fontSize = 16.px
                fontWeight = FontWeight.BOLD
            }
        }
        checkbox("Only show compatible versions", onlyCompatible) {
            onlyCompatible.value = minecraftVersion != null
            isDisable = minecraftVersion == null
            action {
                runAsync {
                    val modLoaders = getModLoaderList()
                    runLater {
                        modLoaderList.value.setAll(modLoaders)
                    }
                }
            }
        }
        listview(modLoaderList) {
            cellFragment(ModLoaderListFragment::class)
            selectedVersion.bind(selectionModel.selectedItemProperty())
            runAsync {
                val modLoaders = getModLoaderList()
                runLater {
                    if (items == null) {
                        items = FXCollections.observableArrayList(modLoaders)
                    } else {
                        items.setAll(modLoaders)
                    }
                    modLoaders.forEachIndexed { index, data ->
                        if (data.name == model.modLoaderVersion.value) {
                            selectionModel.select(index)
                        }
                    }
                }
            }
            setOnMouseClicked {
                if (it.clickCount == 2) {
                    close()
                    callback(Result.Select(selectedVersion.value!!))
                }
            }
        }
        hbox {
            spacing = 10.0
            region {
                hgrow = Priority.ALWAYS
            }
            button("Cancel") {
                action {
                    close()
                    callback(Result.Cancel)
                }
            }
            button("Select") {
                isDefaultButton = true
                action {
                    close()
                    callback(Result.Select(selectedVersion.value!!))
                }
                enableWhen(selectedVersion.isNotNull)
            }
        }
    }

    private fun getModLoaderList(): List<ModLoaderListElementData> {
        val modLoaders = curseApi.getModLoaderList().sortedByDescending { ForgeVersion.parse(it.name) }

        val mcVersion = minecraftVersion
        return if (onlyCompatible.value && mcVersion != null && mcVersion.isNotBlank()) {
            modLoaders.filter { it.gameVersion == mcVersion }
        } else {
            modLoaders
        }
    }

    sealed class Result {
        data class Select(val modLoader: ModLoaderListElementData) : Result()
        object Cancel : Result()
    }
}

class ModLoaderListFragment : ListCellFragment<ModLoaderListElementData>() {
    override val root = hbox {
        spacing = 10.0
        region {
            addClass("recommended-icon")
            prefWidth = 16.0
            prefHeight = 16.0
            itemProperty.onChange {
                if (it != null && it.recommended) {
                    addPseudoClass("recommended")
                } else {
                    removePseudoClass("recommended")
                }
            }
        }
        label(stringBinding(itemProperty) { value?.name ?: "" })
        region {
            hgrow = Priority.ALWAYS
        }
    }
}
