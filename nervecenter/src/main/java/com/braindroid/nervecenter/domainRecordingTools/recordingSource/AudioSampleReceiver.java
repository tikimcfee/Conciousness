package com.braindroid.nervecenter.domainRecordingTools.recordingSource;

public interface AudioSampleReceiver {

    void onNewAudioSample(short[] sample);
    void onSamplingStopped();

}
