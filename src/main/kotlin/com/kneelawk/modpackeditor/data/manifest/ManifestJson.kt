package com.kneelawk.modpackeditor.data.manifest

import com.kneelawk.modpackeditor.data.reqJsonObject
import com.kneelawk.modpackeditor.data.reqLong
import com.kneelawk.modpackeditor.data.reqString
import tornadofx.*
import javax.json.JsonObject

data class ManifestJson(
        override var minecraft: MinecraftJson = MinecraftJson(),
        override var manifestType: String = "minecraftModpack",
        override var manifestVersion: Long = 1,
        override var name: String = "",
        override var version: String = "0.0.1",
        override var author: String = "Unknown",
        override var projectId: Long? = null,
        override var files: MutableList<FileJson> = arrayListOf(),
        override var overrides: String = "overrides",
        override var additionalJavaArgs: String? = null
) : JsonModel, ManifestData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("minecraft", minecraft)
            add("manifestType", manifestType)
            add("manifestVersion", manifestVersion)
            add("name", name)
            add("version", version)
            add("author", author)
            projectId?.let { add("projectID", it) }
            add("files", files)
            add("overrides", overrides)
            additionalJavaArgs?.let { add("additionalJavaArgs", it) }
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            minecraft = reqJsonObject("minecraft").toModel()
            manifestType = string("manifestType") ?: "minecraftModpack"
            manifestVersion = long("manifestVersion") ?: 1
            name = reqString("name")
            version = string("version") ?: "0.0.1"
            author = string("author") ?: "Unknown"
            projectId = long("projectID")
            files = jsonArray("files")?.toModel() ?: arrayListOf()
            overrides = string("overrides") ?: "overrides"
            additionalJavaArgs = string("additionalJavaArgs")
        }
    }
}

data class FileJson(
        override var projectId: Long = 0,
        override var fileId: Long = 0,
        override var required: Boolean = true
) : JsonModel, FileData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("projectID", projectId)
            add("fileID", fileId)
            add("required", required)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            projectId = reqLong("projectID")
            fileId = reqLong("fileID")
            required = boolean("required") ?: true
        }
    }
}

data class MinecraftJson(
        override var version: String = "1.0",
        override var modLoaders: MutableList<ModLoaderJson> = arrayListOf()
) : JsonModel, MinecraftData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("version", version)
            add("modLoaders", modLoaders)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            version = reqString("version")
            modLoaders = jsonArray("modLoaders")?.toModel() ?: arrayListOf()
        }
    }
}

data class ModLoaderJson(
        override var id: String = "unknown",
        override var primary: Boolean = true
) : JsonModel, ModLoaderData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("primary", primary)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            id = reqString("id")
            primary = boolean("primary") ?: false
        }
    }
}
