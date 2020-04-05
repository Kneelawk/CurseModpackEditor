package com.kneelawk.modpackeditor.data

import com.kneelawk.modpackeditor.data.curseapi.AddonFileData

/**
 * Object describing something that could describe an addon file or possibly just its addon id.
 */
data class MaybeAddonFile(val projectId: Long, val fileId: Long, val fileData: AddonFileData?) {
    constructor(projectId: Long, addonData: AddonFileData) : this(projectId, addonData.id, addonData)
    constructor(addonId: AddonId) : this(addonId.projectId, addonId.fileId, null)
}
