package com.braindroid.nervecenter.recordingTools;

import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;

public interface Recorder {

    /**
     * PersistedRecording Controls
     */

    void startRecording();

    void stopRecording();

    void pauseRecording();


    /**
     * State access
     */

    RecordingProvider getRecordingProvider();

    void setRecording(PersistedRecording audioRecording);

}
