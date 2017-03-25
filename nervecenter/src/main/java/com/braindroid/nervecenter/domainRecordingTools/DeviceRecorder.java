package com.braindroid.nervecenter.domainRecordingTools;

import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.braindroid.nervecenter.domainRecordingTools.recordingSource.AudioRecordingHandler;
import com.braindroid.nervecenter.domainRecordingTools.recordingSource.AudioSampleReceiver;
import com.braindroid.nervecenter.playbackTools.PersistingRecordingMetaWriter;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingFileHandler;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingModelHandler;
import com.braindroid.nervecenter.utils.SampleIOHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;


public class DeviceRecorder
        implements MediaRecorder.OnInfoListener,
        MediaRecorder.OnErrorListener,
        PersistingRecordingMetaWriter,
        AudioSampleReceiver {

    private final AudioRecordingHandler audioRecordingHandler;
    private final BasicRecordingProvider recordingProvider;
    private final LinkedList<PersistedRecording> completedRecordings = new LinkedList<>();

    private final PersistedRecordingFileHandler fileHandler;
    private final PersistedRecordingModelHandler modelHandler;

    private PersistedRecording currentRecording = null;
    private boolean isRecording = false;

    public DeviceRecorder(AudioRecordingHandler audioRecordingHandler,
                          BasicRecordingProvider recordingProvider,
                          PersistedRecordingFileHandler fileHandler,
                          PersistedRecordingModelHandler modelHandler) {
        this.recordingProvider = recordingProvider;
        this.fileHandler = fileHandler;
        this.modelHandler = modelHandler;
        this.audioRecordingHandler = audioRecordingHandler;
        audioRecordingHandler.setAudioSampleReceiver(this);
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
        if(currentRecording == null) {
            Timber.w("No current recording set in initialize; reacquiring from provider");
            currentRecording = recordingProvider.getCurrentRecording();
        }

        boolean successful = true;
        return successful;
    }

    private void reinitialize() {
        initialize();
    }

    public PersistedRecording startRecording() {
        Timber.v("PersistedRecording start - %s", currentRecording);
        try {
//            mediaRecorder.start();
            audioRecordingHandler.startRecording();
            isRecording = true;
        } catch (IllegalStateException e) {
            Timber.e(e, "Could not start media recorder; nothing is being written to [%s].", currentRecording);
        }
        return currentRecording;
    }

    //region Sample receives


    private SampleIOHandler sampleIOHandler;

    @Override
    public void onNewAudioSample(short[] sample) {
        if(sampleIOHandler == null) {
            sampleIOHandler = new SampleIOHandler(
                    fileHandler.ensureAudioFileOutputStream(currentRecording)
            );
        }
        sampleIOHandler.onNewAudioSample(sample);
    }

    @Override
    public void onSamplingStopped() {
        if(sampleIOHandler != null) {
            sampleIOHandler.kill();
            sampleIOHandler = null;
        }
    }

    //endregion

    public PersistedRecording stopRecording() {
        Timber.v("PersistedRecording stop - %s", currentRecording);
        try {
//            mediaRecorder.stop();
            audioRecordingHandler.stopRecording();
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
