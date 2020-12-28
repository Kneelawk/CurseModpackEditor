package com.kneelawk.modpackeditor.ui.dependency

import com.kneelawk.modpackeditor.tasks.Dependency
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import com.kneelawk.modpackeditor.ui.util.asyncExpression
import javafx.beans.property.BooleanProperty
import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Represents an element in a list of required collected dependencies.
 */
data class RequiredDependencyCollectorElement(val enabled: BooleanProperty, val dependency: Dependency)

/**
 * Table cell type that handles displaying of the element's enabled property along with invalidation logic.
 */
class RequiredDependencyCollectorEnableFragment : TableCellFragment<RequiredDependencyCollectorElement, Boolean>() {
    override val root = vbox {
        padding = insets(5.0)
        alignment = Pos.CENTER

        checkbox(property = itemProperty)
    }
}

/**
 * Table cell type that handles displaying info about a dependency.
 */
class RequiredDependencyCollectorInfoFragment : TableCellFragment<RequiredDependencyCollectorElement, Dependency>() {
    private val elementUtils: ElementUtils by inject()

    override val root = hbox {
        padding = insets(5.0)
        spacing = 10.0
        alignment = Pos.CENTER_LEFT

        imageview(itemProperty.asyncExpression({ null }, { elementUtils.loadSmallImage(it) }))

        vbox {
            spacing = 10.0
            alignment = Pos.CENTER_LEFT
            hbox {
                spacing = 10.0
                alignment = Pos.BOTTOM_LEFT
                label(
                    itemProperty.asyncExpression({ it?.projectId?.toString() ?: "" },
                        { elementUtils.loadModName(it) })
                ) {
                    style {
                        fontWeight = FontWeight.BOLD
                        fontSize = 16.px
                    }
                }
                label("by")
                label(itemProperty.asyncExpression({ "Loading..." }, { elementUtils.loadModAuthor(it) }))
            }
            hbox {
                spacing = 10.0
                alignment = Pos.BOTTOM_LEFT
                label(
                    itemProperty.asyncExpression({ it?.fileId?.toString() ?: "" },
                        { elementUtils.loadModFileDisplay(it) })
                )
                label("-")
                label(itemProperty.asyncExpression({ "loading..." }, { elementUtils.loadModFileName(it) }))
            }
        }
    }
}
