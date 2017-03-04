package com.braindroid.conciousness.recordingList;

import com.braindroid.nervecenter.recordingTools.Recording;
import com.braindroid.nervecenter.recordingTools.RecordingTag;

import java.io.File;
import java.util.List;

public class RecordingTransformer {

    public static RecordingListViewModel toViewModel(Recording recording) {
        File file = recording.asFile();

        CharSequence body;
        List<RecordingTag> recordingTags = recording.getRecordingUserMeta().getTags();
        if(recordingTags.size() > 0) {

            StringBuilder builder = new StringBuilder();
            for (int i = 0, s = recordingTags.size(); i < s; i++) {
                RecordingTag tag = recordingTags.get(i);
                builder.append(tag.getForDisplay());
                if(i >= 0 && i <= s - 2) {
                    builder.append(", ");
                }
            }

            body = builder.toString();
        } else {
            body = "";
        }

        return new RecordingListViewModel(
                file.getName(),
                file.exists() && file.length() > 0,
                body
        );
    }
}
