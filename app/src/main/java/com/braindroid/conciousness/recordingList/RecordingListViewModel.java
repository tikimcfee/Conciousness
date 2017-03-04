package com.braindroid.conciousness.recordingList;

public class RecordingListViewModel {

    private final CharSequence recordingTitle;
    private final CharSequence topSupplementalText;

    private final boolean isPlayable;

    public RecordingListViewModel(CharSequence recordingTitle, boolean isPlayable, CharSequence topSupplementalText) {
        this.recordingTitle = recordingTitle;
        this.isPlayable = isPlayable;
        this.topSupplementalText = topSupplementalText;
    }

    public CharSequence getTopSupplementalText() {
        return topSupplementalText != null ? topSupplementalText : "(no info)";
    }

    public CharSequence getRecordingTitle() {
        return recordingTitle != null ? recordingTitle : "(no title)";
    }

    public boolean isPlayable() {
        return isPlayable;
    }
}
