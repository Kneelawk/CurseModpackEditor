package com.kneelawk.modpackeditor.data.curseapi

import com.kneelawk.modpackeditor.data.reqLong
import com.kneelawk.modpackeditor.data.reqString
import tornadofx.JsonBuilder
import tornadofx.JsonModel
import tornadofx.boolean
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.json.JsonObject

/**
 * Describes Curse's knowledge of a minecraft version.
 */
data class MinecraftVersionJson(
        override var id: Long = 0,
        override var gameVersionId: Long = 0,
        override var versionString: String = "0.0",
        override var jarDownloadUrl: String = "",
        override var jsonDownloadUrl: String = "",
        override var approved: Boolean = false,
        override var dateModified: LocalDateTime = LocalDateTime.now(),
        override var gameVersionTypeId: Long = 0,
        override var gameVersionStatus: Long = 0,
        override var gameVersionTypeStatus: Long = 0
) : JsonModel, MinecraftVersionData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("gameVersionId", gameVersionId)
            add("versionString", versionString)
            add("jarDownloadUrl", jarDownloadUrl)
            add("jsonDownloadUrl", jsonDownloadUrl)
            add("approved", approved)
            add("dateModified", dateModified.format(DateTimeFormatter.ISO_DATE_TIME))
            add("gameVersionTypeId", gameVersionTypeId)
            add("gameVersionStatus", gameVersionStatus)
            add("gameVersionTypeStatus", gameVersionTypeStatus)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            id = reqLong("id")
            gameVersionId = reqLong("gameVersionId")
            versionString = reqString("versionString")
            jarDownloadUrl = reqString("jarDownloadUrl")
            jsonDownloadUrl = reqString("jsonDownloadUrl")
            approved = boolean("approved") ?: false
            dateModified = LocalDateTime.parse(reqString("dateModified"), DateTimeFormatter.ISO_DATE_TIME)
            gameVersionTypeId = reqLong("gameVersionTypeId")
            gameVersionStatus = reqLong("gameVersionStatus")
            gameVersionTypeStatus = reqLong("gameVersionTypeStatus")
        }
    }
}