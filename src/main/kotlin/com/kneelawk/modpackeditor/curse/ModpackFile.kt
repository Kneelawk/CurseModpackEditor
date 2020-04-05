package com.kneelawk.modpackeditor.curse

import com.kneelawk.modpackeditor.data.manifest.ManifestData
import com.kneelawk.modpackeditor.data.manifest.ManifestJson
import tornadofx.toModel
import java.io.Closeable
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import javax.json.Json

/**
 * Reads Curse modpack information from a given path.
 */
class ModpackFile(modpack: Path, create: Boolean = false) : Closeable {

    companion object {
        const val MANIFEST_PATH = "/manifest.json"
    }

    /**
     * The FileSystem of the zip containing the modpack.
     */
    private val packFileSystem = FileSystems.newFileSystem(modpack, mapOf("create" to create))
            ?: throw IOException("Unable to open modpack as a zip")

    /**
     * Reads the modpack's manifest.
     */
    fun readManifest(): ManifestJson {
        return Json.createReader(Files.newBufferedReader(packFileSystem.getPath(MANIFEST_PATH)))
                .use { it.readObject() }.toModel()
    }

    /**
     * Writes the modpack's manifest.
     */
    fun writeManifest(manifest: ManifestData) {
        Json.createWriter(Files.newBufferedWriter(packFileSystem.getPath(MANIFEST_PATH)))
                .use { it.writeObject(manifest.toJSON()) }
    }

    /**
     * Gets the modpack's overrides directory path.
     */
    fun readOverrides(): Path {
        var overrides = readManifest().overrides

        if (!overrides.startsWith("/")) {
            overrides = "/$overrides"
        }

        return packFileSystem.getPath(overrides)
    }

    /**
     * Extracts the contents of the modpack's overrides directory to a given location.
     */
    fun extractOverrides(toDir: Path) {
        val overrides = readOverrides()

        if (Files.exists(overrides)) {
            Files.walk(overrides).forEach { from ->
                val to = toDir.resolve(overrides.relativize(from).toString())
                if (Files.isDirectory(from)) {
                    Files.createDirectories(to)
                } else {
                    Files.createDirectories(to.parent)
                    Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }
    }

    /**
     * Closes this modpack reader.
     */
    override fun close() {
        packFileSystem.close()
    }
}
