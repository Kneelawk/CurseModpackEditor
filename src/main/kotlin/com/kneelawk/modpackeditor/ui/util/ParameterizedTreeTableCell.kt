package com.kneelawk.modpackeditor.ui.util

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback
import tornadofx.*
import kotlin.reflect.KClass

/**
 * Fragment designed to be in a cell of a TreeTable.
 */
abstract class TreeTableCellFragment<S, T> : RowItemFragment<S, T>() {
    val cellProperty: ObjectProperty<TreeTableCell<S, T>?> = SimpleObjectProperty()
    var cell by cellProperty

    val editingProperty = SimpleBooleanProperty(false)
    var editing by editingProperty

    open fun startEdit() {}

    open fun commitEdit(newValue: T) {}

    open fun cancelEdit() {}

    open fun onEdit(op: () -> Unit) {
        editingProperty.onChange { if (it) op() }
    }
}

/**
 * TreeTableCell designed to support more customizations including using a TreeTableCellFragment.
 */
@Suppress("UNCHECKED_CAST")
open class ParameterizedTreeTableCell<S, T>(val scope: Scope, val owningColumn: TreeTableColumn<S, T>) :
    TreeTableCell<S, T>() {
    private val editSupport: (TreeTableCell<S, T>.(EditEventType, T?) -> Unit)? get() = owningColumn.properties["tornadofx.editSupport"] as (TreeTableCell<S, T>.(EditEventType, T?) -> Unit)
    private val cellFormat: (TreeTableCell<S, T>.(T) -> Unit)? get() = owningColumn.properties["tornadofx.cellFormat"] as (TreeTableCell<S, T>.(T) -> Unit)
    private var cellFragment: TreeTableCellFragment<S, T>? = null
    private var fresh = true

    init {
        owningColumn.properties["tornadofx.cellFormatCapable"] = true
        owningColumn.properties["tornadofx.editCapable"] = true
        indexProperty().onChange {
            if (it == -1) clearCellFragment()
        }
    }

    override fun startEdit() {
        super.startEdit()
        editSupport?.invoke(this, EditEventType.StartEdit, null)
        cellFragment?.startEdit()
    }

    override fun commitEdit(newValue: T) {
        super.commitEdit(newValue)
        editSupport?.invoke(this, EditEventType.CommitEdit, newValue)
        cellFragment?.commitEdit(newValue)
    }

    override fun cancelEdit() {
        super.cancelEdit()
        editSupport?.invoke(this, EditEventType.CancelEdit, null)
        cellFragment?.cancelEdit()
    }

    override fun updateItem(item: T, empty: Boolean) {
        super.updateItem(item, empty)

        if (item == null || empty) {
            cleanUp()
            clearCellFragment()
        } else {
            if (fresh) {
                val cellFragmentType =
                    owningColumn.properties["tornadofx.cellFragment"] as KClass<TreeTableCellFragment<S, T>>?
                val cellParams = owningColumn.properties["com.kneelawk.modpackeditor.cellParams"] as Map<*, Any?>?

                cellFragment = if (cellFragmentType != null) find(cellFragmentType, scope, cellParams) else null
                fresh = false
            }
            cellFragment?.apply {
                editingProperty.cleanBind(editingProperty())
                itemProperty.value = item
                rowItemProperty.value = treeTableView.getTreeItem(index).value
                cellProperty.value = this@ParameterizedTreeTableCell
                graphic = root
            }
            cellFormat?.invoke(this, item)
        }
    }

    private fun cleanUp() {
        textProperty().unbind()
        graphicProperty().unbind()
        text = null
        graphic = null
        style = null
        styleClass.clear()
    }

    private fun clearCellFragment() {
        cellFragment?.apply {
            cellProperty.value = null
            itemProperty.value = null
            editingProperty.unbind()
            editingProperty.value = false
        }
    }
}

fun <S, T, F : TreeTableCellFragment<S, T>> TreeTableColumn<S, T>.paramCellFragment(
    scope: Scope, fragment: KClass<F>,
    vararg params: Pair<*, Any?>
) {
    paramCellFragment(scope, fragment, mapOf(*params))
}

fun <S, T, F : TreeTableCellFragment<S, T>> TreeTableColumn<S, T>.paramCellFragment(
    scope: Scope, fragment: KClass<F>,
    params: Map<*, Any?>?
) {
    properties["tornadofx.cellFragment"] = fragment
    properties["com.kneelawk.modpackeditor.cellParams"] = params
    if (properties["tornadofx.cellFormatCapable"] != true)
        cellFactory = Callback { ParameterizedTreeTableCell<S, T>(scope, it) }
}
