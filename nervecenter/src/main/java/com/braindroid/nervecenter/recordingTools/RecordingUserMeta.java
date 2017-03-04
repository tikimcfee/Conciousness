package com.braindroid.nervecenter.recordingTools;

import java.util.ArrayList;
import java.util.Collection;

public class RecordingUserMeta {

    private static class DefaultTag implements RecordingTag {
        @Override
        public String forDisplay() {
            return "An Emotion";
        }

        @Override
        public String forStorage() {
            return "default-emotion-id";
        }
    }

    private final Collection<? super RecordingTag> tags;

    public RecordingUserMeta() {
        this.tags = new ArrayList<>();
    }

    public RecordingUserMeta(Collection<? super RecordingTag> tags) {
        this.tags = tags;
    }

    public Collection<? super RecordingTag> getTags() {
        return tags;
    }

    public void setTags(Collection<? extends RecordingTag> newTags) {
        tags.clear();
        tags.addAll(newTags);
    }

}
