package com.braindroid.nervecenter.playbackTools;

import android.media.MediaPlayer;

import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingFileHandler;

import java.io.FileInputStream;
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

    private final MediaPlayer mediaPlayer;

    private PersistedRecording currentRecording;
    private final PersistedRecordingFileHandler fileHandler;
    private int currentRecordingDuration;
    private volatile boolean isPrepared = false;

    private MediaPlayer.OnPreparedListener onPreparedListener = null;
    private MediaPlayer.OnSeekCompleteListener onSeekCompleteListener = null;
    private MediaPlayer.OnCompletionListener onCompletionListener = null;

//    private final Handler backgroundHandler;

    public ManagedMediaPlayer(PersistedRecordingFileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
    }

    public boolean isReady() {
        return currentRecording != null && isPrepared;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        this.onSeekCompleteListener = onSeekCompleteListener;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }

    public void initializeWithRecording(PersistedRecording recording) {
        currentRecording = recording;
        currentRecordingDuration = 0;
        isPrepared = false;

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();

        FileInputStream inputStream = fileHandler.ensureAudioFileInputStream(recording);
        if(inputStream == null) {
            Timber.e("Could not initialize - no input stream for %s", recording);
            return;
        }

        try {
            mediaPlayer.setDataSource(inputStream.getFD());
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
            Timber.d("Player already paused or stopped.");
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
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    //region MediaRecording Callbacks
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(onCompletionListener != null) {
            onCompletionListener.onCompletion(mp);
        }
    }


    final int MAX_RETRY = 1;
    int retries = 0;
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Timber.e("MediaPlayer error : %s / %s : %s", what, extra, mediaPlayer);
        if(retries < MAX_RETRY) {
            retries++;
            mediaPlayer.reset();
            reset();
        } else {

        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        currentRecordingDuration = mp.getDuration();
        isPrepared = true;

        if(onPreparedListener != null) {
            onPreparedListener.onPrepared(mp);
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if(onSeekCompleteListener != null) {
            onSeekCompleteListener.onSeekComplete(mp);
        }
    }
    //endregion
}
