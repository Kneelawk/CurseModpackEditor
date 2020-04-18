package com.kneelawk.modpackeditor.data.curseapi

import java.time.LocalDateTime
import javax.json.JsonObject

interface CategoryListElementData {
    val id: Long
    val name: String
    val slug: String
    val avatarUrl: String?
    val dateModified: LocalDateTime
    val parentGameCategoryId: Long?
    val rootGameCategoryId: Long?
    val gameId: Long

    fun toJSON(): JsonObject
}