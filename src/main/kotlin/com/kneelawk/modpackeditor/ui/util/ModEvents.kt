package com.kneelawk.modpackeditor.ui.util

import com.kneelawk.modpackeditor.data.manifest.FileJson
import tornadofx.FXEvent
import tornadofx.Scope

/**
 * Event fired when a mod list item's remove button is clicked.
 */
class ModRemoveEvent(val addonId: FileJson, scope: Scope) : FXEvent(scope = scope)

/**
 * Event fired when a mod list item's required check box changes state.
 */
class ModRequiredEvent(val addonId: FileJson, val required: Boolean, scope: Scope) : FXEvent(scope = scope)

/**
 * Event fired when a mod list item's details button is clicked.
 */
class ModDetailsEvent(val addonId: FileJson, scope: Scope) : FXEvent(scope = scope)
