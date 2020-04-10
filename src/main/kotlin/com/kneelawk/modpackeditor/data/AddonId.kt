package com.kneelawk.modpackeditor.data

/**
 * Interface describing something that can be used as a file id.
 */
interface AddonId {
    val projectId: Long
    val fileId: Long
}

/**
 * Simple file id implementation.
 */
data class SimpleAddonId(override val projectId: Long, override val fileId: Long) : AddonId {
    constructor(addonId: AddonId) : this(addonId.projectId, addonId.fileId)
}
