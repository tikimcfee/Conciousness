package com.braindroid.nervecenter.kotlinModels.data

import io.realm.RealmObject

open class RecordingTag (
        var identifier: String = "recording_tag_no_init",
        var displayName: String = "#badtags",
        var userMeta: RecordingMeta = RecordingMeta()
): RealmObject()