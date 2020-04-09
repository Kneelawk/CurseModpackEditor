package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.curseapi.MinecraftVersionData
import com.kneelawk.modpackeditor.data.version.MinecraftVersion
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
            asyncItems {
                curseApi.getMinecraftVersionList().sortedByDescending { MinecraftVersion.parse(it.versionString) }
            }
            selectedVersion.bind(selectionModel.selectedItemProperty())
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
                isDisable = selectedVersion.value == null
                isDefaultButton = true
                action {
                    close()
                    callback(Result.Select(selectedVersion.value!!))
                }
                selectedVersion.addListener { _, _, newValue ->
                    isDisable = newValue == null
                }
            }
        }
    }

    sealed class Result {
        data class Select(val minecraft: MinecraftVersionData) : Result()
        object Cancel : Result()
    }
}
