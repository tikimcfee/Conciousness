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
    fun rootRecordingPath(): String? = absoluteBasePath()?.let {
        it + recordingStoreDirectoryName + File.separator
    }

    fun rootRecordingDirectory(): File? = rootRecordingPath()?.let {
        ensureDirectory(it)
    }

    fun recordingPathForFilename(filename: String): String? = rootRecordingPath()?.let {
        it + filename
    }

    fun recordingFileForFilename(filename: String): File? = recordingPathForFilename(filename)?.let {
        File(it)
    }

    // Data model files
    fun rootModelPath(): String? = absoluteBasePath()?.let {
        it + modelStoreDirectoryName + File.separator
    }

    fun rootModelDirectory(): File? = rootModelPath()?.let {
        ensureDirectory(it)
    }

    fun modelPathForFilename(filename: String): String? = rootModelPath()?.let {
        it + filename
    }

    fun modelFileForFilename(filename: String): File? = modelPathForFilename(filename)?.let {
        File(it)
    }
}