package com.kneelawk.modpackeditor.ui.util

import com.kneelawk.modpackeditor.ui.SelectMinecraftVersionFragment
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.stage.Window
import tornadofx.*

/**
 * Created by Kneelawk on 4/11/20.
 */
class MinecraftVersionFilterFragment : Fragment() {
    private val modListState: ModListState by inject()
    val enableProperty: ObservableValue<Boolean> by param(SimpleBooleanProperty(true))
    val ownerWindow: () -> Window? by param { currentWindow }

    override val root = hbox {
        spacing = 10.0
        alignment = Pos.CENTER
        enableWhen(enableProperty)
        button(modListState.lowMinecraftVersion.stringBinding { it.toString() }) {
            action {
                selectLowMinecraftVersion()
            }
        }
        label("to")
        button(modListState.highMinecraftVersion.stringBinding { it.toString() }) {
            action {
                selectHighMinecraftVersion()
            }
        }
    }

    private fun selectLowMinecraftVersion() {
        find<SelectMinecraftVersionFragment>(
            SelectMinecraftVersionFragment::previousVersion to modListState.lowMinecraftVersion.value,
            SelectMinecraftVersionFragment::callback to { result: SelectMinecraftVersionFragment.Result ->
                when (result) {
                    is SelectMinecraftVersionFragment.Result.Cancel -> {
                    }
                    is SelectMinecraftVersionFragment.Result.Select -> {
                        val version = result.minecraft
                        modListState.lowMinecraftVersion.value = version
                        if (modListState.highMinecraftVersion.value < version) {
                            modListState.highMinecraftVersion.value = version
                        }
                    }
                }
            }).openModal(owner = ownerWindow())
    }

    private fun selectHighMinecraftVersion() {
        find<SelectMinecraftVersionFragment>(
            SelectMinecraftVersionFragment::previousVersion to modListState.highMinecraftVersion.value,
            SelectMinecraftVersionFragment::callback to { result: SelectMinecraftVersionFragment.Result ->
                when (result) {
                    is SelectMinecraftVersionFragment.Result.Cancel -> {
                    }
                    is SelectMinecraftVersionFragment.Result.Select -> {
                        val version = result.minecraft
                        modListState.highMinecraftVersion.value = version
                        if (modListState.lowMinecraftVersion.value > version) {
                            modListState.lowMinecraftVersion.value = version
                        }
                    }
                }
            }).openModal(owner = ownerWindow())
    }
}