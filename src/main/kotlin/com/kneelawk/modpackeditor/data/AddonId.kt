package com.kneelawk.modpackeditor.data

import com.kneelawk.modpackeditor.data.manifest.FileJson

/**
 * Interface describing something that can be used as a file id.
 */
interface AddonId {
    val projectId: Long
    val fileId: Long

    fun toFileJson(required: Boolean) = FileJson(projectId, fileId, required)
}

/**
 * Simple file id implementation.
 */
data class SimpleAddonId(override val projectId: Long, override val fileId: Long) : AddonId {
    constructor(addonId: AddonId) : this(addonId.projectId, addonId.fileId)
}
