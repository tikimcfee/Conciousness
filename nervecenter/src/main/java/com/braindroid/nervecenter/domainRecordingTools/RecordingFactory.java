package com.braindroid.nervecenter.domainRecordingTools;

import android.content.Context;

import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecordingSystemMeta;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecordingUserMeta;
import com.braindroid.nervecenter.recordingTools.models.Recording;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class RecordingFactory {

    private RecordingFactory() {}

    public static PersistedRecording create(final Context context, final String requestedName) {
        Timber.v("create() called with: context = [" + context + "], requestedName = [" + requestedName + "]");

        final String cleanName = requestedName.endsWith(".aac") ? requestedName : requestedName + ".aac";

        PersistedRecording toReturn = new PersistedRecording();
        toReturn.setName("User recording : " + cleanName);

        PersistedRecordingSystemMeta meta = new PersistedRecordingSystemMeta();
        meta.setTargetModelIdentifier(cleanName);
        meta.setTargetRecordingIdentifier(cleanName);
        toReturn.setSystemMeta(meta);

        PersistedRecordingUserMeta userMeta = new PersistedRecordingUserMeta();
        userMeta.setBaseProperties(new HashMap<String, String>());
        toReturn.setUserMeta(userMeta);
        toReturn.setTags(new ArrayList<Recording.Tag>());

        return toReturn;
    }

    private static final String INVALID_RECORDING_PATH_FILE_NAME = "temporary_invalid_recording.aac";
    public static PersistedRecording unplayable(final Context context) {
        return create(context, INVALID_RECORDING_PATH_FILE_NAME);
    }
}
