package com.braindroid.conciousness.mainApplication

import android.app.Application
import io.realm.Realm
import timber.log.Timber

class ConsciousnessApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Plant timber debug tree
        Timber.plant(Timber.DebugTree())

        // Initialize, FOR THE REALM!
        Realm.init(this)
    }
}