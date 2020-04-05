package com.kneelawk.modpackeditor.data.manifest

import com.kneelawk.modpackeditor.data.AddonId
import tornadofx.JsonBuilder
import javax.json.JsonObject

/**
 * Immutable interface describing a modpack manifest.
 */
interface ManifestData {
    val minecraft: MinecraftData
    val manifestType: String
    val manifestVersion: Long
    val name: String
    val version: String
    val author: String
    val projectId: Long?
    val files: List<FileData>
    val overrides: String
    val additionalJavaArgs: String?

    fun toJSON(): JsonObject
}

/**
 * Immutable interface describing a mod dependency of a modpack.
 */
interface FileData : AddonId {
    override val projectId: Long
    override val fileId: Long
    val required: Boolean

    fun toJSON(): JsonObject
}

/**
 * Immutable interface describing minecraft information for a modpack manifest.
 */
interface MinecraftData {
    val version: String
    val modLoaders: List<ModLoaderData>

    fun toJSON(): JsonObject
}

/**
 * Immutable interface describing a mod loader object in a modpack manifest.
 */
interface ModLoaderData {
    val id: String
    val primary: Boolean

    fun toJSON(): JsonObject
}
