package com.braindroid.nervecenter.recordingTools.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Map;

@JsonObject
public class PersistedRecordingUserMeta implements Recording.UserMeta{

    @JsonField
    private Map<String, String> baseProperties;

    @Override
    public Map<String, String> getBaseProperties() {
        return this.baseProperties;
    }

    @Override
    public void setBaseProperties(Map<String, String> baseProperties) {
        this.baseProperties = baseProperties;
    }
}
