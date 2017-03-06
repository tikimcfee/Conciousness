package com.braindroid.nervecenter.recordingTools.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Collections;
import java.util.Map;

@JsonObject
public class PersistedRecordingTag implements Recording.Tag {

    @JsonField
    private String display;

    @JsonField
    private String identifier;

    @JsonField
    private Map<String, String> tagProperties;

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Map<String, String> getTagProperties() {
        return tagProperties == null ? Collections.<String, String>emptyMap() : tagProperties;
    }

    @Override
    public void setTagProperties(Map<String, String> tagProperties) {
        if(tagProperties == null) {
            this.tagProperties = Collections.emptyMap();
        } else {
            this.tagProperties = tagProperties;
        }
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
