package com.braindroid.conciousness.recordingList;

import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording;
import com.braindroid.nervecenter.kotlinModels.data.RecordingTag;

import java.util.List;

public class RecordingTransformer {

    public static RecordingListViewModel toViewModel(OnDiskRecording recording) {
        CharSequence body = null;
        List<RecordingTag> recordingTags = recording.getTags();
        if(recordingTags.size() > 0) {

            StringBuilder builder = new StringBuilder();
            for (int i = 0, s = recordingTags.size(); i < s; i++) {
                RecordingTag tag = recordingTags.get(i);
                builder.append(tag.getDisplayName());
                if(i >= 0 && i <= s - 2) {
                    builder.append(", ");
                }
            }

            body = builder.toString();
        }

        return new RecordingListViewModel(recording.getSystemMeta().getRecordingName(), true, body);
    }
}
