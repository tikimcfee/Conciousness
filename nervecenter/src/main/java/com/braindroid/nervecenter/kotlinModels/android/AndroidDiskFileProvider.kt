package com.braindroid.nervecenter.kotlinModels.android

import android.content.Context
import com.braindroid.nervecenter.kotlinModels.utils.DiskBasedFileProvider
import java.io.File

class AndroidDiskFileProvider(val context: Context): DiskBasedFileProvider {

    override fun absoluteBasePath(): String? {
        return context.filesDir?.absolutePath + File.separator
    }

    fun ensureDirectory(path: String) : File {
        val directory = File(path)
        if(directory.exists() && directory.isDirectory) return directory
        directory.mkdirs()
        return directory
    }

    override fun recordingPathForFilename(filename: String): String? {
        return absoluteBasePath()?.let {
            val path = it + defaultRecordingStoreDirectoryName + File.separator
            ensureDirectory(path)
            path + filename
        }
    }

    override fun recordingFileForFilename(filename: String): File? {
        return recordingPathForFilename(filename)?.let {
            return File(it).let {
                it.createNewFile()
                it
            }
        }
    }

    override fun modelPathForFilename(filename: String): String? {
        return absoluteBasePath()?.let {
            val path = it + defaultModelStoreDirectoryName + File.separator
            ensureDirectory(path)
            path + filename
        }
    }

    override fun modelFileForFilename(filename: String): File? {
        return modelPathForFilename(filename)?.let {
            return File(it).let {
                it.createNewFile()
                it
            }
        }
    }
}