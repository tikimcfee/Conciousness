package com.braindroid.nervecenter.recordingTools.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

@JsonObject
public class PersistedRecording implements Recording {

    //region JSON Definition
    @JsonField
    private String name;

    @JsonField(name="systemMeta")
    private PersistedRecordingSystemMeta systemMetaImpl;

    @JsonField(name="userMeta")
    private PersistedRecordingUserMeta userMetaImpl;

    @JsonField(name="tags")
    private List<PersistedRecordingTag> tagsImpl;
    //endregion

    @JsonIgnore
    private FileInputStream audioInputStream;

    @JsonIgnore
    private FileOutputStream audioOutputStream;

    @JsonIgnore
    private FileInputStream modelInputStream;

    @JsonIgnore
    private FileOutputStream modelOutputStream;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public FileInputStream getAudioInputStream() {
        return audioInputStream;
    }

    public void setAudioInputStream(FileInputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
    }

    public FileOutputStream getAudioOutputStream() {
        return audioOutputStream;
    }

    public void setAudioOutputStream(FileOutputStream audioOutputStream) {
        this.audioOutputStream = audioOutputStream;
    }

    public FileInputStream getModelInputStream() {
        return modelInputStream;
    }

    public void setModelInputStream(FileInputStream modelInputStream) {
        this.modelInputStream = modelInputStream;
    }

    public FileOutputStream getModelOutputStream() {
        return modelOutputStream;
    }

    public void setModelOutputStream(FileOutputStream modelOutputStream) {
        this.modelOutputStream = modelOutputStream;
    }

    @Override
    public SystemMeta getSystemMeta() {
        return systemMetaImpl;
    }

    @Override
    public void setSystemMeta(SystemMeta systemMeta) {
        if(systemMeta instanceof PersistedRecordingSystemMeta) {
            this.systemMetaImpl = ((PersistedRecordingSystemMeta) systemMeta);
        } else {
            Timber.e("[%s] is not of correct type!", systemMeta);
        }
    }

    public PersistedRecordingSystemMeta getSystemMetaImpl() {
        return systemMetaImpl;
    }

    public void setSystemMetaImpl(PersistedRecordingSystemMeta systemMetaImpl) {
        this.systemMetaImpl = systemMetaImpl;
    }

    public PersistedRecordingUserMeta getUserMetaImpl() {
        return userMetaImpl;
    }

    public void setUserMetaImpl(PersistedRecordingUserMeta userMetaImpl) {
        this.userMetaImpl = userMetaImpl;
    }

    public List<PersistedRecordingTag> getTagsImpl() {
        return tagsImpl;
    }

    public void setTagsImpl(List<PersistedRecordingTag> tagsImpl) {
        this.tagsImpl = tagsImpl;
    }

    @Override
    public UserMeta getUserMeta() {
        return userMetaImpl;
    }

    @Override
    public void setUserMeta(UserMeta userMeta) {
        if(userMeta instanceof PersistedRecordingUserMeta) {
            this.userMetaImpl = ((PersistedRecordingUserMeta) userMeta);
        } else {
            Timber.e("[%s] is not of correct type", userMeta);
        }
    }

    @Override
    public List<Tag> getTags() {
        return Collections.<Tag>unmodifiableList(tagsImpl);
    }

    @Override
    public void setTags(List<Tag> tags) {
        if(this.tagsImpl == null) {
            this.tagsImpl = new ArrayList<>();
        }

        if(tags == null) {
            return;
        }

        for (int i = 0, s = tags.size(); i < s; i++) {
            Tag tag = tags.get(i);
            if(tag instanceof PersistedRecordingTag) {
                this.tagsImpl.add(((PersistedRecordingTag) tag));
            }
        }
    }

    @Override
    public String toString() {
        return "PersistedRecording{" +
                "name='" + name + '\'' +
                ", systemMetaImpl=" + systemMetaImpl +
                ", userMetaImpl=" + userMetaImpl +
                ", tagsImpl=" + tagsImpl +
                ", audioInputStream=" + audioInputStream +
                ", audioOutputStream=" + audioOutputStream +
                ", modelInputStream=" + modelInputStream +
                ", modelOutputStream=" + modelOutputStream +
                '}';
    }
}
