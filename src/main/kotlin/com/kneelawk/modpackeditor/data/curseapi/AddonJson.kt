package com.kneelawk.modpackeditor.data.curseapi

import com.kneelawk.modpackeditor.data.reqDouble
import com.kneelawk.modpackeditor.data.reqJsonObject
import com.kneelawk.modpackeditor.data.reqLong
import com.kneelawk.modpackeditor.data.reqString
import tornadofx.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.json.JsonObject

/**
 * A single addon retrieved from curse.
 */
data class AddonJson(

        /**
         * The project id of this addon.
         */
        override var id: Long = 0,

        /**
         * The name of this addon.
         */
        override var name: String = "",

        /**
         * A list of authors that worked on this addon.
         */
        override var authors: List<AuthorJson> = emptyList(),

        /**
         * a list of attachments (often images) associated with this addon.
         */
        override var attachments: List<AttachmentJson> = emptyList(),

        /**
         * The url of this addon on Curse's website.
         */
        override var websiteUrl: String = "",

        /**
         * The id of the game (Minecraft, WoW, etc.) this addon is associated with (Minecraft is 432).
         */
        override var gameId: Long = 432,

        /**
         * A brief summary describing this addon.
         */
        override var summary: String = "",

        /**
         * The file id of the current default file for this addon.
         */
        override var defaultFileId: Long? = null,

        /**
         * The number of downloads curse has logged.
         */
        override var downloadCount: Double = 0.0,

        /**
         * A selection of this addon's latest file.
         */
        override var latestFiles: List<AddonFileJson> = emptyList(),

        /**
         * A list of addon categories (Tech, Magic, Aesthetic, etc.) this addon is associated with.
         */
        override var categories: List<CategoryJson> = emptyList(),

        /**
         * This addon's status with the CurseForge service.
         */
        override var status: Long = 0,

        /**
         * The category id of the primary category this addon is associated with.
         */
        override var primaryCategoryId: Long = 0,

        /**
         * The overall category section (Mod, Modpack, Bukkit Plugin, etc.) this addon is part of.
         */
        override var categorySection: CategorySectionJson = CategorySectionJson(),

        /**
         * A machine-readable name used to refer to this addon.
         */
        override var slug: String = "",

        /**
         * A list of references to the latest files for recent game versions.
         */
        override var gameVersionLatestFiles: List<GameVersionLatestFileJson> = emptyList(),

        /**
         * Is this addon featured by Curse?
         */
        override var isFeatured: Boolean = false,

        /**
         * Some arbitrary score representing how popular an addon is.
         */
        override var popularityScore: Double? = null,

        /**
         * This addon's rank by popularity.
         */
        override var gamePopularityRank: Long? = null,

        /**
         * This addon's primary world language.
         */
        override var primaryLanguage: String = "enUS",

        /**
         * Machine readable string representing the game this addon is for.
         */
        override var gameSlug: String = "minecraft",

        /**
         * Human readable string representing the game this addon is for.
         */
        override var gameName: String = "Minecraft",

        /**
         * Host name of the service where this addon is located.
         */
        override var portalName: String? = "www.curseforge.com",

        /**
         * The last time this addon was modified.
         */
        override var dateModified: LocalDateTime = LocalDateTime.now(),

        /**
         * When this addon was first created on the curse servers.
         */
        override var dateCreated: LocalDateTime = LocalDateTime.now(),

        /**
         * When this addon was made public.
         */
        override var dateReleased: LocalDateTime? = null,

        /**
         * Whether this addon is available.
         */
        override var isAvailable: Boolean = true,

        /**
         * Whether this addon is considered 'experimental'.
         */
        override var isExperiemental: Boolean = false
) : JsonModel, AddonData {

    /**
     * Serializes this addon to JSON.
     */
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("name", name)
            add("authors", authors.toJSON())
            add("attachments", attachments.toJSON())
            add("websiteUrl", websiteUrl)
            add("gameId", gameId)
            add("summary", summary)
            defaultFileId?.let { add("defaultFileId", it) }
            add("downloadCount", downloadCount)
            add("latestFiles", latestFiles.toJSON())
            add("categories", categories.toJSON())
            add("status", status)
            add("primaryCategoryId", primaryCategoryId)
            add("categorySection", categorySection)
            add("slug", slug)
            add("gameVersionLatestFiles", gameVersionLatestFiles.toJSON())
            add("isFeatured", isFeatured)
            popularityScore?.let { add("popularityScore", it) }
            gamePopularityRank?.let { add("gamePopularityRank", it) }
            add("primaryLanguage", primaryLanguage)
            add("gameSlug", gameSlug)
            add("gameName", gameName)
            portalName?.let { add("portalName", it) }
            add("dateModified", dateModified.format(DateTimeFormatter.ISO_DATE_TIME))
            add("dateCreated", dateCreated.format(DateTimeFormatter.ISO_DATE_TIME))
            dateReleased?.let { add("dateReleased", it.format(DateTimeFormatter.ISO_DATE_TIME)) }
            add("isAvailable", isAvailable)
            add("isExperiemental", isExperiemental)
        }
    }

    override fun toJSON() = super.toJSON()

    /**
     * Loads this addon from JSON.
     */
    override fun updateModel(json: JsonObject) {
        with(json) {
            id = reqLong("id")
            name = reqString("name")
            authors = jsonArray("authors")?.toModel() ?: emptyList()
            attachments = jsonArray("attachments")?.toModel() ?: emptyList()
            websiteUrl = reqString("websiteUrl")
            gameId = long("gameId") ?: 432
            summary = reqString("summary")
            defaultFileId = long("defaultFileId")
            downloadCount = reqDouble("downloadCount")
            latestFiles = jsonArray("latestFiles")?.toModel() ?: emptyList()
            categories = jsonArray("categories")?.toModel() ?: emptyList()
            status = reqLong("status")
            primaryCategoryId = reqLong("primaryCategoryId")
            categorySection = reqJsonObject("categorySection").toModel()
            slug = reqString("slug")
            gameVersionLatestFiles = jsonArray("gameVersionLatestFiles")?.toModel() ?: emptyList()
            isFeatured = boolean("isFeatured") ?: false
            popularityScore = double("popularityScore")
            gamePopularityRank = long("gamePopularityRank")
            primaryLanguage = string("primaryLanguage") ?: "enUS"
            gameSlug = string("gameSlug") ?: "minecraft"
            gameName = string("gameName") ?: "Minecraft"
            portalName = string("portalName")
            dateModified = LocalDateTime.parse(reqString("dateModified"), DateTimeFormatter.ISO_DATE_TIME)
            dateCreated = LocalDateTime.parse(reqString("dateCreated"), DateTimeFormatter.ISO_DATE_TIME)
            dateReleased = string("dateRelease")?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
            isAvailable = boolean("isAvailable") ?: true
            isExperiemental = boolean("isExperiemental") ?: false
        }
    }
}

