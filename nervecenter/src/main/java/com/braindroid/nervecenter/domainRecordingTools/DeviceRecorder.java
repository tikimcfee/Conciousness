package com.braindroid.nervecenter.domainRecordingTools;

import android.media.MediaRecorder;
import android.support.annotation.Nullable;

import com.braindroid.nervecenter.playbackTools.PersistingRecordingMetaWriter;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingFileHandler;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingModelHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

import static android.media.MediaRecorder.AudioSource.MIC;


public class DeviceRecorder
        implements MediaRecorder.OnInfoListener,
        MediaRecorder.OnErrorListener,
        PersistingRecordingMetaWriter {

    private final MediaRecorder mediaRecorder;
    private final BasicRecordingProvider recordingProvider;
    private final LinkedList<PersistedRecording> completedRecordings = new LinkedList<>();

    private final PersistedRecordingFileHandler fileHandler;
    private final PersistedRecordingModelHandler modelHandler;

    private PersistedRecording currentRecording = null;
    private boolean isRecording = false;

    public DeviceRecorder(MediaRecorder mediaRecorder,
                          BasicRecordingProvider recordingProvider,
                          PersistedRecordingFileHandler fileHandler,
                          PersistedRecordingModelHandler modelHandler) {
        this.mediaRecorder = mediaRecorder;
        this.recordingProvider = recordingProvider;
        this.fileHandler = fileHandler;
        this.modelHandler = modelHandler;
    }

    public boolean restore() {
        if(recordingProvider.hasFiles()) {
            List<PersistedRecording> recordings = recordingProvider.attemptRestore();
            setRecordings(recordings);
            advance();
            return true;
        }

        Timber.v("No files to restore; initializing.");
        initialize();

        return false;
    }

    public boolean initialize()  {
        mediaRecorder.setOnErrorListener(this);
        mediaRecorder.setOnInfoListener(this);

        mediaRecorder.setAudioSource(MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioEncodingBitRate(96000);

        if(currentRecording == null) {
            Timber.w("No current recording set in initialize; reacquiring from provider");
            currentRecording = recordingProvider.getCurrentRecording();
        }

        FileOutputStream fileOutputStream = fileHandler.ensureAudioFileOutputStream(currentRecording);
        if(fileOutputStream == null) {
            Timber.e("No FOS available for recording; will not set output fil");
            return false;
        }

        try {
            mediaRecorder.setOutputFile(fileOutputStream.getFD());
        } catch (IllegalStateException e) {
            Timber.e(e, "Illegal State in initialize() : %s", currentRecording);
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Timber.e(e, "Bade file in DeviceRecorder : %s", currentRecording);
            e.printStackTrace();
        }

        boolean successful = true;
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException ise) {
            Timber.e("Incorrect state not in correct state to initialize - %s", mediaRecorder.toString());
            successful = false;
        } catch (IOException ioe) {
            successful = false;
        }

        return successful;
    }

    private void reinitialize() {
        mediaRecorder.reset();
        initialize();
    }

    public PersistedRecording startRecording() {
        Timber.v("PersistedRecording start - %s", currentRecording);
        try {
            mediaRecorder.start();
            isRecording = true;
        } catch (IllegalStateException e) {
            Timber.e(e, "Could not start media recorder; nothing is being written to [%s].", currentRecording);
        }
        return currentRecording;
    }

    public PersistedRecording stopRecording() {
        Timber.v("PersistedRecording stop - %s", currentRecording);
        try {
            mediaRecorder.stop();
            completedRecordings.add(currentRecording);
            isRecording = false;
        } catch (IllegalStateException e) {
            Timber.e(e, "Could not stop media recorder; recording may be corrupt or unreadable [%s].", currentRecording);
        }
        return currentRecording;
    }

    public void advance() {
        if(isRecording) {
            Timber.e("Cannot advance, already recording. Stop and reinitialize first.");
            return;
        }
        currentRecording = recordingProvider.acquireNewRecording();
        reinitialize();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public List<PersistedRecording> getAllRecordings() {
        return Collections.unmodifiableList(completedRecordings);
    }

    public @Nullable PersistedRecording getLastRecording() {
        if(completedRecordings.size() == 0) {
            return null;
        }
        return completedRecordings.getLast();
    }

    public void setRecordings(List<PersistedRecording> newRecordings) {
        completedRecordings.clear();
        completedRecordings.addAll(newRecordings);
        if(newRecordings.size() > 0) {
            currentRecording = completedRecordings.get(newRecordings.size() - 1);
        }
    }

    @Override
    public void persistRecording(PersistedRecording persistedRecording) {
        modelHandler.persistRecording(persistedRecording);
    }

    //region Callbacks
    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        Timber.d("onInfo() called with: mr = [" + mr + "], what = [" + what + "], extra = [" + extra + "]");
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        Timber.d("onInfo() called with: mr = [" + mr + "], what = [" + what + "], extra = [" + extra + "]");
    }
    //endregion
}
