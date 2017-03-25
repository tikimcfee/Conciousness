package com.braindroid.nervecenter.visualization;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;

import com.braindroid.nervecenter.utils.AudioUtils;
import com.braindroid.nervecenter.utils.SamplingUtils;

public class WaveformCanvas {

    //region Audio bits
    private short[] currentAudioSampleSet;
    private int currentSampleSetLength;
    private int sampleRate, channels;

    private final Paint waveformFillPaint;
    private final Paint waveformStrokePaint;
    private final Paint canvasTextAxisPaint;
    private float xStep, centerY;

    public WaveformCanvas() {

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
            float y = centerY - ((sample / max) * centerY);
            waveformPath.lineTo(x, y);
        }

        // draw minimums
        for (int x = width - 1; x >= 0; x--) {
            short sample = extremes[x][1];
            float y = centerY - ((sample / max) * centerY);
            waveformPath.lineTo(x, y);
        }

        waveformPath.close();

        return waveformPath;
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
