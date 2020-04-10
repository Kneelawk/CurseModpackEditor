package com.kneelawk.modpackeditor.ui.util

import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Tooltip
import tornadofx.*

/**
 * Created by Kneelawk on 4/8/20.
 */
class CustomMessageDecorator(private val message: String?, severity: ValidationSeverity) : Decorator {
    private val pseudoClass: String = when (severity) {
        ValidationSeverity.Error -> "error"
        ValidationSeverity.Warning -> "warning"
        ValidationSeverity.Success -> "success"
        else -> "info"
    }
    private var tooltip: Tooltip? = null
    private var attachedToNode: Node? = null

    private var focusListener = ChangeListener<Boolean> { _, _, newValue ->
        if (newValue) showTooltip(attachedToNode!!) else tooltip?.hide()
    }

    override fun decorate(node: Node) {
        attachedToNode = node
        node.addPseudoClass(pseudoClass)

        if (message?.isNotBlank() == true) {
            tooltip = Tooltip(message)
            if (node is Control) node.tooltip = tooltip else Tooltip.install(node, tooltip)
            if (node.isFocused) showTooltip(node)
            node.focusedProperty().addListener(focusListener)
        }
    }

    private fun showTooltip(node: Node) {
        tooltip?.apply {
            if (isShowing) return
            val b = node.localToScreen(node.boundsInLocal)
            if (b != null) show(node, b.minX + 5, b.maxY)
        }
    }

    override fun undecorate(node: Node) {
        node.removePseudoClass(pseudoClass)
        tooltip?.apply {
            hide()
            Tooltip.uninstall(node, this)
        }
        node.focusedProperty().removeListener(focusListener)
    }
}