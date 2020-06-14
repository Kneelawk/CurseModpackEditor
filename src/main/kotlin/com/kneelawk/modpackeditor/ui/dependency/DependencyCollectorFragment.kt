package com.kneelawk.modpackeditor.ui.dependency

import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.tasks.DependencyType
import com.kneelawk.modpackeditor.ui.ModpackEditorMainController
import com.kneelawk.modpackeditor.ui.ModpackModel
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import com.kneelawk.modpackeditor.ui.util.MinecraftVersionFilterFragment
import com.kneelawk.modpackeditor.ui.util.ModListState
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * Fragment for collecting the dependencies of a mod file or collection of mod files.
 */
class DependencyCollectorFragment : Fragment() {
    val roots: List<AddonId> by param()
    val showRequiredBy: Boolean by param(false)

    private val model: ModpackModel by inject()
    private val elementUtils: ElementUtils by inject()
    private val modListState: ModListState by inject()
    private val mainController: ModpackEditorMainController by inject()

    private val primaryModName = stringProperty("")
    private val descriptionTitle = if (roots.size == 1) {
        mainController.modpackTitle.stringBinding(primaryModName) {
            "$it - ${primaryModName.value} - Dependency Scanner"
        }
    } else {
        mainController.modpackTitle.stringBinding { "$it - Dependency Scanner" }
    }

    private val collecting = SimpleBooleanProperty(false)
    private val collectProgress = SimpleDoubleProperty(0.0)
    private val collectStatus = SimpleStringProperty("Not collected.")

    private val collectedDependencies = listProperty<DependencyCollectorElement>(FXCollections.observableArrayList())

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0
        alignment = Pos.CENTER

        hbox {
            spacing = 10.0
            enableWhen(collecting.not())
            label("Minecraft version filter:")
            add<MinecraftVersionFilterFragment>()
        }

        button("Re-Scan Dependencies") {
            maxWidth = Double.MAX_VALUE
            enableWhen(collecting.not())
            action {
                scanModDependencies()
            }
        }
        label(collectStatus)
        progressbar(collectProgress) {
            maxWidth = Double.MAX_VALUE
        }

        tableview(collectedDependencies) {
            column("Add", DependencyCollectorElement::enabled) {
                prefWidth(50.0)
                minWidth(50.0)
                cellFragment(DependencyCollectorEnableFragment::class)
                makeEditable()
            }
            readonlyColumn("Info", DependencyCollectorElement::dependency) {
                prefWidth(800.0)
                minWidth(500.0)
                cellFragment(DependencyCollectorInfoFragment::class)
            }

            columnResizePolicy = SmartResize.POLICY

            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
            maxWidth = Double.MAX_VALUE
            maxHeight = Double.MAX_VALUE
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
        if (roots.size == 1) runAsync {
            val name = elementUtils.loadModName(roots.first())
            runLater {
                primaryModName.value = name
            }
        }
    }

    private fun scanModDependencies() {
        collecting.value = true
        val task = modListState.collectDependenciesTask(roots, mapOf(), model.modpackMods.map { it.projectId }.toSet())
        collectProgress.bind(task.progressProperty())
        collectStatus.bind(task.messageProperty())
        task.success { list ->
            collecting.value = false
            collectedDependencies.setAll(list.map {
                DependencyCollectorElement(
                    SimpleBooleanProperty(it.highestPriority.type higherPriorityThan DependencyType.OPTIONAL), it)
            })
        }
        task.fail {
            collecting.value = false
        }
        task.cancel {
            collecting.value = false
        }
    }
}
