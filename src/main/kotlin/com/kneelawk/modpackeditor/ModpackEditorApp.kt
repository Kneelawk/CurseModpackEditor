package com.kneelawk.modpackeditor

import com.kneelawk.modpackeditor.net.setupRestEngine
import com.kneelawk.modpackeditor.net.shutdownCustomClient
import com.kneelawk.modpackeditor.tasks.shutdownThreadPool
import com.kneelawk.modpackeditor.ui.ModpackEditorStartView
import tornadofx.App
import tornadofx.importStylesheet
import tornadofx.launch

fun main(args: Array<String>) {
    launch<ModpackEditorApp>(args)
}

class ModpackEditorApp : App(ModpackEditorStartView::class) {
    init {
        importStylesheet(javaClass.getResource("obsidian/obsidian.css").toExternalForm())
        importStylesheet(javaClass.getResource("style.css").toExternalForm())
        setupRestEngine()
    }

    override fun stop() {
        super.stop()
        shutdownCustomClient()
        shutdownThreadPool()
    }
}
