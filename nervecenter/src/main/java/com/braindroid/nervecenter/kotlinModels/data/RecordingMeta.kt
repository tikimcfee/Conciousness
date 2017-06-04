package com.braindroid.nervecenter.kotlinModels.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RecordingMeta(
        var properties: RealmList<MetaTuple> = RealmList()
): RealmObject()

open class MetaTuple(
        @PrimaryKey var key: String = "meta_key_no_init",
        var tupleValue: Any? = null
): RealmObject()

