package com.braindroid.nervecenter.kotlinModels.utils

import com.braindroid.nervecenter.kotlinModels.android.AndroidDiskFileProvider
import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

class OnDiskRecordingFileHandler(val fileProvider: AndroidDiskFileProvider) {

    fun OnDiskRecording.asAudioFile(): File? {
        return fileProvider.recordingFileForFilename(this.recordingId)
    }

    fun OnDiskRecording.asDataFile(): File? {
        return fileProvider.modelFileForFilename(this.recordingId)
    }

    fun recordingExists(recording: OnDiskRecording): Boolean = recording.asAudioFile()?.exists() ?: false

    fun recordingAudioFilePath(recording: OnDiskRecording) : String? = recording.asAudioFile()?.absolutePath

    fun createAudioInputStream(recording: OnDiskRecording): FileInputStream? = recording.asAudioFile()?.let { inputStream(it) }

    fun createAudioOutStream(recording: OnDiskRecording): FileOutputStream? = recording.asAudioFile()?.let { outputStream(it) }

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