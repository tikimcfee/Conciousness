package com.braindroid.conciousness.recordingList;

import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;

public interface RecordingListClickListener {

    void onRecordingItemClicked(PersistedRecording recording, int position);

    void onRecordingItemLongClicked(PersistedRecording recording, int position);

}
