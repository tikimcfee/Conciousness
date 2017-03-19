package com.braindroid.nervecenter.domainRecordingTools;

import com.braindroid.nervecenter.playbackTools.PersistingRecordingMetaWriter;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingModelHandler;

public class PersistedRecordingMetaFileWriter implements PersistingRecordingMetaWriter {

    private final PersistedRecordingModelHandler modelHandler;

    public PersistedRecordingMetaFileWriter(PersistedRecordingModelHandler modelHandler) {
        this.modelHandler = modelHandler;
    }

    @Override
    public void persistRecording(PersistedRecording persistedRecording) {
        modelHandler.persistRecording(persistedRecording);
    }
}
