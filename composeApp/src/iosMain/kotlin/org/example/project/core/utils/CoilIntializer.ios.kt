package org.example.project.core.utils

import coil3.PlatformContext
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual fun getCacheDirectory(context: PlatformContext): Path {
    // 1. Query the iOS file system for the Caches directory
    val paths = NSSearchPathForDirectoriesInDomains(
        directory = NSCachesDirectory,
        domainMask = NSUserDomainMask,
        expandTilde = true
    )

    // 2. Grab the first result (iOS always returns the path at index 0)
    val cacheDirectory = paths.first() as String

    // 3. Convert the String to an Okio Path
    return cacheDirectory.toPath()
}