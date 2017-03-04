package com.braindroid.nervecenter.playbackTools;

import com.braindroid.nervecenter.recordingTools.Recording;
import com.braindroid.nervecenter.recordingTools.RecordingUserMeta;

public interface RecordingWriter {

    void writeRecordingMeta(Recording recording, RecordingUserMeta recordingUserMeta);

}
