package com.kneelawk.modpackeditor.data.jumploader

import tornadofx.JsonBuilder
import javax.json.JsonObject

interface JumpLoaderData {
    val downloadRequiredFiles: Boolean
    val forceFallbackStorage: Boolean
    val overrideInferredSide: Boolean
    val disableUI: Boolean
    val launch: LaunchData
    val jars: JarsData
    val autoconfig: AutoconfigData

    fun toJSON(): JsonObject
}

interface AutoconfigData {
    val enable: Boolean
    val handler: String
    val forceUpdate: Boolean

    fun toJSON(): JsonObject
}

interface JarsData {
    val minecraft: List<MinecraftData>
    val maven: List<MavenData>

    fun toJSON(): JsonObject
}

interface MavenData {
    val mavenPath: String
    val repoUrl: String

    fun toJSON(): JsonObject
}

interface MinecraftData {
    val gameVersion: String
    val downloadType: String

    fun toJSON(): JsonObject
}

interface LaunchData {
    val mainClass: String

    fun toJSON(): JsonObject
}
