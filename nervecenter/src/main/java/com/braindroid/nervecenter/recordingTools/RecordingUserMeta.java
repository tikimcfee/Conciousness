package com.braindroid.nervecenter.recordingTools;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonObject
public class RecordingUserMeta {

    @JsonField
    public List<RecordingTag> tags;

    @JsonField
    public String recordingTitle;

    @JsonField
    public String recordingMetaFileName;

    public List<RecordingTag> getTags() {
        return tags != null ? tags : Collections.<RecordingTag>emptyList();
    }

    public void setTags(List<RecordingTag> newTags) {
        this.tags = newTags;
    }

    public String getRecordingTitle() {
        return recordingTitle;
    }

    public void setRecordingTitle(String recordingTitle) {
        this.recordingTitle = recordingTitle;
    }

    public String getRecordingMetaFileName() {
        return recordingMetaFileName;
    }

    public void setRecordingMetaFileName(String recordingMetaFileName) {
        this.recordingMetaFileName = recordingMetaFileName;
    }

    private Map<String, Object> toMap() {
        HashMap<String, Object> toReturn = new HashMap<>();
        toReturn.put("recordingTitle", recordingTitle);
        toReturn.put("recordingMetaFileName", recordingMetaFileName);
        toReturn.put("recordingTags", tags);
        return toReturn;
    }

    @Override
    public String toString() {
        return "RecordingUserMeta{" +
                "tags=" + tags +
                ", recordingTitle='" + recordingTitle + '\'' +
                ", recordingMetaFileName='" + recordingMetaFileName + '\'' +
                '}';
    }
}
