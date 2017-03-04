package com.braindroid.nervecenter.recordingTools;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

public class Recording {

    public String filePath;

    private RecordingUserMeta recordingUserMeta;

    private File file;

    public Recording(String filePath) {
        this.filePath = filePath;
        ensureFile();
    }

    public Recording() {}

    public RecordingUserMeta getRecordingUserMeta() {
        if(recordingUserMeta == null) {
            recordingUserMeta = new RecordingUserMeta();
        }
        return recordingUserMeta;
    }

    public void setRecordingUserMeta(RecordingUserMeta recordingUserMeta) {
        this.recordingUserMeta = recordingUserMeta;
    }

    private void ensureFile() {
        getFile();
    }

    private File getFile() {
        if(file != null) {
            return file;
        }

        if(filePath != null) {
            file = new File(filePath);
        }

        return file;
    }

    public File asFile() {
        ensureFile();

        Timber.v("Vend recording - %s", this);
        if(!file.exists()) {
            try {
                if(!file.createNewFile()){
                    Timber.e("File does not exist on disk.");
                }
            } catch (IOException e) {
                Timber.e(e, "Failed to create new file - %s", this);
                e.printStackTrace();
            }
        }
        return file;
    }

    public boolean isPlayable() {
        return file.exists() && file.length() > 32;
    }

    public String absolutePath() {
        ensureFile();
        return file.getAbsolutePath();
    }

    public String metaName() {
        ensureFile();
        return file.getName() + "_meta.json";
    }

    public String metaPath() {
        ensureFile();
        return file.getParent() + File.separator + metaName();
    }

    @Override
    public String toString() {
        return "Recording{" +
                "filePath='" + filePath + '\'' +
                ", file=" + file +
                '}';
    }
}
