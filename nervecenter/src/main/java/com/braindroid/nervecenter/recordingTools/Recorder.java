package com.braindroid.nervecenter.recordingTools;

public interface Recorder {

    /**
     * Recording Controls
     */

    void startRecording();

    void stopRecording();

    void pauseRecording();


    /**
     * State access
     */

    RecordingProvider getRecordingProvider();

    void setRecording(Recording audioRecording);

}
