package com.braindroid.nervecenter.domainRecordingTools.recordingSource;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.braindroid.nervecenter.utils.LibConstants;

import timber.log.Timber;

import static android.media.AudioFormat.ENCODING_PCM_16BIT;
import static android.media.MediaRecorder.AudioSource.DEFAULT;

public class AudioRecordingHandler implements Runnable {

    private final AudioRecord audioRecord;
    private Thread currentRecordingThread = null;
    private volatile boolean shouldStopRecording = false;

    public final int bufferSize;

    public static int getDefaultBufferSize() {
        return AudioRecord.getMinBufferSize(
                LibConstants.SAMPLE_RATE,
                LibConstants.DEFAULT_AUDIO_IN_CHANNEL,
                ENCODING_PCM_16BIT
        );
    }

    @Nullable
    private AudioSampleReceiver audioSampleReceiver;


//    private final Thread debugThread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            while (!Thread.interrupted()) {
//                SystemClock.sleep(1000);
//            }
//        }
//    });


    public AudioRecordingHandler() {
        this.bufferSize = getDefaultBufferSize();
        this.audioRecord = new AudioRecord(
                DEFAULT,
                LibConstants.SAMPLE_RATE,
                LibConstants.DEFAULT_AUDIO_IN_CHANNEL,
                LibConstants.DEFAULT_AUDIO_ENCODING_FORMAT,
                bufferSize
        );

//        debugThread.start();
    }

    public void startRecording() {
        if(!canStart()) {
            return;
        }

        Timber.v("Recording starting...");
//        recordingSet = false;
        shouldStopRecording = false;

        currentRecordingThread = getNewThread(this);
        currentRecordingThread.start();
    }

    public void stopRecording() {
        if(currentRecordingThread == null) {
            Timber.e("Not recording!");
            return;
        }

        Timber.v("Setting stop flag for recording.");
        shouldStopRecording = true;
    }

    //region Helpers

    private Thread getNewThread(Runnable runnable) {
        return new Thread(runnable);
    }

    private boolean canStart() {
        if(currentRecordingThread != null) {
            Timber.e("Already recording audio!");
            return false;
        }

        if(audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Timber.e("Cannot start recording - state is not initialized! - [%d]", audioRecord.getState());
            return false;
        }

        return true;
    }

    public void setAudioSampleReceiver(AudioSampleReceiver audioSampleReceiver) {
        this.audioSampleReceiver = audioSampleReceiver;
    }

    //endregion

    //region Handle Audio Recording

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);

        long totalBytesRead = 0;
        final int finalBufferSize = bufferSize / 2;
        short[] audioBuffer = new short[finalBufferSize];

        audioRecord.startRecording();

        while (!shouldStopRecording) {
            int numberOfBytesRead = audioRecord.read(audioBuffer, 0, audioBuffer.length);
            totalBytesRead += numberOfBytesRead;

            notifyReceiver(audioBuffer);
        }

        audioRecord.stop();
        audioRecord.release();

        Timber.v("Recording stopped. Setting last recording. Bytes read : %s", totalBytesRead);

//        recordingSet = true;
        audioSampleReceiver.onSamplingStopped();
    }

    private void notifyReceiver(short[] sample) {
        if(audioSampleReceiver == null) {
            return;
        }

        audioSampleReceiver.onNewAudioSample(sample);
    }

    //endregion
}
