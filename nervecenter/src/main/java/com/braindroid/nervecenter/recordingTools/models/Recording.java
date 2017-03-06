package com.braindroid.nervecenter.recordingTools.models;

import java.util.List;
import java.util.Map;

public interface Recording {

    /**
     * Meta information required by system
     */
    interface SystemMeta {
        String getTargetRecordingIdentifier();
        void setTargetRecordingIdentifier(String identifier);

        String getTargetModelIdentifier();
        void setTargetModelIdentifier(String identifier);
    }

    /**
     * Arbitrary information set by the user
     */
    interface UserMeta {
        Map<String, String> getBaseProperties();
        void setBaseProperties(Map<String, String> baseProperties);
    }

    /**
     * Arbitrary tags set on a recording.
     */
    interface Tag {

        // A display name for the tag
        String getDisplay();
        void setDisplay(String display);

        // A unique identifier
        String getIdentifier();
        void setIdentifier(String identifier);

        // Arbitrary properties for the tag
        Map<String, String> getTagProperties();
        void setTagProperties(Map<String, String> tagProperties);
    }

    /**
     * Human-readable name of recording, either a default or the one set by the user =
     * @return      name of the recording
     */
    String getName();
    void setName(String name);

//    /**
//     * A recording must supply an input stream for reading / playback .
//     * @return  A valid input stream to be read from. This will opened / closed arbitrarily.
//     */
//    InputStream getAudioInputStream();
//
//    /**
//     * A recording must supply an output stream that targets where audio may be piped to for writing.
//     * @return  A valid output stream to be written to. This will opened / closed arbitrarily.
//     */
//    OutputStream getAudioOutputStream();

    /**
     * Meta information that may be set / retrieved for use by the system only
     * @return  A non-null SystemMeta instance, preferably immutable (cloned)
     */
    SystemMeta getSystemMeta();
    void setSystemMeta(SystemMeta systemMeta);

    /**
     * Any meta information set by the user, accessible at any time
     * @return  A non-null UserMeta instance, preferably immutable
     */
    UserMeta getUserMeta();
    void setUserMeta(UserMeta userMeta);

    /**
     * Get tags that have been assigned to the recording, if any
     * @return A non-null list of tags. May be empty.
     */
    List<Tag> getTags();
    void setTags(List<Tag> tags);

}
