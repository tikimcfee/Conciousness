package com.braindroid.conciousness.recordingList;

import com.braindroid.nervecenter.recordingTools.models.Recording;

import java.util.List;

public class RecordingTransformer {

    public static RecordingListViewModel toViewModel(Recording recording) {
        CharSequence body = null;
        List<Recording.Tag> recordingTags = recording.getTags();
        if(recordingTags.size() > 0) {

            StringBuilder builder = new StringBuilder();
            for (int i = 0, s = recordingTags.size(); i < s; i++) {
                Recording.Tag tag = recordingTags.get(i);
                builder.append(tag.getDisplay());
                if(i >= 0 && i <= s - 2) {
                    builder.append(", ");
                }
            }

            body = builder.toString();
        }

        return new RecordingListViewModel(
                recording.getName(),
                true,
                body
        );
    }
}
