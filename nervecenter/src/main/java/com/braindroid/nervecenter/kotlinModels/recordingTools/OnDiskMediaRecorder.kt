package com.braindroid.nervecenter.kotlinModels.recordingTools

import android.media.MediaRecorder
import android.media.MediaRecorder.AudioEncoder.AAC
import android.media.MediaRecorder.AudioSource.MIC
import android.media.MediaRecorder.OutputFormat.MPEG_4
import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording
import com.braindroid.nervecenter.kotlinModels.utils.OnDiskRecordingFileHandler
import timber.log.Timber
import java.io.IOException

class OnDiskMediaRecorder(val fileHandler: OnDiskRecordingFileHandler) {

    /* Audio format parameter defaults */
    private var audioSource = MIC
    private var outputFormat = MPEG_4
    private var audioEncoder = AAC
    private var samplingRate = 44100
    private var encodingRate = 96000

    /* MediaRecorder instance and external listeners */
    private val mediaRecorder = MediaRecorder()
    var errorListener: MediaRecorder.OnErrorListener? = null
    var infoListener: MediaRecorder.OnInfoListener? = null

    /* Recorder state */
    var isRecording = false
        private set

    var currentRecording: OnDiskRecording? = null


    init {
        mediaRecorder.setOnErrorListener { mr, what, extra ->
            Timber.e("error from ($mr, $this) --> what:$what extra:$extra")
            errorListener?.onError(mr, what, extra)
        }

        mediaRecorder.setOnInfoListener { mr, what, extra ->
            Timber.v("info from ($mr, $this) --> what:$what extra:$extra")
            infoListener?.onInfo(mr, what, extra)
        }
    }

    private fun reinit() {
        mediaRecorder.reset()

        mediaRecorder.setAudioSource(audioSource)
        mediaRecorder.setOutputFormat(outputFormat)
        mediaRecorder.setAudioEncoder(audioEncoder)
        mediaRecorder.setAudioSamplingRate(samplingRate)
        mediaRecorder.setAudioEncodingBitRate(encodingRate)
    }

    fun prepare(onDiskRecording: OnDiskRecording): Boolean {
        reinit()

        currentRecording = onDiskRecording
        return fileHandler.createAudioOutStream(onDiskRecording)?.let {
            try {
                mediaRecorder.setOutputFile(it.fd)
                mediaRecorder.prepare()
                return true
            } catch (ise: IllegalStateException) {
                Timber.e(ise, "Illegal state exception for $mediaRecorder, $this")
            } catch (ioe: IOException) {
                Timber.e(ioe, "IOException for $mediaRecorder, $this")
            }

            false
        }?: false
    }

    fun start() {
        if(isRecording) {
            Timber.w("Already recording")
            return
        }

        try {
            mediaRecorder.start()
            isRecording = true
        } catch (ise: IllegalStateException) {
            Timber.e(ise, "Could not start media recorder; nothing is being written to ${currentRecording}.")
        }
    }

    fun stop() {
        if(!isRecording) {
            Timber.e("Not recording")
            return
        }

        try {
            mediaRecorder.stop()
        } catch (e: IllegalStateException) {
            Timber.e(e, "Could not stop media recorder; recording may be corrupt or unreadable ${currentRecording}.")
        }

        isRecording = false
    }

}