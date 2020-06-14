package com.kneelawk.modpackeditor.curse

import com.kneelawk.modpackeditor.data.version.MinecraftVersion

/**
 * Describes an error that occurred when selecting a mod version.
 */
sealed class AddonVersionSelectionError {
    object UnknownAddon : AddonVersionSelectionError()
    object NoFiles : AddonVersionSelectionError()
    class DifferentMinecraftVersion(versions: Collection<MinecraftVersion>) : AddonVersionSelectionError()
}