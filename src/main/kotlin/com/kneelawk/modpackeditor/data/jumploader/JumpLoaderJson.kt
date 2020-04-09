package com.kneelawk.modpackeditor.data.jumploader

import com.kneelawk.modpackeditor.data.reqJsonArray
import com.kneelawk.modpackeditor.data.reqJsonObject
import com.kneelawk.modpackeditor.data.reqString
import tornadofx.*
import javax.json.JsonObject

data class JumpLoaderJson(
        override var downloadRequiredFiles: Boolean = true,
        override var forceFallbackStorage: Boolean = false,
        override var overrideInferredSide: Boolean = false,
        override var disableUI: Boolean = true,
        override var launch: LaunchJson = LaunchJson(),
        override var jars: JarsJson = JarsJson(),
        override var autoconfig: AutoconfigJson = AutoconfigJson()
) : JsonModel, JumpLoaderData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("downloadRequiredFiles", downloadRequiredFiles)
            add("forceFallbackStorage", forceFallbackStorage)
            add("overrideInferredSide", overrideInferredSide)
            add("disableUI", disableUI)
            add("launch", launch)
            add("jars", jars)
            add("autoconfig", autoconfig)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            downloadRequiredFiles = boolean("downloadRequiredFiles") ?: true
            forceFallbackStorage = boolean("forceFallbackStorage") ?: false
            overrideInferredSide = boolean("overrideInferredSide") ?: false
            disableUI = boolean("disableUI") ?: true
            launch = reqJsonObject("launch").toModel()
            jars = reqJsonObject("jars").toModel()
            autoconfig = reqJsonObject("autoconfig").toModel()
        }
    }
}

data class AutoconfigJson(
        override var enable: Boolean = true,
        override var handler: String = "fabric",
        override var forceUpdate: Boolean = true
) : JsonModel, AutoconfigData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("enable", enable)
            add("handler", handler)
            add("forceUpdate", forceUpdate)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            enable = boolean("enable") ?: true
            handler = reqString("handler")
            forceUpdate = boolean("forceUpdate") ?: true
        }
    }
}

data class JarsJson(
        override var minecraft: MutableList<MinecraftJson> = arrayListOf(),
        override var maven: MutableList<MavenJson> = arrayListOf()
) : JsonModel, JarsData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("minecraft", minecraft)
            add("maven", maven)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            minecraft = reqJsonArray("minecraft").toModel()
            maven = jsonArray("maven")?.toModel() ?: arrayListOf()
        }
    }
}

data class MavenJson(
        override var mavenPath: String = "",
        override var repoUrl: String = ""
) : JsonModel, MavenData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("mavenPath", mavenPath)
            add("repoUrl", repoUrl)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            mavenPath = reqString("mavenPath")
            repoUrl = reqString("repoUrl")
        }
    }
}

data class MinecraftJson(
        override var gameVersion: String = "1.0",
        override var downloadType: String = "client"
) : JsonModel, MinecraftData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("gameVersion", gameVersion)
            add("downloadType", downloadType)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            gameVersion = reqString("gameVersion")
            downloadType = string("downloadType") ?: "client"
        }
    }
}

data class LaunchJson(
        override var mainClass: String = "net.fabricmc.loader.launch.knot.KnotClient"
) : JsonModel, LaunchData {
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("mainClass", mainClass)
        }
    }

    override fun toJSON() = super.toJSON()

    override fun updateModel(json: JsonObject) {
        with(json) {
            mainClass = reqString("mainClass")
        }
    }
}
