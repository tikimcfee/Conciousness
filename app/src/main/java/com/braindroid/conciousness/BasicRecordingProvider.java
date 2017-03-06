package com.braindroid.conciousness;

import android.content.Context;

import com.braindroid.nervecenter.playbackTools.PersistingRecordingMetaWriter;
import com.braindroid.nervecenter.recordingTools.RecordingProvider;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingFileHandler;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingModelHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingFileHandler.AUDIO_DIRECTORY_PATH_ROOT;

public class BasicRecordingProvider implements RecordingProvider {

    private final Context context;
    private final PersistedRecordingFileHandler fileHandler = new PersistedRecordingFileHandler();

    private PersistedRecording currentRecording;

    private File sourcePath = null;
    private File currentFile = null;

    private int currentFileNumber = 0;

    public BasicRecordingProvider(Context context) {
        this.context = context;
    }

    //region File handling
    public boolean hasFiles() {
        return fileHandler.hasModels(context);
    }

    public List<PersistedRecording> attemptRestore() {
        Timber.d("attemptRestore() called");
        ensureSourcePath();
        File[] list = sourcePath.listFiles();
        if(list == null || list.length == 0) {
            Timber.w("No files restored; %s", (Object)list);
            return Collections.emptyList();
        }

        ArrayList<PersistedRecording> restoredRecordings = new ArrayList<>();
        for(File file : list) {
            if(file.length() > 0 && file.getName().endsWith(".aac")) {
                final PersistedRecording expectedRecording = RecordingFactory.create(context, file.getName());
                final PersistedRecording inflatedRecording = PersistedRecordingModelHandler.readPersistedRecordingModel(context, expectedRecording);

                if(inflatedRecording == null) {
                    Timber.v("No on-disk user model found for [%s]. returning as in-memory", expectedRecording, expectedRecording.getSystemMeta());
                    restoredRecordings.add(expectedRecording);
                } else {
                    restoredRecordings.add(inflatedRecording);
                }
            } else {
                if(file.length() == 0 && !file.delete()) {
                    Timber.e("Failed to delete file with 0 length path=%s; replacing with unplayable audio file", file.getAbsolutePath());
                    PersistedRecording persistedRecording = RecordingFactory.unplayable(context);
                    restoredRecordings.add(persistedRecording);
                }
            }
        }

        currentFileNumber = restoredRecordings.size();
        if(currentFileNumber > 0) {
            currentRecording = restoredRecordings.get(currentFileNumber - 1);
        }
        return restoredRecordings;
    }

    private boolean ensureSourcePath() {
        sourcePath = fileHandler.ensureDirectoryExists(context, AUDIO_DIRECTORY_PATH_ROOT);
        if(sourcePath.list() != null) {
            currentFileNumber = sourcePath.list().length;
        }
        return fileHandler.valid(sourcePath);
    }

    private String getNextFileName() {
        return String.format(Locale.ENGLISH, "audio_recording_%s", ++currentFileNumber);
    }

    private String currentFileNameAbsolute(File sourcePath, String fileName) {
        return sourcePath.getAbsolutePath() + File.separator + fileName;
    }

    private File getFile(boolean create) {
        ensureSourcePath();
        if(create || currentFile == null) {
            currentFile = new File(currentFileNameAbsolute(sourcePath, getNextFileName()));
        }
        return currentFile;
    }
    //endregion

    //region Provider Implementation
    @Override
    public PersistedRecording acquireNewRecording() {
        PersistedRecording recording = RecordingFactory.create(context, getFile(true).getName());
        if(context instanceof PersistingRecordingMetaWriter) {
            ((PersistingRecordingMetaWriter) context).persistRecording(recording);
        }

        return currentRecording = recording;
    }

    @Override
    public PersistedRecording getCurrentRecording() {
        if(currentRecording == null) {
            currentRecording = acquireNewRecording();
        }
        return currentRecording;
    }
    //endregion
}
