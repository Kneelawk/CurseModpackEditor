package com.kneelawk.modpackeditor.data.curseapi

import java.time.LocalDateTime
import javax.json.JsonObject

/**
 * Immutable interface describing a mod loader in Curse's database.
 */
interface ModLoaderListElementData {
    val name: String
    val gameVersion: String
    val latest: Boolean
    val recommended: Boolean
    val dateModified: LocalDateTime

    fun toJSON(): JsonObject
}