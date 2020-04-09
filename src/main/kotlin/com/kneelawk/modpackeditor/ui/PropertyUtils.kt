package com.kneelawk.modpackeditor.ui

import javafx.beans.value.ObservableValue
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
