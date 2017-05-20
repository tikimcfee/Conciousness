package com.braindroid.nervecenter.recordingTools.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording;
import com.braindroid.nervecenter.kotlinModels.data.RecordingTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static PersistedRecording fromOnDiskRecording(OnDiskRecording onDiskRecording) {
        PersistedRecording persistedRecording = new PersistedRecording();
        persistedRecording.name = onDiskRecording.getSystemMeta().getRecordingName();

        // Copy system meta
        PersistedRecordingSystemMeta meta = new PersistedRecordingSystemMeta();
            meta.setTargetModelIdentifier(onDiskRecording.getSystemMeta().getRecordingId());
            meta.setTargetRecordingIdentifier(onDiskRecording.getSystemMeta().getRecordingId());
        persistedRecording.setSystemMeta(meta);

        // Copy user meta
        PersistedRecordingUserMeta userMeta = new PersistedRecordingUserMeta();
            Map<String, String> copied = new HashMap<String, String>();
            for(String key : onDiskRecording.getUserMeta().getProperties().keySet()) {
                String val = null;
                 try {
                     val = (String)onDiskRecording.getUserMeta().getProperties().get(key);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }

                copied.put(key, val);
            }
            userMeta.setBaseProperties(copied);
        persistedRecording.setUserMeta(userMeta);

        // Copy tags
        List<PersistedRecordingTag> copiedTags = new ArrayList<>();
            for(RecordingTag tag : onDiskRecording.getTags()) {
                PersistedRecordingTag copy =  new PersistedRecordingTag();
                copy.setDisplay(tag.getDisplayName());
                copy.setIdentifier(tag.getIdentifier());
                copy.setTagProperties(new HashMap<String, String>());
                copiedTags.add(copy);
            }
        persistedRecording.setTagsImpl(copiedTags);

        return  persistedRecording;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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
        } else {
            tagsImpl.clear();
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
                '}';
    }
}
