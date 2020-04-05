package com.kneelawk.modpackeditor.data.curseapi

import java.time.LocalDateTime
import javax.json.JsonObject

/**
 * Immutable interface for describing a curse addon file.
 */
interface AddonFileData {
    val id: Long
    val displayName: String
    val fileName: String
    val fileDate: LocalDateTime
    val fileLength: Long

    /**
     * Known values are:
     * * 1: Release
     * * 2: Beta
     * * 3: Alpha
     */
    val releaseType: Long

    /**
     * Known values are:
     * * 1: Normal
     * * 2: SemiNormal
     */
    val fileStatus: Long
    val downloadUrl: String
    val isAlternate: Boolean
    val alternateFileId: Long?
    val dependencies: List<DependencyData>
    val isAvailable: Boolean
    val packageFingerprint: Long?
    val gameVersion: List<String>
    val serverPackFileId: Long?
    val hasInstallScript: Boolean
    val gameVersionDateReleased: LocalDateTime?

    fun toJSON(): JsonObject
}

/**
 * Immutable interface describing a dependency.
 */
interface DependencyData {
    val addonId: Long

    /**
     * Known values are:
     * * 1: Required
     * * 2: Optional
     * * 3: Embedded
     */
    val type: Long

    fun toJSON(): JsonObject
}
