package com.kneelawk.modpackeditor.ui.util

import javafx.application.Platform
import javafx.beans.property.Property
import tornadofx.runLater
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

/**
 * Utility to wrap properties so that set operations only happen on the JavaFX application thread.
 */
class ObjectPropertyWrapper<T>(private val property: Property<T>) {
    private val update = AtomicReference<T>()

    operator fun setValue(thisRef: Any, kProperty: KProperty<*>, value: T) {
        if (Platform.isFxApplicationThread()) {
            property.value = value
        } else if (update.getAndSet(value) == null) {
            runLater { property.value = update.getAndSet(null) }
        }
    }

    operator fun getValue(thisRef: Any, kProperty: KProperty<*>): T {
        return property.value!!
    }
}