/**
 * Describes an attachment associated with an addon.
 */
data class AttachmentJson(

        /**
         * The attachment id of this attachment.
         */
        override var id: Long? = null,

        /**
         * The project id of the project this attachment is associated with.
         */
        override var projectId: Long? = null,

        /**
         * This attachment's description.
         */
        override var description: String = "",

        /**
         * Whether this is the default attachment for its associated project.
         */
        override var isDefault: Boolean = false,

        /**
         * The url of the thumbnail for this attachment.
         */
        override var thumbnailUrl: String? = null,

        /**
         * The title of this attachment.
         */
        override var title: String? = null,

        /**
         * The url of this attachment.
         */
        override var url: String = ""
) : JsonModel, AttachmentData {

    /**
     * Serializes this attachment descriptor to JSON.
     */
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            id?.let { add("id", it) }
            projectId?.let { add("projectId", it) }
            add("description", description)
            add("isDefault", isDefault)
            thumbnailUrl?.let { add("thumbnailUrl", it) }
            title?.let { add("title", it) }
            add("url", url)
        }
    }

    override fun toJSON() = super.toJSON()

    /**
     * Loads this attachment descriptor from JSON.
     */
    override fun updateModel(json: JsonObject) {
        with(json) {
            id = long("id")
            projectId = long("projectId")
            description = string("description") ?: ""
            isDefault = boolean("isDefault") ?: false
            thumbnailUrl = string("thumbnailUrl")
            title = string("title")
            url = reqString("url")
        }
    }
}

/**
 * Contains identifiers for an addon author.
 */
