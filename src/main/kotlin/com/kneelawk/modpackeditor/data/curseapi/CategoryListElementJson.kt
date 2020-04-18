package com.kneelawk.modpackeditor.data.curseapi

import com.kneelawk.modpackeditor.data.reqLong
import com.kneelawk.modpackeditor.data.reqString
import tornadofx.JsonBuilder
import tornadofx.JsonModel
import tornadofx.long
import tornadofx.string
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.json.JsonObject

data class CategoryListElementJson (
        override var id: Long = 0,
        override var name: String = "",
        override var slug: String = "",
        override var avatarUrl: String? = null,
        override var dateModified: LocalDateTime = LocalDateTime.now(),
        override var parentGameCategoryId: Long? = null,
        override var rootGameCategoryId: Long? = null,
        override var gameId: Long = 432
) : JsonModel, CategoryListElementData {
    override fun toJSON(json: JsonBuilder) {
        with (json) {
            add("id", id)
            add("name", name)
            add("avatarUrl", avatarUrl)
            add("dateModified", dateModified.format(DateTimeFormatter.ISO_DATE_TIME))
            add("parentGameCategoryId", parentGameCategoryId)
            add("rootGameCategoryId", rootGameCategoryId)
            add("gameId", gameId)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with (json) {
            id = reqLong("id")
            name = reqString("name")
            slug = reqString("slug")
            avatarUrl = string("avatarUrl")
            dateModified = LocalDateTime.parse(reqString("dateModified"), DateTimeFormatter.ISO_DATE_TIME)
            parentGameCategoryId = long("parentGameCategoryId")
            rootGameCategoryId = long("rootGameCategoryId")
            gameId = reqLong("gameId")
        }
    }
}