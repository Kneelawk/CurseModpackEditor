package com.kneelawk.modpackeditor.ui.util

import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.SimpleAddonId

/**
 * Represents an update to an addon.
 */
data class AddonUpdate(val projectId: Long, val oldFileId: Long, val newFileId: Long) {
    val oldVersion: AddonId
        get() = SimpleAddonId(projectId, oldFileId)

    val newVersion: AddonId
        get() = SimpleAddonId(projectId, newFileId)
}