data class AuthorJson(

        /**
         * The username of this author.
         */
        override var name: String = "",

        /**
         * The url of this author's user page on the Curse website.
         */
        override var url: String = "",

        /**
         * The Curse user id of this author.
         */
        override var userId: Long = 0,

        /**
         * The Twitch user id of this author.
         */
        override var twitchId: Long? = null
) : JsonModel, AuthorData {

    /**
     * Serializes this author descriptor to JSON.
     */
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("name", name)
            add("url", url)
            add("userId", userId)
            twitchId?.let { add("twitchId", it) }
        }
    }

    override fun toJSON() = super.toJSON()

    /**
     * Loads this author descriptor from JSON.
     */
    override fun updateModel(json: JsonObject) {
        with(json) {
            name = reqString("name")
            url = reqString("url")
            userId = reqLong("userId")
            twitchId = long("twitchId")
        }
    }
}

/**
 * Describes a category an addon can fit into.
 *
 * Some potential categories are things like: Tech, Magic, Aesthetic, and others.
 */
data class CategoryJson(

        /**
         * The category id of this category.
         */
        override var categoryId: Long = 0,

        /**
         * The name of this category.
         */
        override var name: String = "",

        /**
         * The url of the page on Curse's website for this category.
         */
        override var url: String = "",

        /**
         * The url of the icon for this category.
         */
        override var avatarUrl: String = "",

        /**
         * The category id of the parent category of this category.
         */
        override var parentId: Long? = null,

        /**
         * The category id of the root category of this category.
         */
        override var rootId: Long? = null,

        /**
         * The game id of the game this category is associated with.
         */
        override var gameId: Long = 432
) : JsonModel, CategoryData {

    /**
     * Serializes this category descriptor to JSON.
     */
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("categoryId", categoryId)
            add("name", name)
            add("url", url)
            add("avatarUrl", avatarUrl)
            parentId?.let { add("parentId", it) }
            rootId?.let { add("rootId", it) }
            add("gameId", gameId)
        }
    }

    override fun toJSON() = super.toJSON()

    /**
     * Loads this category descriptor from JSON.
     */
    override fun updateModel(json: JsonObject) {
        with(json) {
            categoryId = reqLong("categoryId")
            name = reqString("name")
            url = reqString("url")
            avatarUrl = reqString("avatarUrl")
            parentId = long("parentId")
            rootId = long("rootId")
            gameId = long("gameId") ?: 432
        }
    }
}

/**
 * Represents an overarching category section.
 *
 * Some examples of category sections are: Mods, Modpacks, Bukkit Plugins, and others.
 */
data class CategorySectionJson(

        /**
         * The category section id of this category section.
         */
        override var id: Long = 0,

        /**
         * The game id of the game this category section is associated with.
         */
        override var gameId: Long = 432,

        /**
         * The name of this category section.
         */
        override var name: String = "",

        /**
         * The packaging type used for this category section.
         */
        override var packageType: Long = 0,

        /**
         * The category id of this category section.
         */
        override var gameCategoryId: Long = 0
) : JsonModel, CategorySectionData {

    /**
     * Serializes this catetory section descriptor to JSON.
     */
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("id", id)
            add("gameId", gameId)
            add("name", name)
            add("packageType", packageType)
            add("gameCategoryId", gameCategoryId)
        }
    }

    override fun toJSON() = super.toJSON()

    /**
     * Loads this category section descriptor from JSON.
     */
    override fun updateModel(json: JsonObject) {
        with(json) {
            id = reqLong("id")
            gameId = long("gameId") ?: 432
            name = reqString("name")
            packageType = reqLong("packageType")
            gameCategoryId = reqLong("gameCategoryId")
        }
    }
}

/**
 * References the latest file for a given game version.
 */
data class GameVersionLatestFileJson(

        /**
         * The version of the game that this file is for.
         */
        override var gameVersion: String,

        /**
         * The file id of the file referenced.
         */
        override var projectFileId: Long,

        /**
         * The file name of the file referenced.
         */
        override var projectFileName: String,

        /**
         * The type of the file referenced.
         */
        override var fileType: Long
) : JsonModel, GameVersionLatestFileData {

    /**
     * Serializes this file reference to JSON.
     */
    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("gameVersion", gameVersion)
            add("projectFileId", projectFileId)
            add("projectFileName", projectFileName)
            add("fileType", fileType)
        }
    }

    override fun toJSON() = super.toJSON()

    /**
     * Loads this file reference from JSON.
     */
    override fun updateModel(json: JsonObject) {
        with(json) {
            gameVersion = reqString("gameVersion")
            projectFileId = reqLong("projectFileId")
            projectFileName = reqString("projectFileName")
            fileType = reqLong("fileType")
        }
    }
}
