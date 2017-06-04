package com.braindroid.nervecenter.kotlinModels.data

import io.realm.RealmList
import io.realm.RealmObject

open class RecordingMeta(
        var properties: RealmList<MetaTuple> = RealmList()
): RealmObject()

