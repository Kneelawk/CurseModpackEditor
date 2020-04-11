package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.curse.CurseApi
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

    private val selectedVersion = objectProperty<MinecraftVersion>(null)

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
        listview<MinecraftVersion> {
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
                        if (data == MinecraftVersion.tryParse(model.minecraftVersion.value)) {
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

    private fun getMinecraftVersions(): List<MinecraftVersion> {
        val versions = curseApi.getMinecraftVersionList().map {
            MinecraftVersion.parse(it.versionString)
        }.flatMap { version ->
            if (version.patch == 0) {
                listOf(version, MinecraftVersion(version.major, version.minor, version.patch, true))
            } else {
                listOf(version)
            }
        }.sortedDescending()
        return listOf(MinecraftVersion(versions[0].major, versions[0].minor + 1, 0, true)) + versions
    }

    sealed class Result {
        data class Select(val minecraft: MinecraftVersion) : Result()
        object Cancel : Result()
    }
}
