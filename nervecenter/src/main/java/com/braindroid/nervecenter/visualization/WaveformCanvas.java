package com.braindroid.nervecenter.visualization;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.text.TextPaint;

import com.braindroid.nervecenter.utils.AudioUtils;
import com.braindroid.nervecenter.utils.SamplingUtils;

import timber.log.Timber;

public class WaveformCanvas {

    public interface CanvasSupplier {
        Canvas acquireCanvas();
        void postCanvas(Canvas canvas);
    }

    //region Audio bits
    private short[] currentAudioSampleSet;
    private int currentSampleSetLength;
    private int sampleRate, channels;
    //endregion

    //region Drawing
    private final Paint waveformFillPaint;
    private final Paint waveformStrokePaint;
    private final Paint canvasTextAxisPaint;
    private float xStep, centerY;
    //endregion

    //region Deps
    private final CanvasSupplier canvasSupplier;
    //endregion

    private void DEBUG() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    // Debug stuff
                    SystemClock.sleep(2000);
                }
            }
        }).start();
    }

    public WaveformCanvas(CanvasSupplier canvasSupplier) {
        this.canvasSupplier = canvasSupplier;

        canvasTextAxisPaint = new TextPaint();
        canvasTextAxisPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvasTextAxisPaint.setTextAlign(Paint.Align.CENTER);
        canvasTextAxisPaint.setColor(Color.BLACK);

        waveformStrokePaint = new Paint();
        waveformStrokePaint.setColor(Color.BLACK);
        waveformStrokePaint.setStyle(Paint.Style.STROKE);
        waveformStrokePaint.setStrokeWidth(2);
        waveformStrokePaint.setAntiAlias(true);

        waveformFillPaint = new Paint();
        waveformFillPaint.setStyle(Paint.Style.FILL);
        waveformFillPaint.setAntiAlias(true);
        waveformFillPaint.setColor(Color.LTGRAY);

        DEBUG();
    }

    public void setAudioData(short[] audioSampleSet, int sampleRate, int channels) {
        this.currentAudioSampleSet = audioSampleSet;
        this.sampleRate = sampleRate;
        this.channels = channels;

        calculateAudioLength();
    }

    private void calculateAudioLength() {
        if (currentAudioSampleSet == null || sampleRate == 0 || channels == 0)
            return;

        currentSampleSetLength = AudioUtils.calculateAudioLength(
                currentAudioSampleSet.length, sampleRate, channels);
    }

    public void updateCanvas(Canvas targetCanvas) {
        createPlaybackWaveform(targetCanvas.getWidth(), targetCanvas.getHeight(), targetCanvas);
    }

    private void createPlaybackWaveform(int width, int height, Canvas targetCanvas) {
        if (width <= 0 || height <= 0 || currentAudioSampleSet == null)
            return;

        Path mWaveform = drawPlaybackWaveform(width, height);
        targetCanvas.drawPath(mWaveform, waveformFillPaint);
        targetCanvas.drawPath(mWaveform, waveformStrokePaint);

        drawAxis(width, targetCanvas);

    }

    private void rawDrawSlice(int sliceStart, int sliceEnd, int height, int scaledViewPortWidth, final Canvas targetCanvas) {
        xStep = scaledViewPortWidth / (currentSampleSetLength * 1.0f);
        final float centerY = height / 2f;

        final SamplingUtils.StreamPairReceiver receiver = new SamplingUtils.StreamPairReceiver() {
            float lastMin = -1;
            float lastMax = -1;
            int lastPos = -1;

            @Override
            public void onExtremePair(int position1, short min1, short max1, int position2, short min2, short max2) {
                float yPosMin1 = centerY - scaledSample(min1, Short.MAX_VALUE, centerY);
                float yPosMax1 = centerY - scaledSample(max1, Short.MAX_VALUE, centerY);
                float yPosMin2 = centerY - scaledSample(min2, Short.MAX_VALUE, centerY);
                float yPosMax2 = centerY - scaledSample(max2, Short.MAX_VALUE, centerY);

                if(lastPos != -1) {
                    targetCanvas.drawLine(lastPos, lastMin, position1, yPosMin1, waveformStrokePaint);
                    targetCanvas.drawLine(lastPos, lastMax, position1, yPosMax1, waveformStrokePaint);
                }

                targetCanvas.drawLine(position1, yPosMin1, position2, yPosMin2, waveformStrokePaint);
                targetCanvas.drawLine(position1, yPosMax1, position2, yPosMax2, waveformStrokePaint);
                lastMin = yPosMin2;
                lastMax = yPosMax2;
                lastPos = position2;
            }
        };

        SamplingUtils.streamExtremePairsFromSlice(currentAudioSampleSet, scaledViewPortWidth,
                sliceStart, sliceEnd, receiver);
    }

    private void rawDraw(int width, int height, final Canvas targetCanvas) {
        xStep = width / (currentSampleSetLength * 1.0f);
        final float centerY = height / 2f;

        SamplingUtils.streamExtremePairs(currentAudioSampleSet, width, new SamplingUtils.StreamPairReceiver() {
            float lastMin = -1;
            float lastMax = -1;
            int lastPos = -1;

            @Override
            public void onExtremePair(int position1, short min1, short max1, int position2, short min2, short max2) {
                float yPosMin1 = centerY - scaledSample(min1, Short.MAX_VALUE, centerY);
                float yPosMax1 = centerY - scaledSample(max1, Short.MAX_VALUE, centerY);
                float yPosMin2 = centerY - scaledSample(min2, Short.MAX_VALUE, centerY);
                float yPosMax2 = centerY - scaledSample(max2, Short.MAX_VALUE, centerY);

                if(lastPos != -1) {
                    targetCanvas.drawLine(lastPos, lastMin, position1, yPosMin1, waveformStrokePaint);
                    targetCanvas.drawLine(lastPos, lastMax, position1, yPosMax1, waveformStrokePaint);
                }

                targetCanvas.drawLine(position1, yPosMin1, position2, yPosMin2, waveformStrokePaint);
                targetCanvas.drawLine(position1, yPosMax1, position2, yPosMax2, waveformStrokePaint);
                lastMin = yPosMin2;
                lastMax = yPosMax2;
                lastPos = position2;
            }
        });
    }

    private Path drawPlaybackWaveform(int width, int height) {
        xStep = width / (currentSampleSetLength * 1.0f);

        Path waveformPath = new Path();
        float centerY = height / 2f;
        float max = Short.MAX_VALUE;

        short[][] extremes = SamplingUtils.getExtremes(currentAudioSampleSet, width);

        waveformPath.moveTo(0, centerY);

        // draw maximums
        for (int x = 0; x < width; x++) {
            short sample = extremes[x][0];
            float y = centerY - scaledSample(sample, max, centerY);
            waveformPath.lineTo(x, y);
        }

        // draw minimums
        for (int x = width - 1; x >= 0; x--) {
            short sample = extremes[x][1];
            float y = centerY - scaledSample(sample, max, centerY);
            waveformPath.lineTo(x, y);
        }

        waveformPath.close();

        return waveformPath;
    }

    private float scaledSample(float sample, float max, float val) {
        return (sample / max) * val;
    }

    private void drawAxis(int width, Canvas targetCanvas) {
        int seconds = currentSampleSetLength / 1000;
        float xStep = width / (currentSampleSetLength / 1000f);
        float textHeight = canvasTextAxisPaint.getTextSize();
        float textWidth = canvasTextAxisPaint.measureText("10.00");
        int secondStep = (int)(textWidth * seconds * 2) / width;
        secondStep = Math.max(secondStep, 1);
        for (float i = 0; i <= seconds; i += secondStep) {
            targetCanvas.drawText(String.format("%.2f", i), i * xStep, textHeight, canvasTextAxisPaint);
        }
    }

}
