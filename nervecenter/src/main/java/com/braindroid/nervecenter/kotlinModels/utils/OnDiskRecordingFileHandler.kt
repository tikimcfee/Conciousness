package com.braindroid.nervecenter.kotlinModels.utils

import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

class OnDiskRecordingFileHandler(val fileProvider: DiskBasedFileProvider) {

    var currentRecording: OnDiskRecording? = null

    fun OnDiskRecording.asAudioFile(): File? {
        return fileProvider.recordingFileForFilename(this.systemMeta.recordingId)
    }

    fun OnDiskRecording.asDataFile(): File? {
        return fileProvider.modelFileForFilename(this.systemMeta.recordingId)
    }

    fun recordingExists(): Boolean = currentRecording?.asAudioFile()?.exists() ?: false

    fun recordingAudioFilePath() : String? {
        return currentRecording?.asAudioFile()?.absolutePath
    }

    fun createAudioInputStream(): FileInputStream? {
        return currentRecording?.asAudioFile()?.let { inputStream(it) }
    }

    fun createAudioOutStream(): FileOutputStream? {
        return currentRecording?.asAudioFile()?.let { outputStream(it) }
    }

    fun outputStream(file: File): FileOutputStream? {
        try {
            return FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            Timber.v(e, "[$file] did not produce output stream")
        }
        return null
    }

    fun inputStream(file: File): FileInputStream? {
        try {
            return FileInputStream(file)
        } catch (e: FileNotFoundException) {
            Timber.v(e, "[$file] did not produce input stream")
        }
        return null
    }
}