package com.braindroid.conciousness;

import android.content.Context;
import android.media.MediaRecorder;

import com.braindroid.nervecenter.domainRecordingTools.DeviceRecorder;
import com.braindroid.nervecenter.recordingTools.Recording;
import com.braindroid.nervecenter.recordingTools.RecordingProvider;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import timber.log.Timber;

public class BasicRecordingProvider implements RecordingProvider {

    private final DeviceRecorder deviceRecorder;
    private final Context context;

    private Recording currentRecording;

    private File sourcePath = null;
    private File currentFile = null;

    private int currentFileNumber = 0;

    public BasicRecordingProvider(MediaRecorder mediaRecorder, Context context) {
        deviceRecorder = new DeviceRecorder(mediaRecorder, this);
        this.context = context;
    }

    public boolean initialize() {
        return deviceRecorder.initialize();
    }

    //region File handling
    private String getNextFileName() {
        return String.format(Locale.ENGLISH, "audio_recording_%s", ++currentFileNumber);
    }

    private String currentFileName() {
        return String.format(Locale.ENGLISH, "audio_recording_%s", currentFileNumber);
    }

    private String currentFileNameAbsolute(File sourcePath, String fileName) {
        return sourcePath.getAbsolutePath() + File.separator + fileName;
    }

    private File getFile(boolean create) {
        if(sourcePath == null) {
            Timber.v("sourcePath is null; attempting to acquire a new root.");
            sourcePath = context.getFilesDir();
            if(sourcePath == null || !sourcePath.canWrite() || !sourcePath.isDirectory()) {
                Timber.e("Source path is not writable; returning in-memory file. Path : %s", sourcePath);
                return new File("memoryOnly");
            }
        }

        if(create || currentFile == null) {
            currentFile = new File(currentFileNameAbsolute(sourcePath, getNextFileName()));
        }

        return currentFile;
    }
    //endregion

    //region Provider Implementation
    @Override
    public Recording acquireNewRecording() {
        return currentRecording = vendRecording(getFile(true).getPath());
    }

    @Override
    public Recording getCurrentRecording() {
        if(currentRecording == null) {
            currentRecording = acquireNewRecording();
        }
        return currentRecording;
    }

    private static Recording vendRecording(final String path) {
        return new Recording() {
            private final File file = new File(path + ".aac");

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
            public String toString() {
                return "$classname{" +
                        "file=" + file +
                        '}';
            }
        };
    }
    //endregion
}
