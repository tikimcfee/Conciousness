package com.braindroid.nervecenter.playbackTools.playbackSource;

public interface PlaybackListener {
    void onProgress(int progress);
    void onCompletion();
}
