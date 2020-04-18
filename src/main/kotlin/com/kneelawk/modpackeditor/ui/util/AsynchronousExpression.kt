package com.kneelawk.modpackeditor.ui.util

import javafx.beans.binding.ObjectExpression
import javafx.beans.property.ReadOnlyObjectPropertyBase
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import tornadofx.runAsync
import tornadofx.runLater

/**
 * Created by Kneelawk on 4/17/20.
 */
// Prevent garbage collection of the input property.
class AsynchronousExpression<I, O>(@Suppress("CanBeParameter") private val property: ObservableValue<I>,
                                   private val immediate: (I?) -> O, private val loader: (I?) -> O) :
        ReadOnlyObjectPropertyBase<O>(), ChangeListener<I> {

    private var lastRequest: I? = null
    private var internalValue: O = immediate(property.value)

    init {
        property.addListener(WeakChangeListener(this))
        load(property.value)
    }

    override fun getName() = ""

    override fun getBean() = null

    override fun get() = internalValue

    override fun changed(observable: ObservableValue<out I>?, oldValue: I, newValue: I) {
        internalValue = immediate(newValue)
        fireValueChangedEvent()
        load(newValue)
    }

    private fun load(value: I) {
        lastRequest = value
        runAsync {
            val result = loader(value)
            runLater {
                if (lastRequest == value) {
                    internalValue = result
                    fireValueChangedEvent()
                }
            }
        }
    }
}

fun <I, O> ObservableValue<I>.asyncExpression(immediate: (I?) -> O, loader: (I?) -> O): ObjectExpression<O> {
    return AsynchronousExpression(this, immediate, loader)
}
