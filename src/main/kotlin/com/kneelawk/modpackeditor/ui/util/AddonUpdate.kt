package com.kneelawk.modpackeditor.ui.util

import com.kneelawk.modpackeditor.data.AddonId

/**
 * Represents an update to an addon.
 */
data class AddonUpdate(val oldVersion: AddonId, val newVersion: AddonId)
