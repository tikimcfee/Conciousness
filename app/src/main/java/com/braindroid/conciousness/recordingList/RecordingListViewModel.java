package com.braindroid.conciousness.recordingList;

public class RecordingListViewModel {

    private final CharSequence recordingTitle;
    private final boolean isPlayable;

    public RecordingListViewModel(CharSequence recordingTitle, boolean isPlayable) {
        this.recordingTitle = recordingTitle;
        this.isPlayable = isPlayable;
    }

    public CharSequence getRecordingTitle() {
        return recordingTitle != null ? recordingTitle : "(no title)";
    }

    public boolean isPlayable() {
        return isPlayable;
    }
}
