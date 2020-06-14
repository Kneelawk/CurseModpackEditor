package com.kneelawk.modpackeditor.tasks

import arrow.core.Either
import arrow.core.Right
import com.kneelawk.modpackeditor.cache.ResourceCaches
import com.kneelawk.modpackeditor.curse.AddonVersionSelectionError
import com.kneelawk.modpackeditor.curse.ModListUtils
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.SimpleAddonId
import com.kneelawk.modpackeditor.data.version.MinecraftVersion
import javafx.concurrent.Task
import tornadofx.find

/**
 * Collects a list of dependencies for the list of addons.
 */
class CollectDependenciesTask(private val addons: List<AddonId>, private val selectedVersions: Map<Long, Long>,
                              private val ignored: Set<Long>,
                              private val lowMinecraftVersion: MinecraftVersion,
                              private val highMinecraftVersion: MinecraftVersion) : Task<CollectDependenciesResult>() {
    private val modListUtils = find<ModListUtils>()
    private val cache = find<ResourceCaches>()

    private val rootProjects = addons.map { it.projectId }.toSet()
    private val unresolved = mutableMapOf<Long, AddonVersionSelectionError>()

    override fun call(): CollectDependenciesResult {
        updateProgress(0.0, 1.0)
        updateMessage("Collecting dependencies...")

        val segmentSize = 1.0 / addons.size.toDouble()

        addons.forEachIndexed { index, addonId ->
            collectRequired(addonId, addonId, index.toDouble() / addons.size.toDouble(), segmentSize)
        }

        updateProgress(1.0, 1.0)
        updateMessage("Dependencies collected.")

        return CollectDependenciesResult(found.values, unresolved)
    }

    /**
     * Returns all the dependencies of [addonId] that are not already in [found] or [ignored].
     */
    private fun collectRequired(addonId: AddonId, root: AddonId, offset: Double, size: Double) {
        updateProgress(offset, 1.0)
        updateMessage("Getting info: ${addonId.projectId}/${addonId.fileId}")

        val file = cache.getAddonFile(addonId)
        val dependencies = file.orNull()?.dependencies.orEmpty()
        val segmentSize = size / 2.0 / dependencies.size.toDouble()

        updateProgress(offset + size / 2.0, 1.0)
        updateMessage("Collecting dependencies: ${file.orNull()?.fileName}")

        dependencies.forEachIndexed { index, dep ->
            if (!ignored.contains(dep.addonId) && !rootProjects.contains(dep.addonId)) {
                if (found.containsKey(dep.addonId)) {
                    found += dep.addonId to found.getValue(dep.addonId)
                            .withRequiredBy(DependencyRequirement(root, DependencyType.fromApi(dep.type)))
                } else {
                    val version = selectVersion(dep.addonId)
                    version?.let {
                        found += it.projectId to RequiredDependency(it,
                            setOf(DependencyRequirement(root, DependencyType.fromApi(dep.type))))
                        collectRequired(it, root, offset + size / 2.0 + index.toDouble() * segmentSize, segmentSize)
                    }
                }
            }
        }

        updateProgress(offset + size, 1.0)
        updateMessage("Collected dependencies: ${file.orNull()?.fileName}")
    }

    /**
     * Selects a file for the given project.
     *
     * May be null if the project could not be found or does not contain a file compatible with the given minecraft
     * version range.
     */
    private fun selectVersion(projectId: Long): Either<AddonVersionSelectionError, AddonId> {
        return when {
            selectedVersions.containsKey(projectId) -> Right(
                SimpleAddonId(projectId, selectedVersions.getValue(projectId)))
            else -> modListUtils.latestByMinecraftVersion(projectId, lowMinecraftVersion, highMinecraftVersion)
                    .map { SimpleAddonId(projectId, it.id) }
        }
    }
}

/**
 * The result of a [CollectDependenciesTask].
 *
 * @param required a list of all the required dependencies of the root addons successfully collected.
 * @param optional a list of all the optional dependencies of the root addons successfully collected.
 * @param unresolved a list of all the dependencies that were encountered but couldn't be resolved.
 */
data class CollectDependenciesResult(val required: Collection<RequiredDependency>,
                                     val optional: Collection<OptionalDependency>,
                                     val unresolved: Map<Long, AddonVersionSelectionError>)

/**
 * Gives info about a required dependency.
 */
data class RequiredDependency(val addonId: AddonId, val requiredBy: Set<AddonId>) : AddonId {
    override val projectId: Long
        get() = addonId.projectId
    override val fileId: Long
        get() = addonId.fileId

    fun withRequiredBy(requirement: AddonId): RequiredDependency {
        return if (requiredBy.contains(requirement)) this
        else RequiredDependency(addonId, requiredBy + requirement)
    }
}

/**
 * Gives info about an optional dependency.
 */
data class OptionalDependency(val addonId: AddonId, val dependencies: Set<AddonId>, val requiredBy: Set<AddonId>) :
        AddonId {
    override val projectId: Long
        get() = addonId.projectId
    override val fileId: Long
        get() = addonId.fileId

    fun withRequiredBy(requirement: AddonId): OptionalDependency {
        return if (requiredBy.contains(requirement)) this
        else OptionalDependency(addonId, dependencies, requiredBy + requirement)
    }
}

/**
 * The type of a dependencies relation.
 */
enum class DependencyType {
    REQUIRED,
    OPTIONAL,
    EMBEDDED;

    companion object {
        fun fromApi(ordinal: Long): DependencyType {
            if (ordinal !in 1..3) {
                throw IllegalArgumentException("Invalid dependency type: $ordinal")
            }
            return enumValues<DependencyType>()[(ordinal - 1).toInt()]
        }
    }
}
