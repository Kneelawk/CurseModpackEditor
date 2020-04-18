package com.kneelawk.modpackeditor.ui.mods

import com.kneelawk.modpackeditor.data.curseapi.AddonData
import com.kneelawk.modpackeditor.ui.ModpackEditorMainController
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import com.kneelawk.modpackeditor.ui.util.MinecraftVersionFilterFragment
import com.kneelawk.modpackeditor.ui.util.paramCellFragment
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * Created by Kneelawk on 4/12/20.
 */
class AddModsView : View() {
    private val c: AddModsController by inject()
    private val elementUtils: ElementUtils by inject()
    private val mainController: ModpackEditorMainController by inject()

    private val descriptionTitle =
            mainController.modpackTitle.stringBinding { "$it - Add Mods" }

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0
        alignment = Pos.CENTER

        titledpane("Installation Options") {
            isExpanded = false
            hbox {
                alignment = Pos.CENTER
                spacing = 10.0
                label("Automatically install version for:")
                add<MinecraftVersionFilterFragment>()
            }
        }
        titledpane("Search Options") {
            isExpanded = false
            gridpane {
                alignment = Pos.CENTER
                hgap = 10.0
                vgap = 10.0
                row {
                    checkbox("Filter by category", c.filterByCategory)
                    button {
                        maxWidth = Double.MAX_VALUE
                        enableWhen(c.filterByCategory)

                        action {
                            c.selectCategory()
                        }

                        graphic = hbox {
                            spacing = 10.0
                            alignment = Pos.CENTER

                            imageview(
                                c.selectedCategory.objectBinding { elementUtils.loadSmallImage(it?.avatarUrl) }) {
                                minWidth = 32.0
                                minHeight = 32.0
                            }
                            label(c.selectedCategory.stringBinding { it?.name ?: "Select a category" })
                        }
                    }
                }
                row {
                    checkbox("Filter by Minecraft version", c.filterByMinecraftVersion)
                    button(c.selectedMinecraftVersion) {
                        maxWidth = Double.MAX_VALUE
                        enableWhen(c.filterByMinecraftVersion)
                        action {
                            c.selectMinecraftVersion()
                        }
                    }
                }
                row {
                    label("Sort by")
                    combobox(c.sortBy, TwitchSearchSortBy.values().toList()) {
                        maxWidth = Double.MAX_VALUE
                    }
                }

                constraintsForColumn(0).hgrow = Priority.NEVER
                constraintsForColumn(1).hgrow = Priority.ALWAYS
            }
        }

        hbox {
            spacing = 10.0
            textfield(c.searchFilter) {
                hgrow = Priority.ALWAYS
                action {
                    c.pageNumber.value = 0
                    c.searchMods()
                }
            }
            button("Search") {
                maxHeight = Double.MAX_VALUE
                action {
                    c.pageNumber.value = 0
                    c.searchMods()
                }
            }
        }

        listview(c.loadedAddons) {
            vgrow = Priority.ALWAYS

            paramCellFragment(scope, AddModsElementFragment::class,
                AddModsElementFragment::detailsCallback to c::displayModDetails,
                AddModsElementFragment::filesCallback to c::displayModFiles,
                AddModsElementFragment::installLatestCallback to c::installLatest
            )

            c.loadedAddons.onChange { _: ObservableList<AddonData>? ->
                scrollTo(0)
            }
        }

        label(c.loadingStatus)

        hbox {
            button("<") {
                enableWhen(c.pageNumber.booleanBinding { it?.let { it.toLong() > 0 } ?: false }
                        .and(c.loadingStatus.booleanBinding { it == AddModsLoadingStatus.LOADED }))
                action {
                    c.pageNumber.value--
                    c.searchMods()
                }
            }
            region {
                hgrow = Priority.ALWAYS
            }
            label(c.pageNumber.stringBinding { "Page ${it?.toInt()?.plus(1)}" })
            region {
                hgrow = Priority.ALWAYS
            }
            button(">") {
                enableWhen(
                    c.loadedAddons.booleanBinding { it?.let { it.size >= AddModsController.PAGE_SIZE } ?: false }
                            .and(c.loadingStatus.booleanBinding { it == AddModsLoadingStatus.LOADED }))
                action {
                    c.pageNumber.value++
                    c.searchMods()
                }
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
}