package com.braindroid.nervecenter.recordingTools;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class RecordingTag {

    @JsonField
    private String forDisplay;

    @JsonField
    private String forStorage;

    public String getForDisplay() {
        return forDisplay;
    }

    public void setForDisplay(String forDisplay) {
        this.forDisplay = forDisplay;
    }

    public String getForStorage() {
        return forStorage;
    }

    public void setForStorage(String forStorage) {
        this.forStorage = forStorage;
    }
}
