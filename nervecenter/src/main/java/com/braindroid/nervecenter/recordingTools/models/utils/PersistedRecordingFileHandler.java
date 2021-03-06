package com.braindroid.nervecenter.recordingTools.models.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

public class PersistedRecordingFileHandler {

    public static String MODEL_DIRECTORY_PATH_ROOT = "recordingData";
    public static String AUDIO_DIRECTORY_PATH_ROOT = "recordings";

    private final Context context;

    public PersistedRecordingFileHandler(Context context) {
        this.context = context;
    }

    public boolean recordingFileExists(PersistedRecording recording) {
        return new File(getAudioFilePath(recording)).exists();
    }

    public boolean modelFileExists(PersistedRecording recording) {
        return new File(getModelFilePath(recording)).exists();
    }
    
    public boolean hasModels() {
        File rootModelDirectory = ensureDirectoryExists(AUDIO_DIRECTORY_PATH_ROOT);
        if(!valid(rootModelDirectory)) {
            return false;
        }

        String[] list = rootModelDirectory.list();
        return list != null && list.length > 0;
    }

    public boolean valid(final File file) {
        return file != null && file.exists();
    }

    private @Nullable FileInputStream ensureInputStream(final String targetFilePath) {
        Timber.w("Creating input stream [%s]", targetFilePath);
        File inputFile = create(targetFilePath);
        try {
            return new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            Timber.e(e, "Could not find file [%s]", targetFilePath);
        }

        return null;
    }

    private @Nullable FileOutputStream ensureOutputStream(final String targetFilePath) {
        Timber.w("Creating output stream [%s]", targetFilePath);
        File outputFile = create(targetFilePath);
        try {
            return new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            Timber.e(e, "Could not find file [%s]", targetFilePath);
        }

        return null;
    }

    public @Nullable FileInputStream ensureAudioFileInputStream(PersistedRecording recording) {
        Timber.v("ensureAudioFileInputStream() called with: context = [" + context + "], recording = [" + recording + "]");
        if(recording == null || context == null) {
            return null;
        }
        String inputStreamPath = getAudioFilePath(recording);
        return ensureInputStream(inputStreamPath);
    }

    public @Nullable FileOutputStream ensureAudioFileOutputStream(PersistedRecording recording) {
        Timber.v("ensureAudioFileOutputStream() called with: context = [" + context + "], recording = [" + recording + "]");
        if(recording == null || context == null) {
            return null;
        }
        String outputStreamName = getAudioFilePath(recording);
        return ensureOutputStream(outputStreamName);
    }

    public @Nullable FileInputStream ensureModelFileInputStream(PersistedRecording recording) {
        Timber.v("ensureModelFileInputStream() called with: context = [" + context + "], recording = [" + recording + "]");
        if(recording == null || context == null) {
            return null;
        }
        String inputStreamName = getModelFilePath(recording);
        return ensureInputStream(inputStreamName);
    }

    public @Nullable FileOutputStream ensureModelFileOutputStream(PersistedRecording recording) {
        Timber.v("ensureModelFileOutputStream() called with: context = [" + context + "], recording = [" + recording + "]");
        if(recording == null || context == null) {
            return null;
        }
        String outputStreamName = getModelFilePath(recording);
        return ensureOutputStream(outputStreamName);
    }

    public String getModelFilePath(PersistedRecording recording) {
        ensureDirectoryExists(MODEL_DIRECTORY_PATH_ROOT);
        return context.getFilesDir() + File.separator + MODEL_DIRECTORY_PATH_ROOT + File.separator
                + recording.getSystemMeta().getTargetModelIdentifier();
    }

    public String getAudioFilePath(PersistedRecording recording) {
        ensureDirectoryExists(AUDIO_DIRECTORY_PATH_ROOT);
        return context.getFilesDir() + File.separator + AUDIO_DIRECTORY_PATH_ROOT + File.separator
                + recording.getSystemMeta().getTargetRecordingIdentifier();
    }

    public File ensureDirectoryExists(String pathName) {
        File metaDirectory = context.getFilesDir();
        String metaPath = metaDirectory.getAbsolutePath();
        String expectedMetaPath = metaPath + File.separator + pathName;
        File expectedModelFile = new File(expectedMetaPath);
        if(!expectedModelFile.exists()) {
            if(!expectedModelFile.mkdirs()) {
                Timber.e("Could not create filepath for [%s]", expectedMetaPath);
            }
        }
        return expectedModelFile;
    }

    private File create(String filePath) {
        Timber.v("Vend file - %s", filePath);
        File file = new File(filePath);
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
}
