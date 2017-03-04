package com.braindroid.conciousness;

import com.braindroid.nervecenter.recordingTools.Recording;

import java.io.File;

public class RecordingFactory {

    private RecordingFactory() {}

    public static Recording create(final String path) {
        final String cleanPath = path.endsWith(".aac") ? path : path + ".aac";

        return new Recording(cleanPath);
    }

    public static Recording unplayable() {
        return new Recording() {
            @Override
            public File asFile() {
                return new File("inMemory_unplayable");
            }

            @Override
            public String absolutePath() {
                return "UNPLAYABLE_AUDIO";
            }

            @Override
            public String metaName() {
                return absolutePath() + "_meta.json";
            }

            @Override
            public boolean isPlayable() {
                return false;
            }
        };
    }
}
