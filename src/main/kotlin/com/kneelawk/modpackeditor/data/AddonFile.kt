package com.kneelawk.modpackeditor.data

import com.kneelawk.modpackeditor.data.curseapi.AddonFileData

/**
 * Describes an addon file along with its project id.
 */
data class AddonFile(override val projectId: Long, val fileData: AddonFileData) : AddonId {
    override val fileId = fileData.id
}