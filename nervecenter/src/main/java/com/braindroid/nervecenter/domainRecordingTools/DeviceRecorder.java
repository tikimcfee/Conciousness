package com.braindroid.nervecenter.domainRecordingTools;

import android.media.MediaRecorder;

import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.RecordingProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

import static android.media.MediaRecorder.AudioSource.MIC;


public class DeviceRecorder
        implements MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener {

    private final MediaRecorder mediaRecorder;
    private final RecordingProvider recordingProvider;
    private final LinkedList<PersistedRecording> completedRecordings = new LinkedList<>();

    private PersistedRecording currentRecording = null;
    private boolean isRecording = false;

    public DeviceRecorder(MediaRecorder mediaRecorder,
                          RecordingProvider recordingProvider) {
        this.mediaRecorder = mediaRecorder;
        this.recordingProvider = recordingProvider;
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

        try {
            mediaRecorder.setOutputFile(currentRecording.getAudioOutputStream().getFD());
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

    public void setRecordings(List<PersistedRecording> newRecordings) {
        completedRecordings.clear();
        completedRecordings.addAll(newRecordings);
        if(newRecordings.size() > 0) {
            currentRecording = completedRecordings.get(newRecordings.size() - 1);
        }
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
