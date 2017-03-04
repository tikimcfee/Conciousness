package com.braindroid.nervecenter.recordingTools;

public interface RecordingProvider {

    Recording acquireNewRecording();

    Recording getCurrentRecording();

}
