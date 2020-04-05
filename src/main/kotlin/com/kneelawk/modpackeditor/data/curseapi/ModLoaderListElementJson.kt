package com.kneelawk.modpackeditor.data.curseapi

import com.kneelawk.modpackeditor.data.reqString
import tornadofx.JsonBuilder
import tornadofx.JsonModel
import tornadofx.boolean
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.json.JsonObject

/**
 * Describes a mod loader in Curse's database.
 */
data class ModLoaderListElementJson(
        override var name: String = "",
        override var gameVersion: String = "",
        override var latest: Boolean = false,
        override var recommended: Boolean = false,
        override var dateModified: LocalDateTime = LocalDateTime.now()
) : JsonModel, ModLoaderListElementData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("gameVersion", gameVersion)
            add("latest", latest)
            add("recommended", recommended)
            add("dateModified", dateModified.format(DateTimeFormatter.ISO_DATE_TIME))
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            name = reqString("name")
            gameVersion = reqString("gameVersion")
            latest = boolean("latest") ?: false
            recommended = boolean("recommended") ?: false
            dateModified = LocalDateTime.parse(reqString("dateModified"), DateTimeFormatter.ISO_DATE_TIME)
        }
    }
}