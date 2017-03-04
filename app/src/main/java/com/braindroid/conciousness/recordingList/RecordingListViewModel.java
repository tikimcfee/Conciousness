package com.braindroid.conciousness.recordingList;

public class RecordingListViewModel {

    private final CharSequence recordingTitle;

    public RecordingListViewModel(CharSequence recordingTitle) {
        this.recordingTitle = recordingTitle;
    }

    public CharSequence getRecordingTitle() {
        return recordingTitle != null ? recordingTitle : "(no title)";
    }
}
