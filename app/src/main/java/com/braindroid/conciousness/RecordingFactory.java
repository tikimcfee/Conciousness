package com.braindroid.conciousness;

import com.braindroid.nervecenter.recordingTools.Recording;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

public class RecordingFactory {

    private RecordingFactory() {}

    public static Recording create(final String path) {
        return new Recording() {

            private final File file = new File(path + ".aac");
            private String id;

            @Override
            public File asFile() {
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

            @Override
            public String identifier() {
                if(id == null) {
                    id = file.getAbsolutePath();
                }
                return id;
            }

            @Override
            public String toString() {
                return "$classname{" +
                        "file=" + file +
                        '}';
            }
        };
    }
}
