package com.kneelawk.modpackeditor.data.curseapi

import java.time.LocalDateTime
import javax.json.JsonObject

/**
 * Immutable interface describing single addon retrieved from curse.
 */
interface AddonData {

    /**
     * The project id of this addon.
     */
    val id: Long

    /**
     * The name of this addon.
     */
    val name: String

    /**
     * A list of authors that worked on this addon.
     */
    val authors: List<AuthorData>

    /**
     * a list of attachments (often images) associated with this addon.
     */
    val attachments: List<AttachmentData>

    /**
     * The url of this addon on Curse's website.
     */
    val websiteUrl: String

    /**
     * The id of the game (Minecraft, WoW, etc.) this addon is associated with (Minecraft is 432).
     */
    val gameId: Long

    /**
     * A brief summary describing this addon.
     */
    val summary: String

    /**
     * The file id of the current default file for this addon.
     */
    val defaultFileId: Long?

    /**
     * The number of downloads curse has logged.
     */
    val downloadCount: Double

    /**
     * A selection of this addon's latest file.
     */
    val latestFiles: List<AddonFileData>

    /**
     * A list of addon categories (Tech, Magic, Aesthetic, etc.) this addon is associated with.
     */
    val categories: List<CategoryData>

    /**
     * This addon's status with the CurseForge service.
     */
    val status: Long

    /**
     * The category id of the primary category this addon is associated with.
     */
    val primaryCategoryId: Long

    /**
     * The overall category section (Mod, Modpack, Bukkit Plugin, etc.) this addon is part of.
     */
    val categorySection: CategorySectionData

    /**
     * A machine-readable name used to refer to this addon.
     */
    val slug: String

    /**
     * A list of references to the latest files for recent game versions.
     */
    val gameVersionLatestFiles: List<GameVersionLatestFileData>

    /**
     * Is this addon featured by Curse?
     */
    val isFeatured: Boolean

    /**
     * Some arbitrary score representing how popular an addon is.
     */
    val popularityScore: Double?

    /**
     * This addon's rank by popularity.
     */
    val gamePopularityRank: Long?

    /**
     * This addon's primary world language.
     */
    val primaryLanguage: String

    /**
     * Machine readable string representing the game this addon is for.
     */
    val gameSlug: String

    /**
     * Human readable string representing the game this addon is for.
     */
    val gameName: String

    /**
     * Host name of the service where this addon is located.
     */
    val portalName: String?

    /**
     * The last time this addon was modified.
     */
    val dateModified: LocalDateTime

    /**
     * When this addon was first created on the curse servers.
     */
    val dateCreated: LocalDateTime

    /**
     * When this addon was made public.
     */
    val dateReleased: LocalDateTime?

    /**
     * Whether this addon is available.
     */
    val isAvailable: Boolean

    /**
     * Whether this addon is considered 'experimental'.
     */
    val isExperiemental: Boolean

    /**
     * Used to write this data to json.
     */
    fun toJSON(): JsonObject
}

/**
 * Immutable interface describing an attachment associated with an addon.
 */
interface AttachmentData {

    /**
     * The attachment id of this attachment.
     */
    val id: Long?

    /**
     * The project id of the project this attachment is associated with.
     */
    val projectId: Long?

    /**
     * This attachment's description.
     */
    val description: String

    /**
     * Whether this is the default attachment for its associated project.
     */
    val isDefault: Boolean

    /**
     * The url of the thumbnail for this attachment.
     */
    val thumbnailUrl: String?

    /**
     * The title of this attachment.
     */
    val title: String?

    /**
     * The url of this attachment.
     */
    val url: String

    /**
     * Used to write this data to json.
     */
    fun toJSON(): JsonObject
}

/**
 * Immutable interface containing identifiers for an addon author.
 */
interface AuthorData {

    /**
     * The username of this author.
     */
    val name: String

    /**
     * The url of this author's user page on the Curse website.
     */
    val url: String

    /**
     * The Curse user id of this author.
     */
    val userId: Long

    /**
     * The Twitch user id of this author.
     */
    val twitchId: Long?

    /**
     * Used to write this data to json.
     */
    fun toJSON(): JsonObject
}

/**
 * Immutable interface describing a category an addon can fit into.
 *
 * Some potential categories are things like: Tech, Magic, Aesthetic, and others.
 */
interface CategoryData {

    /**
     * The category id of this category.
     */
    val categoryId: Long

    /**
     * The name of this category.
     */
    val name: String

    /**
     * The url of the page on Curse's website for this category.
     */
    val url: String

    /**
     * The url of the icon for this category.
     */
    val avatarUrl: String

    /**
     * The category id of the parent category of this category.
     */
    val parentId: Long?

    /**
     * The category id of the root category of this category.
     */
    val rootId: Long?

    /**
     * The game id of the game this category is associated with.
     */
    val gameId: Long

    /**
     * Used to write this data to json.
     */
    fun toJSON(): JsonObject
}

/**
 * Represents an overarching category section.
 *
 * Some examples of category sections are: Mods, Modpacks, Bukkit Plugins, and others.
 */
interface CategorySectionData {

    /**
     * The category section id of this category section.
     */
    val id: Long

    /**
     * The game id of the game this category section is associated with.
     */
    val gameId: Long

    /**
     * The name of this category section.
     */
    val name: String

    /**
     * The packaging type used for this category section.
     */
    val packageType: Long

    /**
     * The category id of this category section.
     */
    val gameCategoryId: Long

    /**
     * Used to write this data to json.
     */
    fun toJSON(): JsonObject
}

/**
 * Immutable interface referencing the latest file for a given game version.
 */
interface GameVersionLatestFileData {

    /**
     * The version of the game that this file is for.
     */
    val gameVersion: String

    /**
     * The file id of the file referenced.
     */
    val projectFileId: Long

    /**
     * The file name of the file referenced.
     */
    val projectFileName: String

    /**
     * The type of the file referenced.
     */
    val fileType: Long

    /**
     * Used to write this data to json.
     */
    fun toJSON(): JsonObject
}
