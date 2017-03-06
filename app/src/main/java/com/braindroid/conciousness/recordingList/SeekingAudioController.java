package com.braindroid.conciousness.recordingList;

import android.widget.SeekBar;
import android.widget.TextView;

public class SeekingAudioController
        implements SeekBar.OnSeekBarChangeListener {


    private SeekBar seekBar;
    private TextView currentTimeTextView;
    private TextView remainingTimeTextView;

    private ManagedMediaPlayer mediaPlayer;

    public SeekingAudioController(SeekBar seekBar,
                                  TextView currentTimeTextView,
                                  TextView remainingTimeTextView,
                                  ManagedMediaPlayer managedMediaPlayer) {
        this.seekBar = seekBar;
        this.currentTimeTextView = currentTimeTextView;
        this.remainingTimeTextView = remainingTimeTextView;
        this.mediaPlayer = managedMediaPlayer;

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);
    }

    public void resetToStart() {
        mediaPlayer.reset();
    }

    public void play() {
        mediaPlayer.play();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    //region SeekBar Callbacks
    private float getSeekBarProgressPercent(SeekBar seekBar, int progress) {
        return 1.0f * progress / (seekBar.getMax());
    }

    private int getSeekFromProgress(SeekBar seekBar, int progress) {
        return Math.round(
                mediaPlayer.getCurrentRecordingDuration()
                        * getSeekBarProgressPercent(seekBar, progress)
        );
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            mediaPlayer.seek(getSeekFromProgress(seekBar, progress));
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

    private void setCurrentTimeText() {

    }

    private void setRemainingTimeText() {

    }
}
