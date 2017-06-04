package com.braindroid.nervecenter.kotlinModels.playbackTools

import com.braindroid.nervecenter.kotlinModels.utils.OnDiskRecordingFileHandler

class ManagedMediaPlayerPool(val diskRecordingFileHandler: OnDiskRecordingFileHandler) {

    class PooledPlayer(
            diskRecordingFileHandler: OnDiskRecordingFileHandler,
            val playerPool: ManagedMediaPlayerPool
    ) : ManagedMediaPlayer(diskRecordingFileHandler) {
        override fun play() {
            playerPool.currentPlayer?.let {
                if (it.isPlaying()) it.pause()
            }
            playerPool.currentPlayer = this
            super.play()
        }
    }

    var currentPlayer: ManagedMediaPlayer? = null

    fun fromPool() = PooledPlayer(diskRecordingFileHandler, this)
}