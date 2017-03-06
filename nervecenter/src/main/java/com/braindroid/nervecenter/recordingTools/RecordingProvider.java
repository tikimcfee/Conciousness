package com.braindroid.nervecenter.recordingTools;

import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;

public interface RecordingProvider {

    PersistedRecording acquireNewRecording();

    PersistedRecording getCurrentRecording();

}
