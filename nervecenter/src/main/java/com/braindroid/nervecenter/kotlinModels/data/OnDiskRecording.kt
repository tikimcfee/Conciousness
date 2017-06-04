package com.braindroid.nervecenter.kotlinModels.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class OnDiskRecording(
        @PrimaryKey var recordingId: String = "unmanaged_recording",
        var systemMeta: SystemMeta = SystemMeta(),
        var userMeta: RecordingMeta = RecordingMeta(),
        var tags: RealmList<RecordingTag> = RealmList()
): RealmObject()