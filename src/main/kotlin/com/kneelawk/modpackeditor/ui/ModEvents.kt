package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.data.manifest.FileJson
import tornadofx.FXEvent
import tornadofx.Scope

/**
 * Event fired when a mod list item's remove button is clicked.
 */
class ModRemoveEvent(val addonId: FileJson, scope: Scope) : FXEvent(scope = scope)
