package com.braindroid.nervecenter.kotlinModels.data

data class OnDiskRecording(
        var systemMeta: SystemMeta,
        val userMeta: RecordingMeta,
        var tags: List<RecordingTag>
)