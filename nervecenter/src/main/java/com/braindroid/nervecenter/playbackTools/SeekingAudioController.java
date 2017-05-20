package com.braindroid.nervecenter.playbackTools;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.TextView;

import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording;

import java.util.Locale;

public class SeekingAudioController
        implements
        SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnCompletionListener {


    private SeekBar seekBar;
    private TextView currentTimeTextView;
    private TextView remainingTimeTextView;

    private com.braindroid.nervecenter.kotlinModels.playbackTools.ManagedMediaPlayer managedMediaPlayer;
    private boolean isPlaying = false;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Runnable updateUi = new Runnable() {
        @Override
        public void run() {
            if(isPlaying) {
                setRemainingTimeText();
                setCurrentTimeText();
                seekBar.setProgress(managedMediaPlayer.seekPosition());
                uiHandler.postDelayed(this, 50);
            } else {
                uiHandler.removeCallbacksAndMessages(null);
            }
        }
    };

    public SeekingAudioController(SeekBar seekBar,
                                  TextView currentTimeTextView,
                                  TextView remainingTimeTextView,
                                  com.braindroid.nervecenter.kotlinModels.playbackTools.ManagedMediaPlayer managedMediaPlayer) {
        this.seekBar = seekBar;
        this.currentTimeTextView = currentTimeTextView;
        this.remainingTimeTextView = remainingTimeTextView;
        this.managedMediaPlayer = managedMediaPlayer;

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);

        managedMediaPlayer.setExternalPreparedListener(this);
        managedMediaPlayer.setExternalSeekCompleteListener(this);
        managedMediaPlayer.setExternalOnCompletionListener(this);
    }

    public void setRecording(OnDiskRecording recording) {
        managedMediaPlayer.initializeWithRecording(recording);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void play() {
        if(isPlaying) {
            return;
        }

        isPlaying = true;
        uiHandler.post(updateUi);
        managedMediaPlayer.play();
    }

    public void pause() {
        if(!isPlaying) {
            return;
        }

        isPlaying = false;
        managedMediaPlayer.pause();
    }

    //region SeekBar Callbacks
    private float getSeekBarProgressPercent(SeekBar seekBar, int progress) {
        return 1.0f * progress / (seekBar.getMax());
    }

    private int getSeekFromProgress(SeekBar seekBar, int progress) {
        return Math.round(
                managedMediaPlayer.getCurrentRecordingDuration()
                        * getSeekBarProgressPercent(seekBar, progress)
        );
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            managedMediaPlayer.seek(getSeekFromProgress(seekBar, progress));
        }

        setCurrentTimeText();
        setRemainingTimeText();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    //endregion

    //region Media Recording Callbacks
    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlaying = false;
        setCurrentTimeText();
        setRemainingTimeText();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setCurrentTimeText();
        setRemainingTimeText();
        seekBar.setMax(managedMediaPlayer.getCurrentRecordingDuration());
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        seekBar.setProgress(mp.getCurrentPosition());
    }
    //endregion

    private void setCurrentTimeText() {
        final int seekMillis = managedMediaPlayer.seekPosition();
        final int totalSeekSeconds = seekMillis / 1000;
        final int trailingMillis = seekMillis % 1000;
        final int remainingSeekMinutes = totalSeekSeconds / 60;
        final int remainingSeconds = totalSeekSeconds % 60;

        StringBuilder builder = new StringBuilder();
        builder.append(String.format(Locale.ENGLISH, "%02d:%02d:%03d", remainingSeekMinutes, remainingSeconds, trailingMillis));
        currentTimeTextView.setText(builder.toString());
    }

    private void setRemainingTimeText() {
        final int remainingMillis = managedMediaPlayer.getCurrentRecordingDuration() - managedMediaPlayer.seekPosition();
        final int totalSeekSeconds = remainingMillis / 1000;
        final int trailingMillis = remainingMillis % 1000;
        final int remainingSeekMinutes = totalSeekSeconds / 60;
        final int remainingSeconds = totalSeekSeconds % 60;

        StringBuilder builder = new StringBuilder();
        builder.append(String.format(Locale.ENGLISH, "%02d:%02d:%03d", remainingSeekMinutes, remainingSeconds, trailingMillis));
        remainingTimeTextView.setText(builder.toString());
    }
}
