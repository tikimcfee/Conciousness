package com.braindroid.nervecenter.kotlinModels.data

import com.braindroid.nervecenter.recordingTools.models.PersistedRecording

data class OnDiskRecording(
        var systemMeta: SystemMeta = SystemMeta("default_id", "new_on_disk_recording"),
        var userMeta: RecordingMeta = RecordingMeta(mutableMapOf()),
        var tags: List<RecordingTag> = mutableListOf()
)

fun OnDiskRecording.fromPersistedRecording(persistedRecording: PersistedRecording) : OnDiskRecording {
    return OnDiskRecording(
            SystemMeta(
                    persistedRecording.systemMeta.targetRecordingIdentifier,
                    persistedRecording.name
            ),
            RecordingMeta(
                    mutableMapOf<String, Any>().let {
                        for ((key, value) in persistedRecording.userMeta.baseProperties) {
                            it.put(key, value)
                        }
                        it
                    }
            ),
            let {
                var tags = ArrayList<RecordingTag>()
                persistedRecording.tagsImpl.mapTo(tags) { tag ->
                    RecordingTag(
                            tag.identifier,
                            tag.display,
                            RecordingMeta(
                                    mutableMapOf<String, Any>().let {
                                        for ((key, value) in tag.tagProperties) {
                                            it.put(key, value)
                                        }
                                        it
                                    }
                            )
                    )
                }
                tags
            }
    )
}