package com.kneelawk.modpackeditor.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import javafx.scene.image.Image
import java.time.Duration

object ResourceCaches {
    val imageCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(Duration.ofMinutes(10))
            .build(CacheLoader.from { key: String? -> Image(key, 64.0, 64.0, true, true, true) })
    val smallImageCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterAccess(Duration.ofMinutes(10))
            .build(CacheLoader.from { key: String? -> Image(key, 32.0, 32.0, true, true, true) })
}
