package com.kneelawk.modpackeditor.ui.util

import javafx.beans.Observable
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

fun <E> ObservableList<E>.containsProperty(valueProperty: ObservableValue<E>): BooleanBinding {
    return CustomBindings.contains(this, valueProperty)
}

fun <E> ObservableList<E>.containsWhereProperty(finder: (E) -> Boolean): BooleanBinding {
    return CustomBindings.containsWhere(this, finder)
}

fun <E, C> ObservableList<E>.containsWhereProperty(comparisonProperty: ObservableValue<C>,
                                                   finder: (E, C?) -> Boolean): BooleanBinding {
    return CustomBindings.containsWhere(this, comparisonProperty, finder)
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

    fun <E, L> containsWhere(op: L, finder: (E) -> Boolean): BooleanBinding
            where L : Collection<E>,
                  L : Observable {
        return object : BooleanBinding() {
            init {
                super.bind(op)
            }

            override fun dispose() {
                super.unbind(op)
            }

            override fun computeValue(): Boolean {
                return op.find(finder) != null
            }

            override fun getDependencies(): ObservableList<*> {
                return FXCollections.singletonObservableList(op)
            }
        }
    }

    fun <E, L> contains(op: L, valueProperty: ObservableValue<E>): BooleanBinding
            where L : Collection<E>,
                  L : Observable {
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

    fun <E, C, L> containsWhere(op: L, comparisonProperty: ObservableValue<C>,
                                finder: (E, C?) -> Boolean): BooleanBinding
            where L : Collection<E>,
                  L : Observable {
        return object : BooleanBinding() {
            init {
                super.bind(op, comparisonProperty)
            }

            override fun dispose() {
                super.unbind(op, comparisonProperty)
            }

            override fun computeValue(): Boolean {
                return op.find { finder(it, comparisonProperty.value) } != null
            }

            override fun getDependencies(): ObservableList<*> {
                return FXCollections.observableList(listOf(op, comparisonProperty))
            }
        }
    }
}
