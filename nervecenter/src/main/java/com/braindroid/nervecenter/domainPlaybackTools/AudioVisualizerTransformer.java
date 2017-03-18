package com.braindroid.nervecenter.domainPlaybackTools;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import java.util.concurrent.ArrayBlockingQueue;

import timber.log.Timber;

public class AudioVisualizerTransformer implements Visualizer.OnDataCaptureListener {

    private static final int MILLIHERTZ_UPDATE_RATE = Visualizer.getMaxCaptureRate() / 2;

    private Visualizer visualizer;
    private AudioVisualizerSurfaceView audioVisualizerSurfaceView;

    private final ArrayBlockingQueue<byte[]> waveformQueue = new ArrayBlockingQueue<byte[]>(5);
    private Thread updateThread;
    private Thread readThread;

    private static class ReadRunnable implements Runnable, Visualizer.OnDataCaptureListener {
        private final ArrayBlockingQueue<byte[]> waveformQueue;

        private byte[] lastWaveform = null;

        public ReadRunnable(ArrayBlockingQueue<byte[]> waveformQueue) {
            this.waveformQueue = waveformQueue;
        }

        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
            lastWaveform = waveform;
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                if(lastWaveform != null) {
                    waveformQueue.offer(lastWaveform);
                    lastWaveform = null;
                }
                SystemClock.sleep(10);
            }
        }
    }

    private static class UpdateRunnable implements Runnable {

        private final ArrayBlockingQueue<byte[]> waveformQueue;
        private final AudioVisualizerSurfaceView surfaceView;

        public UpdateRunnable(ArrayBlockingQueue<byte[]> waveformQueue, AudioVisualizerSurfaceView surfaceView) {
            this.waveformQueue = waveformQueue;
            this.surfaceView = surfaceView;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    byte[] sample = waveformQueue.take();
                    Timber.v("took sample - %s", sample);
                    surfaceView.updateAudioData(sample);
                } catch (InterruptedException e) {
                    Timber.e(e, "Interrupted during take.");
                }
            }
        }
    }

//    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public AudioVisualizerTransformer(AudioVisualizerSurfaceView audioVisualizerSurfaceView) {
        this.audioVisualizerSurfaceView = audioVisualizerSurfaceView;
    }

    public void watchMediaPlayer(MediaPlayer mediaPlayer) {
        releasePrevious();

        UpdateRunnable updateRunnable = new UpdateRunnable(waveformQueue, audioVisualizerSurfaceView);
        updateThread = new Thread(updateRunnable);
        updateThread.start();

        ReadRunnable readRunnable = new ReadRunnable(waveformQueue);
        readThread = new Thread(readRunnable);
        readThread.start();

        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setDataCaptureListener(readRunnable, MILLIHERTZ_UPDATE_RATE, true, false);
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setEnabled(true);
    }

    private void releasePrevious() {
        if(visualizer != null) {
            visualizer.setEnabled(false);
            visualizer.release();
        }

        if(updateThread != null) {
            updateThread.interrupt();
        }

        if(readThread != null) {
            readThread.interrupt();
        }
    }

    public void stopWatchingAndRelease() {
        releasePrevious();
        visualizer = null;
        updateThread = null;
        readThread = null;
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, final byte[] waveform, int samplingRate) {
//        if(Looper.getMainLooper().equals(Looper.myLooper())) {
//            audioVisualizerSurfaceView.updateAudioData(waveform);
//        } else {
//            uiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    audioVisualizerSurfaceView.updateAudioData(waveform);
//                }
//            });
//        }


//        audioVisualizerSurfaceView.updateAudioData(waveform);
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

    }
}
