package com.kneelawk.modpackeditor.cache

import com.google.common.base.Optional
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.kneelawk.modpackeditor.curse.CurseApi
import com.kneelawk.modpackeditor.data.AddonId
import com.kneelawk.modpackeditor.data.SimpleAddonId
import com.kneelawk.modpackeditor.data.curseapi.AddonFileJson
import com.kneelawk.modpackeditor.data.curseapi.AddonJson
import javafx.scene.image.Image
import tornadofx.Controller
import java.time.Duration

class ResourceCaches : Controller() {
    private val curseApi: CurseApi by inject()

    val imageCache: LoadingCache<String, Image> =
            CacheBuilder.newBuilder().maximumSize(200).expireAfterAccess(Duration.ofMinutes(10))
                    .build(CacheLoader.from { key: String? -> Image(key, 64.0, 64.0, true, true, true) })
    val smallImageCache: LoadingCache<String, Image> =
            CacheBuilder.newBuilder().maximumSize(200).expireAfterAccess(Duration.ofMinutes(10))
                    .build(CacheLoader.from { key: String? -> Image(key, 32.0, 32.0, true, true, true) })

    val addonCache: LoadingCache<Long, Optional<AddonJson>> =
            CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(Duration.ofMinutes(2))
                    .build(CacheLoader.from { key: Long? -> Optional.fromNullable(curseApi.getAddon(key!!)) })
    private val fileCache: LoadingCache<SimpleAddonId, Optional<AddonFileJson>> =
            CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(Duration.ofMinutes(2)).build(
                CacheLoader.from { key: SimpleAddonId? ->
                    Optional.fromNullable(curseApi.getAddonFile(key!!))
                })
    val detailsCache: LoadingCache<Long, Optional<String>> =
            CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(Duration.ofMinutes(2))
                    .build(CacheLoader.from { key: Long? -> Optional.fromNullable(curseApi.getAddonDetails(key!!)) })
    private val changelogCache: LoadingCache<SimpleAddonId, Optional<String>> =
            CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(Duration.ofMinutes(2)).build(
                CacheLoader.from { key: SimpleAddonId? ->
                    Optional.fromNullable(curseApi.getAddonFileChangelog(key!!))
                })

    fun getAddonFile(addonId: AddonId): Optional<AddonFileJson> {
        return fileCache[SimpleAddonId(addonId)]
    }

    fun getAddonFileChangelog(addonId: AddonId): Optional<String> {
        return changelogCache[SimpleAddonId(addonId)]
    }
}
