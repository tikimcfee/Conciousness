package com.braindroid.nervecenter.kotlinModels.playbackTools

import com.braindroid.nervecenter.kotlinModels.utils.OnDiskRecordingFileHandler

class ManagedMediaPlayerPool(val diskRecordingFileHandler: OnDiskRecordingFileHandler) {

    class PooledPlayer(diskRecordingFileHandler: OnDiskRecordingFileHandler,
                       val playerPool: ManagedMediaPlayerPool)
        : ManagedMediaPlayer(diskRecordingFileHandler)
    {
        override fun play() {
            playerPool.playRequest(this)
        }
    }

    var currentPlayer: ManagedMediaPlayer? = null

    fun fromPool() = PooledPlayer(diskRecordingFileHandler, this)

    private fun playRequest(pooledPlayer: PooledPlayer) {
        currentPlayer?.let {
            if (it.isPlaying()) it.pause()
        }
        pooledPlayer.play()
        currentPlayer = pooledPlayer
    }
}