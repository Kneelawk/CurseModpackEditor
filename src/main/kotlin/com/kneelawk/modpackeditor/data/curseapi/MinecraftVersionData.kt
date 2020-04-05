package com.kneelawk.modpackeditor.data.curseapi

import java.time.LocalDateTime
import javax.json.JsonObject

/**
 * Immutable interface describing Curse's knowledge of a minecraft version.
 */
interface MinecraftVersionData {
    val id: Long
    val gameVersionId: Long
    val versionString: String
    val jarDownloadUrl: String
    val jsonDownloadUrl: String
    val approved: Boolean
    val dateModified: LocalDateTime
    val gameVersionTypeId: Long
    val gameVersionStatus: Long
    val gameVersionTypeStatus: Long

    fun toJSON(): JsonObject
}
