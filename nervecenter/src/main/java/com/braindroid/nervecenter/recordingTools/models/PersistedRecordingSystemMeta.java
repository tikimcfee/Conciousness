package com.braindroid.nervecenter.recordingTools.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class PersistedRecordingSystemMeta implements Recording.SystemMeta {

    @JsonField
    private String targetRecordingIdentifier;

    @JsonField
    private String targetModelIdentifier;

    @Override
    public String getTargetRecordingIdentifier() {
        return targetRecordingIdentifier;
    }

    @Override
    public void setTargetRecordingIdentifier(String identifier) {
        this.targetRecordingIdentifier = identifier;
    }

    @Override
    public String getTargetModelIdentifier() {
        return targetModelIdentifier;
    }

    @Override
    public void setTargetModelIdentifier(String identifier) {
        this.targetModelIdentifier = identifier;
    }

    @Override
    public String toString() {
        return "PersistedRecordingSystemMeta{" +
                "targetRecordingIdentifier='" + targetRecordingIdentifier + '\'' +
                ", targetModelIdentifier='" + targetModelIdentifier + '\'' +
                '}';
    }
}
