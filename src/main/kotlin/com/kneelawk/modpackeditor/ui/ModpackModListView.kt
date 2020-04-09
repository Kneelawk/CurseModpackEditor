package com.kneelawk.modpackeditor.ui

import javafx.scene.layout.Priority
import tornadofx.*

/**
 * View that shows and manipulates the list of mods in a modpack.
 */
class ModpackModListView : View() {
    private val c: ModpackModListController by inject()

    override val root = vbox {
        spacing = 10.0

        hbox {
            spacing = 10.0
            vgrow = Priority.ALWAYS

            listview(c.model.modpackMods) {
                cellFragment(ModpackFileListFragment::class)

                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
                maxWidth = Double.MAX_VALUE
                maxHeight = Double.MAX_VALUE
            }
        }
    }
}