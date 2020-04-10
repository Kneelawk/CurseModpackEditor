package com.kneelawk.modpackeditor.data

data class AddonIdWrapper(val addonId: AddonId) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddonIdWrapper

        if (addonId.projectId != other.addonId.projectId) return false
        if (addonId.fileId != other.addonId.fileId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = addonId.projectId.hashCode()
        result = 31 * result + addonId.fileId.hashCode()
        return result
    }
}