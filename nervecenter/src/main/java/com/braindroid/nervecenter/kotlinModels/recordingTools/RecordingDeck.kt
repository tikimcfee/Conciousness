package com.braindroid.nervecenter.kotlinModels.recordingTools

import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording
import com.braindroid.nervecenter.kotlinModels.data.RecordingStore
import com.braindroid.nervecenter.kotlinModels.data.SystemMeta
import java.util.*

class RecordingDeck(val mediaRecorder: OnDiskMediaRecorder,
                    val recordingStore: RecordingStore) {

    val DEFAULT_RECORDING_NAME = "audio_recording_"
    var currentSessionRecordingNumber: Int = 0

    fun readyNewRecording(): Boolean = mediaRecorder.prepare(createUnmanagedRecording())

    private fun createUnmanagedRecording(): OnDiskRecording = synchronized(this, {
        OnDiskRecording(
                UUID.randomUUID().toString(),
                SystemMeta(DEFAULT_RECORDING_NAME + currentSessionRecordingNumber++)
        )
    })

    fun saveRecording(newRecording: OnDiskRecording) = recordingStore.addRecording(newRecording)

    fun allRecordingsAsUnmanaged(): List<OnDiskRecording> = recordingStore.getAllRecordingsUnmanaged()

    fun allRecordingsAsManaged(): List<OnDiskRecording> = recordingStore.getAllRecordings()

}