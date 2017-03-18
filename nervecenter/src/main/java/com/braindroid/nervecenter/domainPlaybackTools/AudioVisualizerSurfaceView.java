package com.braindroid.nervecenter.domainPlaybackTools;

/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import java.util.LinkedList;

import timber.log.Timber;

/**
 * A view that displays audio data on the screen as a waveform.
 */
public class AudioVisualizerSurfaceView extends SurfaceView {

    // The number of buffer frames to keep around (for a nice fade-out visualization).
    private static final int HISTORY_SIZE = 1;

    // To make quieter sounds still show up well on the display, we use +/- 8192 as the amplitude
    // that reaches the top/bottom of the view instead of +/- 32767. Any samples that have
    // magnitude higher than this limit will simply be clipped during drawing.
//    private static final float MAX_AMPLITUDE_TO_DRAW = 32767.0f;
    private static final float MAX_AMPLITUDE_TO_DRAW = 8192.0f;

    // The queue that will hold historical audio data.
    private final LinkedList<byte[]> audioData;

    private final Paint linePaint;

    public AudioVisualizerSurfaceView(Context context) {
        this(context, null, 0);
    }

    public AudioVisualizerSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioVisualizerSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        audioData = new LinkedList<byte[]>();

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(0);
        linePaint.setAntiAlias(true);
    }

    public synchronized void clearBuffer() {
        audioData.clear();
        Timber.v("Cleared buffer");
    }

    /**
     * Updates the waveform view with a new "frame" of samples and renders it. The new frame gets
     * added to the front of the rendering queue, pushing the previous frames back, causing them to
     * be faded out visually.
     *
     * @param buffer the most recent buffer of audio samples
     */
    public synchronized void updateAudioData(byte[] buffer) {
//        byte[] newBuffer;

        // We want to keep a small amount of history in the view to provide a nice fading effect.
        // We use a linked list that we treat as a queue for this.
//        if (audioData.size() == HISTORY_SIZE) {
//            newBuffer = audioData.removeFirst();
//            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
//        } else {
//            newBuffer = buffer.clone();
//        }
//        audioData.addLast(newBuffer);

        audioData.clear();
        audioData.addLast(buffer);

        // Update the display.
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            drawWaveform(canvas);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Repaints the view's surface.
     *
     * @param canvas the {@link Canvas} object on which to draw
     */
    private void drawWaveform(Canvas canvas) {
        // Clear the screen each time because SurfaceView won't do this for us.
        canvas.drawColor(Color.BLACK);

        float width = getWidth();
        float height = getHeight();
        float centerY = height / 2;

        // We draw the history from oldest to newest so that the older audio data is further back
        // and darker than the most recent data.

//        int colorDelta = 255 / (HISTORY_SIZE + 1);
//        int brightness = colorDelta;
        linePaint.setColor(Color.argb(255, 128, 255, 192));

        for (byte[] buffer : audioData) {
//            linePaint.setColor(Color.argb(brightness, 128, 255, 192));

            float lastX = -1;
            float lastY = -1;

            // For efficiency, we don't draw all of the samples in the buffer, but only the ones
            // that align with pixel boundaries.
            for (int x = 0; x < width; x++) {
                int index = (int) ((x / width) * buffer.length);
                short sample = buffer[index];
                float y = (sample / MAX_AMPLITUDE_TO_DRAW) * centerY + centerY;

                if (lastX != -1) {
                    Timber.v("Draw line : %s %s -> %s %s :: sample = %s", lastX, lastY, x, y, sample);
                    canvas.drawLine(lastX, lastY, x, y, linePaint);
                }

                lastX = x;
                lastY = y;
            }

//            brightness += colorDelta;
        }
    }
}
