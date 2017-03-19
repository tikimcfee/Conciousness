package com.braindroid.nervecenter.playbackTools;

import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingFileHandler;

public class ManagedMediaPlayerPool {

    private final PersistedRecordingFileHandler fileHandler;

    private static class PooledMediaPlayer extends ManagedMediaPlayer {

        private ManagedMediaPlayerPool playerPool;

        private PooledMediaPlayer(PersistedRecordingFileHandler fileHandler, ManagedMediaPlayerPool playerPool) {
            super(fileHandler);
            this.playerPool = playerPool;
        }

        @Override
        public void play() {
            if(playerPool.currentMediaPlayer != null) {
                playerPool.currentMediaPlayer.pause();
            }
            playerPool.currentMediaPlayer = this;
            super.play();
        }
    }

    private ManagedMediaPlayer currentMediaPlayer = null;

    public ManagedMediaPlayerPool(PersistedRecordingFileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    public ManagedMediaPlayer createManagedMediaPlayer() {
        return new PooledMediaPlayer(fileHandler, this);
    }

    public void stopPlayback() {
        if(currentMediaPlayer != null) {
            currentMediaPlayer.pause();
        }
    }
}
