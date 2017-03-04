package com.braindroid.conciousness;

import android.content.Context;

import com.braindroid.nervecenter.recordingTools.Recording;
import com.braindroid.nervecenter.recordingTools.RecordingMetaWriter;
import com.braindroid.nervecenter.recordingTools.RecordingProvider;
import com.braindroid.nervecenter.recordingTools.RecordingUserMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class BasicRecordingProvider implements RecordingProvider {

    private final Context context;

    private Recording currentRecording;

    private File sourcePath = null;
    private File currentFile = null;

    private int currentFileNumber = 0;

    public BasicRecordingProvider(Context context) {
        this.context = context;
    }

    //region File handling
    public boolean hasFiles() {
        ensureSourcePath();
        String[] list = sourcePath.list();
        return list != null && list.length > 0;
    }

    public List<Recording> attemptRestore() {
        ensureSourcePath();
        File[] list = sourcePath.listFiles();
        if(list == null || list.length == 0) {
            Timber.w("No files restored; %s", list);
            return Collections.emptyList();
        }

        ArrayList<Recording> restoredRecordings = new ArrayList<>();
        for(File file : list) {
            if(file.length() > 0 && file.getName().endsWith(".aac")) {
                Recording recording = RecordingFactory.create(file.getAbsolutePath());

                RecordingUserMeta userMeta = RecordingMetaWriter.readRecording(context, recording);
                recording.setRecordingUserMeta(userMeta);

                restoredRecordings.add(recording);
            } else {
                if(file.length() == 0 && !file.delete()) {
                    Timber.e("Failed to delete file with 0 length path=%s; replacing with unplayable audio file", file.getAbsolutePath());
                    restoredRecordings.add(RecordingFactory.unplayable());
                }
            }
        }

        currentFileNumber = restoredRecordings.size();
        currentRecording = restoredRecordings.get(currentFileNumber - 1);
        return restoredRecordings;
    }

    private boolean ensureSourcePath() {
        if(sourcePath == null || !sourcePath.exists()) {
            Timber.v("sourcePath is null; attempting to acquire a new root.");
            sourcePath = context.getFilesDir();
            if(sourcePath == null || !sourcePath.canWrite() || !sourcePath.isDirectory()) {
                Timber.e("Source path is not writable; %s", sourcePath);
                return false;
            }

            File rootDirectory = new File(getRootDirectoryName(sourcePath));
            if(!rootDirectory.exists()) {
                if(!rootDirectory.mkdirs()) {
                    Timber.e("Could not create root directory; Path : %s", rootDirectory);
                    return false;
                }
            }

            sourcePath = rootDirectory;
        }

        return true;
    }

    private String getNextFileName() {
        return String.format(Locale.ENGLISH, "audio_recording_%s", ++currentFileNumber);
    }

    private String getRootDirectoryName(File sourcePath) {
        return sourcePath.getAbsolutePath() + File.separator + "recordingSession";
    }

    private String currentFileNameAbsolute(File sourcePath, String fileName) {
        return sourcePath.getAbsolutePath() + File.separator + fileName;
    }

    private File getFile(boolean create) {


        if(create || currentFile == null) {
            currentFile = new File(currentFileNameAbsolute(sourcePath, getNextFileName()));
        }

        return currentFile;
    }
    //endregion

    //region Provider Implementation
    @Override
    public Recording acquireNewRecording() {
        return currentRecording = RecordingFactory.create(getFile(true).getPath());
    }

    @Override
    public Recording getCurrentRecording() {
        if(currentRecording == null) {
            currentRecording = acquireNewRecording();
        }
        return currentRecording;
    }
    //endregion
}
