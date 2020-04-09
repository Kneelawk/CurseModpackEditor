package com.kneelawk.modpackeditor.curse

import com.kneelawk.modpackeditor.data.manifest.ManifestData
import com.kneelawk.modpackeditor.data.manifest.ManifestJson
import tornadofx.toModel
import java.io.Closeable
import java.io.IOException
import java.nio.file.*
import javax.json.Json

/**
 * Reads Curse modpack information from a given path.
 */
class ModpackFile(private var modpack: Path, create: Boolean = false) : Closeable {

    companion object {
        const val MANIFEST_PATH = "/manifest.json"
    }

    /**
     * The FileSystem of the zip containing the modpack.
     */
    private var packFileSystem: FileSystem? = FileSystems.newFileSystem(modpack, mapOf("create" to create))
            ?: throw IOException("Unable to open modpack as a zip.")

    /**
     * Version of the filesystem that must be open.
     */
    private val openFileSystem: FileSystem
        get() = packFileSystem ?: throw IllegalStateException("The pack filesystem has been closed.")

    init {
        // make sure the zip is closed and flushed to the file system if the application exits
        Runtime.getRuntime().addShutdownHook(Thread {
            packFileSystem?.close()
        })
    }

    /**
     * Reads the modpack's manifest.
     */
    fun readManifest(): ManifestJson {
        return Json.createReader(Files.newBufferedReader(openFileSystem.getPath(MANIFEST_PATH)))
                .use { it.readObject() }.toModel()
    }

    /**
     * Writes the modpack's manifest.
     */
    fun writeManifest(manifest: ManifestData) {
        Json.createWriter(Files.newBufferedWriter(openFileSystem.getPath(MANIFEST_PATH)))
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

        return openFileSystem.getPath(overrides)
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
     * Closes and reopens the modpack zip to ensure it is written to the file system.
     */
    fun flush() {
        openFileSystem.close()
        packFileSystem = FileSystems.newFileSystem(modpack, emptyMap<String, String>())
                ?: throw IOException("Unable to open modpack as a zip")
    }

    /**
     * Copies the modpack zip to the new location and then opens that one instead.
     */
    fun migrate(newModpack: Path) {
        openFileSystem.close()

        if (modpack != newModpack) {
            Files.copy(modpack, newModpack, StandardCopyOption.REPLACE_EXISTING)
            modpack = newModpack
        }

        packFileSystem = FileSystems.newFileSystem(modpack, emptyMap<String, String>())
                ?: throw IOException("Unable to open modpack as a zip")
    }

    /**
     * Clones this modpack to a new location, leaving this modpack's path the same.
     */
    fun clone(newModpack: Path): ModpackFile {
        if (modpack == newModpack) {
            flush()
            throw IllegalArgumentException("Cannot clone a modpack to its current location.")
        }

        openFileSystem.close()

        Files.copy(modpack, newModpack, StandardCopyOption.REPLACE_EXISTING)

        packFileSystem = FileSystems.newFileSystem(modpack, emptyMap<String, String>()) ?: throw IOException(
            "Unable to open modpack as a zip")

        return ModpackFile(newModpack)
    }

    /**
     * Closes this modpack reader.
     */
    override fun close() {
        packFileSystem?.close()
        // free the filesystem so the shutdown hook doesn't leak memory
        packFileSystem = null
    }
}
