package com.kneelawk.modpackeditor.curse

import com.kneelawk.modpackeditor.data.curseapi.AddonData

object AddonUtils {
    fun getIconUrl(maybeUrl: String?): String {
        return maybeUrl ?: javaClass.getResource("/com/kneelawk/modpackeditor/question.png").toExternalForm()
    }

    fun getIconUrl(addon: AddonData?): String {
        return addon?.let {
            (addon.attachments.find { it.isDefault } ?: addon.attachments.firstOrNull())?.url ?: javaClass.getResource(
                "/com/kneelawk/modpackeditor/jar.png").toExternalForm()
        } ?: javaClass.getResource("/com/kneelawk/modpackeditor/question.png").toExternalForm()
    }

    fun getAuthors(addon: AddonData?): List<String> {
        return if (addon == null || addon.authors.isEmpty()) {
            listOf("Unknown")
        } else {
            addon.authors.map { it.name }
        }
    }

    fun getAuthorString(addon: AddonData?): String {
        val authors = getAuthors(addon)

        if (authors.size == 1) {
            return authors[0]
        }

        if (authors.size == 2) {
            return "${authors[0]} and ${authors[1]}"
        }

        val builder = StringBuilder()
        for (i in 0 until (authors.size - 1)) {
            builder.append("${authors[i]}, ")
        }
        builder.append("and ${authors[authors.size - 1]}")
        return builder.toString()
    }
}
