package com.braindroid.nervecenter.kotlinModels.data

import com.braindroid.nervecenter.kotlinModels.utils.OnDiskRecordingFileHandler
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber

class RecordingStore(val fileHandler: OnDiskRecordingFileHandler) {

    // 'constants'
    private val NAME = "realm_store_recording_models"

    // instance vars
    val realmConfig: RealmConfiguration

    /**
     * @param recording     lookup a recording by its primary id
     * @return              the recording for the given primary id
     */
    fun Realm.recordingById(recording: OnDiskRecording): OnDiskRecording? =
            this.where(OnDiskRecording::class.java)
                    .equalTo("recordingId", recording.recordingId)
                    .findFirst()
    // ------------------------------------------------------------------

    init {
        realmConfig = RealmConfiguration.Builder()
                .directory(fileHandler.fileProvider.rootModelDirectory())
                .name(NAME)
                .build()
    }

    /**
     * @return List of all recordings as *managed objects*
     */
    fun getAllRecordings(): List<OnDiskRecording> =
            threadLocal().where(OnDiskRecording::class.java).findAll().filter {
                fileHandler.recordingExists(it)
            }

    /**
     * @return List of UNMANAGED realm objects
     */
    fun getAllRecordingsUnmanaged(): List<OnDiskRecording> {
        val realm = threadLocal()
        return realm.where(OnDiskRecording::class.java).findAll().filter {
            fileHandler.recordingExists(it)
        }.map {
            realm.copyFromRealm(it)
        }
    }



    fun clearDeadRecordings() {
        val realm = threadLocal()
        val realmResults = realm.where(OnDiskRecording::class.java).findAll()
        realm.executeTransaction {
            realmResults.filter {
                !fileHandler.recordingExists(it)
            }.forEach {
                Timber.w("Deleting model without backing recording - $it")
                it.deleteFromRealm()
            }
        }
    }

    /**
     * @param newRecording  Copies a recording, managed or otherwise, to realm
     */
    fun addRecording(newRecording: OnDiskRecording) =
            threadLocal().executeTransaction {
                it.copyToRealmOrUpdate(newRecording)
            }

    /**
     * @param recording     target recording to update
     * @param newTags       the _full_ set of tags to update to; any old tags are CLEARED
     */
    fun setTags(recording: OnDiskRecording, newTags: List<RecordingTag>) =
            threadLocal().executeTransaction {
                it.recordingById(recording)?.let {
                    it.tags.clear()
                    it.tags.addAll(newTags)
                } ?: Timber.e("Could not find recording to sets tags on; recording=$recording")
            }


    // Helpers
    fun threadLocal(): Realm = Realm.getInstance(realmConfig)

}