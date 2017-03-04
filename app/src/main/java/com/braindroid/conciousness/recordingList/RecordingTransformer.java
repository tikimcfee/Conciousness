package com.braindroid.conciousness.recordingList;

import com.braindroid.nervecenter.recordingTools.Recording;

public class RecordingTransformer {

    public static RecordingListViewModel toViewModel(Recording recording) {
        return new RecordingListViewModel(
                recording.asFile().getName()
        );
    }
}
