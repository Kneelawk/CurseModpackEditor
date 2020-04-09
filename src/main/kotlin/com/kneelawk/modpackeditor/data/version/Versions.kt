package com.kneelawk.modpackeditor.data.version

import java.util.regex.Pattern

data class MinecraftVersion(val major: Int, val minor: Int, val patch: Int) : Comparable<MinecraftVersion> {
    override fun compareTo(other: MinecraftVersion): Int {
        var c = major.compareTo(other.major)
        if (c != 0) {
            return c
        }
        c = minor.compareTo(other.minor)
        if (c != 0) {
            return c
        }
        return patch.compareTo(other.patch)
    }

    companion object {
        fun parse(version: String): MinecraftVersion {
            val split = version.split(".")
            if (split.size < 2 || split.size > 3) {
                throw IllegalArgumentException("$version is not in valid minecraft version format")
            }
            try {
                return when(split.size) {
                    2 -> MinecraftVersion(split[0].toInt(), split[1].toInt(), 0)
                    else -> MinecraftVersion(split[0].toInt(), split[1].toInt(), split[2].toInt())
                }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("$version is not in valid minecraft version format")
            }
        }
    }
}

data class ForgeVersion(val major: Int, val minor: Int, val patch: Int, val build: Int) : Comparable<ForgeVersion> {
    override fun compareTo(other: ForgeVersion): Int {
        var c = major.compareTo(other.major)
        if (c != 0) {
            return c
        }
        c = minor.compareTo(other.minor)
        if (c != 0) {
            return c
        }
        c = patch.compareTo(other.patch)
        if (c != 0) {
            return c
        }
        return build.compareTo(other.build)
    }

    companion object {
        private val pattern = Pattern.compile(".*?(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)(\\.(?<build>\\d+))?")

        fun parse(version: String): ForgeVersion {
            val match = pattern.matcher(version)
            if (!match.matches()) {
                throw IllegalArgumentException("$version is not in valid forge version format")
            }

            val major = match.group("major")!!.toInt()
            val minor = match.group("minor")!!.toInt()
            val patch = match.group("patch")!!.toInt()
            val build = match.group("build")?.toInt()

            return ForgeVersion(major, minor, patch, build ?: 0)
        }
    }
}
