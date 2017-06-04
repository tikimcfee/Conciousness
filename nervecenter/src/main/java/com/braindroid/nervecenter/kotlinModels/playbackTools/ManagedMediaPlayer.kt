package com.braindroid.nervecenter.kotlinModels.playbackTools

import android.media.MediaPlayer
import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording
import com.braindroid.nervecenter.kotlinModels.utils.OnDiskRecordingFileHandler
import timber.log.Timber
import java.io.IOException

open class ManagedMediaPlayer(
        val recordingFileHandler: OnDiskRecordingFileHandler,
        val mediaPlayer: MediaPlayer = MediaPlayer()) {

    // Current media player state
    var currentRecordingDuration: Int = 0
        private set

    var isPrepared: Boolean = false
        private set

    lateinit private var currentRecording: OnDiskRecording

    // Settable external listeners for media player events
    var externalPreparedListener: MediaPlayer.OnPreparedListener? = null
    var externalSeekCompleteListener: MediaPlayer.OnSeekCompleteListener? = null
    var externalOnCompletionListener: MediaPlayer.OnCompletionListener? = null

    init {
        mediaPlayer.setOnBufferingUpdateListener { mp, percent ->  }
        mediaPlayer.setOnCompletionListener { mp -> externalOnCompletionListener?.onCompletion(mp) }
        mediaPlayer.setOnErrorListener { mp, what, extra -> false }
        mediaPlayer.setOnInfoListener { mp, what, extra -> false }
        mediaPlayer.setOnPreparedListener {
            currentRecordingDuration = it.duration
            isPrepared = true

            externalPreparedListener?.onPrepared(it)
        }
        mediaPlayer.setOnSeekCompleteListener { externalSeekCompleteListener?.onSeekComplete(it) }
    }


    fun initializeWithRecording(onDiskRecording: OnDiskRecording) {
        currentRecording = onDiskRecording
        currentRecordingDuration = 0
        isPrepared = false

        if(mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer.reset()

        recordingFileHandler.recordingAudioFilePath(onDiskRecording)?.let {
            try {
                mediaPlayer.setDataSource(it)
            } catch (e: IOException) {
                Timber.e(e, "Failed to initialize recording=$onDiskRecording")
                return
            }
            mediaPlayer.prepareAsync()
        } ?: Timber.v("Could not initialize recording=$onDiskRecording")
    }

    fun seekPosition(): Int = mediaPlayer.currentPosition

    fun pause() = mediaPlayer.pause()

    open fun play() = mediaPlayer.start()

    fun isPlaying(): Boolean = isPrepared && mediaPlayer.isPlaying

    fun seek(millis: Int) {
        mediaPlayer.seekTo(millis)
        if(!mediaPlayer.isPlaying) mediaPlayer.start()
    }

    fun reset() = initializeWithRecording(currentRecording)
}