package com.braindroid.nervecenter.kotlinModels.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MetaTuple(
        @PrimaryKey var key: String = "meta_key_no_init",
        var tupleValue: String = ""
): RealmObject()