package com.braindroid.conciousness.recordingList;

import android.media.MediaPlayer;

import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;

import java.io.IOException;

import timber.log.Timber;

public class ManagedMediaPlayer
        implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener {

    final MediaPlayer mediaPlayer;

    private PersistedRecording currentRecording;
    private int currentRecordingDuration;

//    private final Handler backgroundHandler;

    public ManagedMediaPlayer() {
//        HandlerThread handlerThread = new HandlerThread("ManagedMediaPlayer");
//        handlerThread.start();
//        backgroundHandler = new Handler(handlerThread.getLooper());

        this.mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
    }

    public void initializeWithRecording(PersistedRecording recording) {
        currentRecording = recording;
        currentRecordingDuration = 0;

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(recording.getAudioInputStream().getFD());
        } catch (IOException e) {
            Timber.e(e, "Failed to initialize recording - %s", recording);
            e.printStackTrace();
            return;
        }

        mediaPlayer.prepareAsync();
    }

    public int getCurrentRecordingDuration() {
        return currentRecordingDuration;
    }

    public int getSeekPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void pause() {
        if(!mediaPlayer.isPlaying()) {
            Timber.w("Player already paused or stopped.");
            return;
        }

        mediaPlayer.pause();
    }

    public void play() {
        if(mediaPlayer.isPlaying()) {
            Timber.w("Player already playing.");
            return;
        }

        mediaPlayer.start();
    }

    public void reset() {
        initializeWithRecording(currentRecording);
    }

    public void seek(int millis) {
        mediaPlayer.seekTo(millis);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Timber.e("MediaPlayer error : %s / %s : %s", what, extra, mediaPlayer);
        mediaPlayer.reset();
        currentRecordingDuration = 0;
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        currentRecordingDuration = mp.getDuration();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
}
