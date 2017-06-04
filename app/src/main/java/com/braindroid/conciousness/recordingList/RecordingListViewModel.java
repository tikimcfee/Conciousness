package com.braindroid.conciousness.recordingList;

public class RecordingListViewModel {

    private final CharSequence recordingTitle;
    private final CharSequence tagInformation;

    private final boolean isPlayable;

    public RecordingListViewModel(CharSequence recordingTitle, boolean isPlayable, CharSequence tagInformation) {
        this.recordingTitle = recordingTitle;
        this.isPlayable = isPlayable;
        this.tagInformation = tagInformation;
    }

    public CharSequence getTagInformation() {
        return tagInformation != null ? tagInformation : "(no info)";
    }

    public CharSequence getRecordingTitle() {
        return recordingTitle != null ? recordingTitle : "(no title)";
    }

    public boolean isPlayable() {
        return isPlayable;
    }
}
