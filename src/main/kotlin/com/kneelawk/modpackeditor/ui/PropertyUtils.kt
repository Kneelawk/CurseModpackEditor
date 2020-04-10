package com.kneelawk.modpackeditor.ui

import javafx.beans.binding.BooleanBinding
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableSet
import javafx.css.Styleable
import tornadofx.*

fun <T : Styleable> T.classWhen(clazz: String, enable: ObservableValue<Boolean>) {
    enable.onChange {
        if (it == true) {
            addClass(clazz)
        } else {
            removeClass(clazz)
        }
    }
}

fun <T : Styleable> T.pseudoClassWhen(pseudoClass: String, enable: ObservableValue<Boolean>) {
    enable.onChange {
        if (it == true) {
            addPseudoClass(pseudoClass)
        } else {
            removePseudoClass(pseudoClass)
        }
    }
}

fun <E> ObservableSet<E>.containsProperty(value: E): BooleanBinding {
    return CustomBindings.contains(this, value)
}

fun <E> ObservableSet<E>.containsProperty(valueProperty: ObservableValue<E>): BooleanBinding {
    return CustomBindings.contains(this, valueProperty)
}

object CustomBindings {
    fun <E> contains(op: ObservableSet<E>, value: E): BooleanBinding {
        return object : BooleanBinding() {
            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return op.contains(value)
            }

            override fun getDependencies(): ObservableList<*> {
                return FXCollections.singletonObservableList(op)
            }
        }
    }

    fun <E> contains(op: ObservableSet<E>, valueProperty: ObservableValue<E>): BooleanBinding {
        return object : BooleanBinding() {
            init {
                super.bind(op, valueProperty)
            }

            override fun dispose() {
                super.unbind(op, valueProperty)
            }

            override fun computeValue(): Boolean {
                return op.contains(valueProperty.value)
            }

            override fun getDependencies(): ObservableList<*> {
                return FXCollections.observableList(listOf(op, valueProperty))
            }
        }
    }
}
