package com.braindroid.nervecenter.kotlinModels.playbackTools

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording
import com.braindroid.nervecenter.kotlinModels.utils.OnDiskRecordingFileHandler
import timber.log.Timber
import java.io.IOException
import android.media.AudioTrack
import android.media.AudioManager
import android.os.AsyncTask
import com.braindroid.nervecenter.domainRecordingTools.recordingSource.AudioRecordingHandler
import com.braindroid.nervecenter.playbackTools.playbackSource.PlaybackListener
import com.braindroid.nervecenter.playbackTools.playbackSource.PlaybackThread
import com.braindroid.nervecenter.utils.SampleIOHandler
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.FileInputStream


open class ManagedMediaPlayer(
        val recordingFileHandler: OnDiskRecordingFileHandler
): PlaybackListener {

//    val mediaPlayer: MediaPlayer = MediaPlayer()

    // Current media player state
    var currentRecordingDuration: Int = 0
        private set

    var playing: Boolean = false
        private set

    var isPrepared: Boolean = false
        private set

    lateinit private var currentRecording: OnDiskRecording
    private var playbackThread: PlaybackThread? = null

    private var currentProgress: Int = 0

    // Settable external listeners for media player events
    var externalPreparedListener: MediaPlayer.OnPreparedListener? = null
    var externalSeekCompleteListener: MediaPlayer.OnSeekCompleteListener? = null
    var externalOnCompletionListener: MediaPlayer.OnCompletionListener? = null

    init {
//        mediaPlayer.setOnBufferingUpdateListener { mp, percent ->  }
//        mediaPlayer.setOnCompletionListener { mp -> externalOnCompletionListener?.onCompletion(mp) }
//        mediaPlayer.setOnErrorListener { mp, what, extra -> false }
//        mediaPlayer.setOnInfoListener { mp, what, extra -> false }
//        mediaPlayer.setOnPreparedListener {
//            currentRecordingDuration = it.duration
//            isPrepared = true

//            externalPreparedListener?.onPrepared(it)
//        }
//        mediaPlayer.setOnSeekCompleteListener { externalSeekCompleteListener?.onSeekComplete(it) }
    }

    override fun onProgress(progress: Int) {
        this.currentProgress = progress
    }

    override fun onCompletion() {
        externalOnCompletionListener?.onCompletion(null)
    }

    fun initializeWithRecording(onDiskRecording: OnDiskRecording) {
        currentRecording = onDiskRecording
        currentRecordingDuration = 0
        isPrepared = false

//        if(mediaPlayer.isPlaying) mediaPlayer.stop()
//        mediaPlayer.reset()

        recordingFileHandler.recordingAudioFilePath(onDiskRecording)?.let {
            try {
                playbackThread = PlaybackThread(
                    SampleIOHandler.getAudioFromPath(it),
                    this
                )
//                mediaPlayer.setDataSource(it)
                isPrepared = true
                externalPreparedListener?.onPrepared(null)
            } catch (e: IOException) {
                Timber.e(e, "Failed to initialize recording=$onDiskRecording")
                return
            }
//            mediaPlayer.prepareAsync()
        } ?: Timber.v("Could not initialize recording=$onDiskRecording")
    }

//    fun seekPosition(): Int = mediaPlayer.currentPosition
    fun seekPosition(): Int = currentProgress

//    fun pause() = mediaPlayer.pause()
    fun pause() {

}


    open fun play() {
        playbackThread?.startPlayback()
        playing = true
    }

//    fun isPlaying(): Boolean = isPrepared && mediaPlayer.isPlaying
    fun isPlaying(): Boolean = isPrepared && playbackThread?.playing() ?: false

    fun seek(millis: Int) {
//        mediaPlayer.seekTo(millis)
//        if(!mediaPlayer.isPlaying) mediaPlayer.start()
    }

    fun reset() = initializeWithRecording(currentRecording)
}