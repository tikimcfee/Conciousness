package com.braindroid.nervecenter.utils;

import android.os.Handler;
import android.os.HandlerThread;

import com.braindroid.nervecenter.domainRecordingTools.recordingSource.AudioSampleReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import timber.log.Timber;

//import okio.BufferedSource;
//import okio.Okio;
//import okio.Source;

/**
 *
 * http://www.geosci.usyd.edu.au/users/jboyden/vad/
 *
 * -- Initial reading
 * http://stackoverflow.com/questions/16320911/android-audiorecord-using-short-array-or-byte-array
 * http://www.labbookpages.co.uk/audio/javaWavFiles.html
 * http://stackoverflow.com/questions/4707994/android-audiorecord-questions
 * https://github.com/gast-lib/gast-lib
 * http://stackoverflow.com/questions/18109450/saving-large-short-array-android
 *
 *
 * -- End result
 * http://stackoverflow.com/questions/10506180/good-way-to-write-an-array-of-shorts-into-file-with-littleendian
 *
 */

public class SampleIOHandler implements AudioSampleReceiver {

    private volatile boolean STREAM_DEATH = false;
    private final HandlerThread handlerThread = new HandlerThread("DeviceRecordingSampleReceiver");
    private final Handler handler;

    private final FileChannel fileChannel;

    //region Sample Receiver


    public SampleIOHandler(FileOutputStream fileInputStream) {
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        this.fileChannel = fileInputStream.getChannel();
    }

    public void kill() {
        handlerThread.quit();
    }

    @Override
    public void onNewAudioSample(short[] sample) {
        if(STREAM_DEATH) {
            return;
        }

        final short[] audioCopy = Arrays.copyOf(sample, sample.length);

        handler.post(new Runnable() {
            @Override
            public void run() {
                ByteBuffer myByteBuffer = ByteBuffer.allocate(audioCopy.length * 2);
                myByteBuffer.order(ByteOrder.LITTLE_ENDIAN);

                ShortBuffer myShortBuffer = myByteBuffer.asShortBuffer();
                myShortBuffer.put(audioCopy);

                try {
                    fileChannel.write(myByteBuffer);
                } catch (IOException e) {
                    Timber.e(e, "Failed to write bytes. How sad.");
                    STREAM_DEATH = true;
                    try {
                        fileChannel.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onSamplingStopped() {
        if(STREAM_DEATH) {
            return;
        }

        Timber.v("Stopping audio writing...");

        try {
            fileChannel.close();
        } catch (IOException e) {
            Timber.e("Failed to flush / close output stream.");
        }

        Timber.v("Audio write finished.");
    }

    //endregion

    public static short[] getAudioFromPath(String path) throws IOException {
        File sourceFile = new File(path);
        if(!sourceFile.exists()) {
            return new short[]{0};
        }

        ByteBuffer myByteBuffer = ByteBuffer.allocate((int)sourceFile.length());
        myByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        FileChannel in = new FileInputStream(sourceFile).getChannel();
        in.read(myByteBuffer);
        myByteBuffer.flip();


        ShortBuffer myShortBuffer = myByteBuffer.asShortBuffer();
        short[] toReturn = new short[myShortBuffer.capacity()];
        myShortBuffer.get(toReturn);
        return toReturn;
    }

    public static short[] getAudioFromInputStream(FileInputStream inputStream) throws IOException {
//        Source fileSource = Okio.source(inputStream);
//        BufferedSource bufferedSource = Okio.buffer(fileSource);
//
//        byte[] data = bufferedSource.readByteArray();
        byte[] data = new byte[]{};

        ShortBuffer shortBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] samples = new short[shortBuffer.limit()];
        shortBuffer.get(samples);

        return samples;
    }

}
