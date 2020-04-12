package com.kneelawk.modpackeditor.ui.util

import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.util.Callback
import tornadofx.*
import kotlin.reflect.KClass

/**
 * Slightly altered version of SmartTableCell modified to pass parameters to its fragments.
 */
@Suppress("UNCHECKED_CAST")
open class ParameterizedTableCell<S, T>(val scope: Scope, val owningColumn: TableColumn<S, T>) :
        TableCell<S, T>() {
    private val editSupport: (TableCell<S, T>.(EditEventType, T?) -> Unit)? get() = owningColumn.properties["tornadofx.editSupport"] as (TableCell<S, T>.(EditEventType, T?) -> Unit)?
    private val cellFormat: (TableCell<S, T>.(T) -> Unit)? get() = owningColumn.properties["tornadofx.cellFormat"] as (TableCell<S, T>.(T) -> Unit)?
    private var cellFragment: TableCellFragment<S, T>? = null
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
                        owningColumn.properties["tornadofx.cellFragment"] as KClass<TableCellFragment<S, T>>?
                val cellParams = owningColumn.properties["com.kneelawk.modpackeditor.cellParams"] as Map<*, Any?>?

                cellFragment = if (cellFragmentType != null) find(cellFragmentType, scope, cellParams) else null
                fresh = false
            }
            cellFragment?.apply {
                editingProperty.cleanBind(editingProperty())
                itemProperty.value = item
                rowItemProperty.value = tableView.items[index]
                cellProperty.value = this@ParameterizedTableCell
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

fun <S, T, F : TableCellFragment<S, T>> TableColumn<S, T>.paramCellFragment(scope: Scope, fragment: KClass<F>,
                                                                             vararg params: Pair<*, Any?>) {
    paramCellFragment(scope, fragment, mapOf(*params))
}

fun <S, T, F : TableCellFragment<S, T>> TableColumn<S, T>.paramCellFragment(scope: Scope, fragment: KClass<F>,
                                                                            params: Map<*, Any?>?) {
    properties["tornadofx.cellFragment"] = fragment
    properties["com.kneelawk.modpackeditor.cellParams"] = params
    if (properties["tornadofx.cellFormatCapable"] != true)
        cellFactory = Callback { ParameterizedTableCell<S, T>(scope, it) }
}
