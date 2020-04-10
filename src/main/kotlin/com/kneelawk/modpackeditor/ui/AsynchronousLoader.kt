package com.kneelawk.modpackeditor.ui

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import tornadofx.runAsync
import tornadofx.runLater
import java.util.concurrent.atomic.AtomicReference

/**
 * Handles asynchronous loading of resources.
 *
 * Note: This asynchronous loader only stores a week reference to itself in the property listener.
 * A reference to this object must be kept alive in order for it to continue functioning.
 */
class AsynchronousLoader<Key, UI, I>(private val element: UI, property: ObservableValue<Key>,
                                     private val instantHandler: UI.(Key) -> Unit,
                                     private val loader: (Key) -> I,
                                     private val handler: UI.(I) -> Unit) :
        ChangeListener<Key> {
    private val lastRequest = AtomicReference<Key>()

    init {
        property.addListener(WeakChangeListener(this))

        val value = property.value
        element.instantHandler(value)
        load(element, value, loader, handler)
    }

    override fun changed(observable: ObservableValue<out Key>?, oldValue: Key, newValue: Key) {
        element.instantHandler(newValue)
        load(element, newValue, loader, handler)
    }

    private fun load(element: UI, value: Key, loader: (Key) -> I, handler: UI.(I) -> Unit) {
        lastRequest.set(value)
        runAsync {
            val result = loader(value)
            runLater {
                if (lastRequest.get() == value) {
                    element.handler(result)
                }
            }
        }
    }
}