package org.example.project.core.utils

import coil3.PlatformContext
import okio.Path
import okio.Path.Companion.toOkioPath

actual fun getCacheDirectory(context: PlatformContext): Path {
    return context.cacheDir.toOkioPath()
}