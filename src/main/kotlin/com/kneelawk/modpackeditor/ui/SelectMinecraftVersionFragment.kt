package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.curseapi.MinecraftVersionData
import com.kneelawk.modpackeditor.data.version.MinecraftVersion
import javafx.collections.FXCollections
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Dialog to select a minecraft version.
 */
class SelectMinecraftVersionFragment : Fragment("Select a Minecraft Version") {
    val callback: (Result) -> Unit by param { _ -> }

    private val selectedVersion = objectProperty<MinecraftVersionData>(null)

    private val curseApi: CurseApi by inject()
    private val model: ModpackModel by inject()

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0
        label("Select a Minecraft Version") {
            style {
                fontSize = 16.px
                fontWeight = FontWeight.BOLD
            }
        }
        listview<MinecraftVersionData> {
            cellFormat {
                text = it.versionString
            }
            selectedVersion.bind(selectionModel.selectedItemProperty())
            runAsync {
                val versions = getMinecraftVersions()
                runLater {
                    if (items == null) {
                        items = FXCollections.observableArrayList(versions)
                    } else {
                        items.setAll(versions)
                    }
                    versions.forEachIndexed { index, data ->
                        if (data.versionString == model.minecraftVersion.value) {
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

    private fun getMinecraftVersions(): List<MinecraftVersionData> {
        return curseApi.getMinecraftVersionList().sortedByDescending { MinecraftVersion.parse(it.versionString) }
    }

    sealed class Result {
        data class Select(val minecraft: MinecraftVersionData) : Result()
        object Cancel : Result()
    }
}
