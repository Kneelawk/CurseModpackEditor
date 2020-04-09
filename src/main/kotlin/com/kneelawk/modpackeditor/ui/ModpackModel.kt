package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.curse.ModpackFile
import com.kneelawk.modpackeditor.data.manifest.FileJson
import com.kneelawk.modpackeditor.data.manifest.ManifestJson
import com.kneelawk.modpackeditor.data.manifest.MinecraftJson
import com.kneelawk.modpackeditor.data.manifest.ModLoaderJson
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.Commit
import tornadofx.ViewModel
import tornadofx.asObservable
import tornadofx.observable
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by Kneelawk on 4/8/20.
 */
class ModpackModel(private var modpack: ModpackFile? = null,
                   val manifest: ManifestJson = modpack?.readManifest() ?: ManifestJson(
                       MinecraftJson("", arrayListOf(ModLoaderJson(""))), author = "")) : ViewModel() {
    val openModpack: ModpackFile
        get() = modpack ?: throw IllegalStateException("Modpack not open.")

    val rawModpackLocation = SimpleStringProperty("")
    val modpackLocation = bind { rawModpackLocation }

    val modpackName = bind { manifest.observable(ManifestJson::name) }
    val modpackAuthor = bind { manifest.observable(ManifestJson::author) }
    val modpackVersion = bind { manifest.observable(ManifestJson::version) }
    val minecraftVersion = bind { manifest.minecraft.observable(MinecraftJson::version) }
    val modLoaderVersion = bind { manifest.minecraft.modLoaders[0].observable(ModLoaderJson::id) }

    val modpackMods: SimpleListProperty<FileJson> = bind { SimpleListProperty(manifest.files.asObservable()) }

    init {
        // I don't like how this ties the model to the UI
        setDecorationProvider { CustomMessageDecorator(it.message, it.severity) }
    }

    override fun onCommit(commits: List<Commit>) {
        if (modpack == null) {
            modpack = ModpackFile(Paths.get(rawModpackLocation.value), true)
        } else if (commits.find { it.property === modpackLocation }?.let { it.newValue != it.oldValue } == true) {
            println("Migrating modpack...")
            openModpack.migrate(Paths.get(rawModpackLocation.value))
        }

        openModpack.writeManifest(manifest)

        val overrides = openModpack.readOverrides()
        if (!Files.exists(overrides)) {
            Files.createDirectories(overrides)
        }

        openModpack.flush()
    }

    fun readOverrides() = openModpack.readOverrides()
}