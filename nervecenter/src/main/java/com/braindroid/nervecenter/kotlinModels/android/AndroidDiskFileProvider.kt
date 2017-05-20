package com.braindroid.nervecenter.kotlinModels.android

import android.content.Context
import com.braindroid.nervecenter.kotlinModels.utils.DiskBasedFileProvider
import java.io.File

class AndroidDiskFileProvider(val context: Context): DiskBasedFileProvider {

    override fun absoluteBasePath(): String? {
        return context.cacheDir?.absolutePath + File.pathSeparator
    }

    override fun recordingPathForFilename(filename: String): String? {
        return absoluteBasePath()?.let { it + defaultRecordingStoreDirectoryName + filename }
    }

    override fun recordingFileForFilename(filename: String): File? {
        return recordingPathForFilename(filename)?.let { return File(it) }
    }

    override fun modelPathForFilename(filename: String): String? {
        return absoluteBasePath()?.let { it + defaultModelStoreDirectoryName + filename }
    }

    override fun modelFileForFilename(filename: String): File? {
        return modelPathForFilename(filename)?.let { return File(it) }
    }
}