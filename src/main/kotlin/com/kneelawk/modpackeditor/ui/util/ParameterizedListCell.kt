package com.kneelawk.modpackeditor.ui.util

import javafx.collections.ObservableMap
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback
import tornadofx.*
import kotlin.reflect.KClass

/**
 * Created by Kneelawk on 4/11/20.
 */
@Suppress("UNCHECKED_CAST")
class ParameterizedListCell<T>(val scope: Scope, listView: ListView<T>?) : ListCell<T>() {
    private val smartProperties: ObservableMap<Any, Any> =
            listView?.properties ?: HashMap(properties.orEmpty()).asObservable()
    private val editSupport: (ListCell<T>.(EditEventType, T?) -> Unit)? get() = smartProperties["tornadofx.editSupport"] as (ListCell<T>.(EditEventType, T?) -> Unit)?
    private val cellFormat: (ListCell<T>.(T) -> Unit)? get() = smartProperties["tornadofx.cellFormat"] as (ListCell<T>.(T) -> Unit)?
    private var cellFragment: ListCellFragment<T>? = null
    private var fresh = true

    init {
        if (listView != null) {
            properties?.let { listView.properties?.putAll(it) }
            setCapabilities(listView)
        }
        indexProperty().onChange {
            if (it == -1) clearCellFragment()
        }
    }

    companion object {
        internal fun setCapabilities(listView: ListView<*>) {
            listView.properties["tornadofx.cellFormatCapable"] = true
            listView.properties["tornadofx.editCapable"] = true
        }
    }

    override fun startEdit() {
        super.startEdit()
        editSupport?.invoke(this, EditEventType.StartEdit, null)
    }

    override fun commitEdit(newValue: T) {
        super.commitEdit(newValue)
        editSupport?.invoke(this, EditEventType.CommitEdit, newValue)
    }

    override fun cancelEdit() {
        super.cancelEdit()
        editSupport?.invoke(this, EditEventType.CancelEdit, null)
    }

    override fun updateItem(item: T, empty: Boolean) {
        super.updateItem(item, empty)

        if (item == null || empty) {
            textProperty().unbind()
            graphicProperty().unbind()
            text = null
            graphic = null
            style = null
            clearCellFragment()
        } else {
            if (fresh) {
                val cellFragmentType = smartProperties["tornadofx.cellFragment"] as KClass<ListCellFragment<T>>?
                val cellParams = smartProperties["com.kneelawk.modpackeditor.cellParams"] as Map<*, Any?>?
                cellFragment = if (cellFragmentType != null) find(cellFragmentType, scope, cellParams) else null
                fresh = false
            }
            cellFragment?.apply {
                editingProperty.cleanBind(editingProperty())
                itemProperty.value = item
                cellProperty.value = this@ParameterizedListCell
                graphic = root
            }
            cellFormat?.invoke(this, item)
        }
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

fun <T, F : ListCellFragment<T>> ListView<T>.paramCellFragment(scope: Scope, fragment: KClass<F>,
                                                               vararg params: Pair<*, Any?>) {
    paramCellFragment(scope, fragment, mapOf(*params))
}

fun <T, F : ListCellFragment<T>> ListView<T>.paramCellFragment(scope: Scope, fragment: KClass<F>,
                                                               params: Map<*, Any?>?) {
    properties["tornadofx.cellFragment"] = fragment
    properties["com.kneelawk.modpackeditor.cellParams"] = params
    if (properties["tornadofx.cellFormatCapable"] != true) {
        ParameterizedListCell.setCapabilities(this)
        cellFactory = Callback { ParameterizedListCell(scope, it) }
    }
}
