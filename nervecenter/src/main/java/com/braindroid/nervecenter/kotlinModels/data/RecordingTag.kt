package com.braindroid.nervecenter.kotlinModels.data

import com.braindroid.nervecenter.recordingTools.models.Recording

data class RecordingTag (
        val identifier: String,
        var displayName: String,
        var userMeta: com.braindroid.nervecenter.recordingTools.models.Recording.UserMeta?
)