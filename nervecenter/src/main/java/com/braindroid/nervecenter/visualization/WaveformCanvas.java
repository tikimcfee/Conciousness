package com.braindroid.nervecenter.visualization;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.text.TextPaint;

import com.braindroid.nervecenter.utils.AudioUtils;
import com.braindroid.nervecenter.utils.sampling.CompressedStreamParams;
import com.braindroid.nervecenter.utils.sampling.SamplingUtils;
import com.braindroid.nervecenter.utils.sampling.StreamPairReceiver;
import com.braindroid.nervecenter.utils.sampling.StreamParams;
import com.braindroid.nervecenter.utils.sampling.StreamResults;
import com.braindroid.nervecenter.utils.sampling.strategies.CompleteStreamFromParams;
import com.braindroid.nervecenter.utils.sampling.strategies.SimplifedStreamFromParams;
import com.braindroid.nervecenter.utils.sampling.strategies.SlicedStreamFromParams;
import com.braindroid.nervecenter.visualization.interactive.MatrixUtils;

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
    private int defaultStrokeWidth = 2;

    private final Paint canvasTextAxisPaint;

    private float xStep, centerY;
    //endregion

    //region Deps
    private final CanvasSupplier canvasSupplier;
    //endregion

    private final HandlerThread handlerThread = new HandlerThread("WaveformCanvasHandlerThread");
    private Handler handler;

    private void DEBUG(final Runnable runnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    // Debug stuff
                    if(runnable != null) {
                        runnable.run();
                        Thread.currentThread().interrupt();
                    } else {
                        SystemClock.sleep(2000);
                    }
                }
            }
        }).start();
    }

    public void RUN_TEST() {
        DEBUG(new Runnable() {
            @Override
            public void run() {
                int counter = 0;
                int steps = 16;
                boolean stop = false;
                float scale = 1.4f;

                while(!stop) {
                    Canvas canvas = canvasSupplier.acquireCanvas();
                    canvas.drawColor(Color.parseColor("#009999"));

                    int regularWidth = canvas.getWidth();
                    int scaledWidth = Math.round(regularWidth * scale);
                    int delta = scaledWidth - regularWidth;

                    canvas.translate(-counter, 0);
                    simplifiedDrawPath(counter, counter + regularWidth, canvas.getHeight(), scaledWidth, canvas);

                    canvasSupplier.postCanvas(canvas);

                    stop = counter >= delta;
                    counter += steps;
//                    SystemClock.sleep(34);
                }
            }
        });
    }

    public void RUN_TEST_PAN_ZOOM(final Matrix currentTransform) {
        if(handler == null) {
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = canvasSupplier.acquireCanvas();

                waveformStrokePaint.setStrokeWidth(defaultStrokeWidth / MatrixUtils.getScaledX(currentTransform));
                int scaledWidth = MatrixUtils.getScaledWidth(currentTransform, canvas.getWidth());

                canvas.drawColor(Color.parseColor("#009999"));
                fullWidthPathDraw_testAllLines(canvas.getHeight(), scaledWidth, canvas, currentTransform);

                canvasSupplier.postCanvas(canvas);
            }
        });
    }

    public void TEST_DRAW_TRANSLATION(final int translation) {
        if(handler == null) {
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                float scale = 4f;

                Canvas canvas = canvasSupplier.acquireCanvas();

                canvas.drawColor(Color.parseColor("#009999"));

                int regularWidth = canvas.getWidth();
                int scaledWidth = Math.round(regularWidth * scale);

//                if(translation <= -scaledWidth) {
//                    canvasSupplier.postCanvas(canvas);
//                    return;
//                }

//                canvas.translate(translation / scale, 0);
                simplifiedDrawPath(-translation, -translation + regularWidth, canvas.getHeight(), scaledWidth, canvas);

                canvasSupplier.postCanvas(canvas);
            }
        });
    }

    public void clearCaches() {
        lastPath = null;
        CACHED_RESULTS = null;
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
        waveformStrokePaint.setStrokeWidth(defaultStrokeWidth);
        waveformStrokePaint.setAntiAlias(false);

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

    private float scaledSample(float sample, float max, float val) {
        return (sample / max) * val;
    }

    private Path[] lastpaths;
    private void fullWidthPathDraw_testAllLines(int height, int scaledViewPortWidth, final Canvas targetCanvas, Matrix sourceTransform) {
        if(lastpaths != null) {
            for(int i = 0; i < lastpaths.length; i++) {
                Path path = lastpaths[i];
                path.transform(sourceTransform);
                targetCanvas.drawPath(path, waveformStrokePaint);
            }
            return;
        }

//        xStep = scaledViewPortWidth / (currentSampleSetLength * 1.0f);
        final float centerY = height / 2f;

        CompressedStreamParams streamParams = new CompressedStreamParams(
                currentAudioSampleSet, scaledViewPortWidth,
                0, scaledViewPortWidth,
                centerY);
        streamParams.samplesToSkip = 1;
        streamParams.scaleIncrement = 1 / MatrixUtils.getScaledX(sourceTransform);
        streamParams.streamMode = CompressedStreamParams.STREAM_MODE_MIN_MAX;

        Path[] simplifiedStreams = lastpaths = SimplifedStreamFromParams.unscaledStreamPath(streamParams);
        for(int i = 0; i < simplifiedStreams.length; i++) {
            Path path = simplifiedStreams[i];
            path.transform(sourceTransform);
            targetCanvas.drawPath(path, waveformStrokePaint);
        }
    }

//    private Path fullWidthPath = null;
    private void fullWidthPathDraw(int height, int scaledViewPortWidth, final Canvas targetCanvas, Matrix sourceTransform) {
//        if(fullWidthPath != null) {
//            targetCanvas.drawPath(fullWidthPath, waveformStrokePaint);
//            return;
//        }

        xStep = scaledViewPortWidth / (currentSampleSetLength * 1.0f);
        final float centerY = height / 2f;

        CompressedStreamParams streamParams = new CompressedStreamParams(
                currentAudioSampleSet, scaledViewPortWidth,
                0, scaledViewPortWidth,
                centerY);
        streamParams.samplesToSkip = 1;
        streamParams.scaleIncrement = 1 / MatrixUtils.getScaledX(sourceTransform);
        streamParams.streamMode = CompressedStreamParams.STREAM_MODE_MIN_MAX;

        Path[] simplifiedStreams = SimplifedStreamFromParams.simplifyStream(streamParams);
        for(int i = 0; i < simplifiedStreams.length; i++) {
            Path path = simplifiedStreams[i];
            path.transform(sourceTransform);
            targetCanvas.drawPath(path, waveformStrokePaint);
        }
    }

    private Path lastPath = null;
    private void simplifiedDrawPath(int sliceStart, int sliceEnd, int height, int scaledViewPortWidth, final Canvas targetCanvas) {
        if(lastPath != null) {
            Path newPath = new Path();
            lastPath.offset(-sliceStart, 0, newPath);
            targetCanvas.drawPath(newPath, waveformStrokePaint);
            return;
        }

        xStep = scaledViewPortWidth / (currentSampleSetLength * 1.0f);
        final float centerY = height / 2f;

        CompressedStreamParams streamParams = new CompressedStreamParams(
                currentAudioSampleSet, scaledViewPortWidth,
                0, scaledViewPortWidth,
                centerY);
        streamParams.samplesToSkip = 4 * 2;

        Path[] simplifiedStreams = SimplifedStreamFromParams.simplifyStream(streamParams);
        lastPath = simplifiedStreams[0];
        targetCanvas.drawPath(lastPath, waveformStrokePaint);
    }

    private StreamResults CACHED_RESULTS = null;
    private void streamPathDrawWithCache(int sliceStart, int sliceEnd, int height, int scaledViewPortWidth, final Canvas targetCanvas) {
        if(CACHED_RESULTS != null) {

            final int pointCount = sliceEnd - sliceStart;
            final int interspersedPoints = pointCount - 1; // we have 1 less interspersed point than we do points
            final int coordinateCount = pointCount * 4 + interspersedPoints * 4;

            targetCanvas.drawLines(CACHED_RESULTS.finalPointsMin, sliceStart, coordinateCount - 1, waveformStrokePaint);
            targetCanvas.drawLines(CACHED_RESULTS.finalPointsMax, sliceStart, coordinateCount - 1, waveformStrokePaint);

//            StreamResults results = SamplingUtils.newResultsFromSlice(CACHED_RESULTS, sliceStart, sliceEnd);
//            targetCanvas.drawLines(results.finalPointsMin, waveformStrokePaint);
//            targetCanvas.drawLines(results.finalPointsMax, waveformStrokePaint);
            return;
        }

        xStep = scaledViewPortWidth / (currentSampleSetLength * 1.0f);
        final float centerY = height / 2f;

        StreamParams streamParams = new StreamParams(
                currentAudioSampleSet, scaledViewPortWidth,
                sliceStart, sliceEnd,
                centerY);

        CACHED_RESULTS = CompleteStreamFromParams.stream(streamParams);

        targetCanvas.drawLines(CACHED_RESULTS.finalPointsMin, waveformStrokePaint);
        targetCanvas.drawLines(CACHED_RESULTS.finalPointsMax, waveformStrokePaint);
    }

    private void streamPathDraw(int sliceStart, int sliceEnd, int height, int scaledViewPortWidth, final Canvas targetCanvas) {
        xStep = scaledViewPortWidth / (currentSampleSetLength * 1.0f);
        final float centerY = height / 2f;

        final Path streamedMinPath = new Path();
        final Path streamedMaxPath = new Path();
        streamedMinPath.moveTo(0, centerY);
        streamedMaxPath.moveTo(0, centerY);

//        final SamplingUtils.StreamPairReceiver receiver = new SamplingUtils.StreamPairReceiver() {
//            @Override
//            public void onExtremePair(int position1, short min1, short max1, int position2, short min2, short max2) {
//                float yPosMin1 = centerY - scaledSample(min1, Short.MAX_VALUE, centerY);
//                float yPosMax1 = centerY - scaledSample(max1, Short.MAX_VALUE, centerY);
//                float yPosMin2 = centerY - scaledSample(min2, Short.MAX_VALUE, centerY);
//                float yPosMax2 = centerY - scaledSample(max2, Short.MAX_VALUE, centerY);
//
//                streamedMinPath.lineTo(position1, yPosMin1);
//                streamedMinPath.lineTo(position2, yPosMin2);
//                streamedMaxPath.lineTo(position1, yPosMax1);
//                streamedMaxPath.lineTo(position2, yPosMax2);
//            }
//        };

        StreamParams streamParams = new StreamParams(
                currentAudioSampleSet, scaledViewPortWidth,
                sliceStart, sliceEnd,
                centerY);

//        SamplingUtils.streamExtremesFromSliceIntoPaths(
//                currentAudioSampleSet, scaledViewPortWidth,
//                sliceStart, sliceEnd,
//                streamedMinPath, streamedMaxPath);
        StreamResults streamResults = SlicedStreamFromParams.stream(streamParams);

//        targetCanvas.drawPath(streamedMinPath, waveformStrokePaint);
//        targetCanvas.drawPath(streamedMaxPath, waveformStrokePaint);

        targetCanvas.drawLines(streamResults.finalPointsMin, waveformStrokePaint);
        targetCanvas.drawLines(streamResults.finalPointsMax, waveformStrokePaint);
    }

    private void rawDrawSlice(int sliceStart, int sliceEnd, int height, int scaledViewPortWidth, final Canvas targetCanvas) {
        xStep = scaledViewPortWidth / (currentSampleSetLength * 1.0f);
        final float centerY = height / 2f;

        final StreamPairReceiver receiver = new StreamPairReceiver() {
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

        SamplingUtils.streamExtremePairs(currentAudioSampleSet, width, new StreamPairReceiver() {
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
