package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.ui.util.paramCellFragment
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * View that shows and manipulates the list of mods in a modpack.
 */
class ModpackModListView : View() {
    private val c: ModpackModListController by inject()

    override val root = vbox {
        padding = insets(10.0)
        spacing = 10.0

        label(c.model.modpackMods.stringBinding { "${it!!.size} Mods" })

        button("Add Mods...") {
            maxWidth = Double.MAX_VALUE
            action {
                c.addMods()
            }
        }

        hbox {
            spacing = 10.0
            vgrow = Priority.ALWAYS

            listview(c.model.modpackMods) {
                paramCellFragment(scope, ModpackModListElementFragment::class,
                    ModpackModListElementFragment::modRequireCallback to c::changeModRequired,
                    ModpackModListElementFragment::modDetailsCallback to c::showModDetails,
                    ModpackModListElementFragment::modRemoveCallback to c::removeMod,
                    ModpackModListElementFragment::modFileDetailsCallback to c::showModFileDetails,
                    ModpackModListElementFragment::modChangeVersionCallback to c::changeModVersion
                )

                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
                maxWidth = Double.MAX_VALUE
                maxHeight = Double.MAX_VALUE
            }
        }
    }
}