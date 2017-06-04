package com.braindroid.nervecenter.kotlinModels.data

import io.realm.RealmObject

open class SystemMeta(
        var recordingName: String = ""
): RealmObject()
