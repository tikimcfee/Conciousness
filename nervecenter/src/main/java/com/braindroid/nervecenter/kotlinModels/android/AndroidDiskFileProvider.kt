package com.braindroid.nervecenter.kotlinModels.android

import android.content.Context
import java.io.File

class AndroidDiskFileProvider(val context: Context) {

    val modelStoreDirectoryName: String
        get() = "recordingData"

    val recordingStoreDirectoryName: String
        get() = "recordings"


    fun absoluteBasePath(): String? {
        return context.filesDir?.absolutePath + File.separator
    }

    fun ensureDirectory(path: String) : File {
        val directory = File(path)
        if(directory.exists() && directory.isDirectory) return directory
        directory.mkdirs()
        return directory
    }

    // Audio recording files
    private fun rootRecordingPath(): String? = absoluteBasePath()?.let {
        it + recordingStoreDirectoryName + File.separator
    }

    fun rootRecordingDirectory(): File? = rootRecordingPath()?.let {
        ensureDirectory(it)
    }

    fun recordingPathForFilename(filename: String): String? = rootRecordingPath()?.let {
        it + filename
    }

    fun recordingFileForFilename(filename: String): File? = recordingPathForFilename(filename)?.let {
        // Ensures directory is created
        rootRecordingDirectory()

        val ret = File(it)
        ret.createNewFile()
        ret
    }

    // Data model files
    private fun rootModelPath(): String? = absoluteBasePath()?.let {
        it + modelStoreDirectoryName + File.separator
    }

    fun rootModelDirectory(): File? = rootModelPath()?.let {
        ensureDirectory(it)
    }

    fun modelPathForFilename(filename: String): String? = rootModelPath()?.let {
        it + filename
    }

    fun modelFileForFilename(filename: String): File? = modelPathForFilename(filename)?.let {
        // Ensures directory is created
        rootModelDirectory()

        val ret = File(it)
        ret.createNewFile()
        ret
    }
}