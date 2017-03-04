package com.braindroid.conciousness.recordingList;

import com.braindroid.nervecenter.recordingTools.Recording;

import java.io.File;

public class RecordingTransformer {

    public static RecordingListViewModel toViewModel(Recording recording) {
        File file = recording.asFile();
        return new RecordingListViewModel(
                file.getName(),
                file.exists() && file.length() > 0
        );
    }
}
