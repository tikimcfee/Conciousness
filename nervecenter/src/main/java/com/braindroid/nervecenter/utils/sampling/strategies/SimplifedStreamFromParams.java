package com.braindroid.nervecenter.utils.sampling.strategies;

import android.graphics.Path;

import com.braindroid.nervecenter.utils.sampling.CompressedStreamParams;

public class SimplifedStreamFromParams {


    public static Path[] unscaledStreamPath(CompressedStreamParams streamParams) {
        final short[] data = streamParams.sampleSet;
        final float centerY = streamParams.centerPosition;

        Path[] resultPaths;
        resultPaths = new Path[1];
        resultPaths[0] = new Path();


        for(int i = 0; i < data.length; i ++) {
            float yPos = centerY - (((float)data[i]/ Short.MAX_VALUE) * centerY);
            resultPaths[0].lineTo(i, yPos);
        }

        return resultPaths;
    }


    public static Path[] simplifyStream(CompressedStreamParams streamParams) {
        final short[] data = streamParams.sampleSet;
        final int sampleSize = streamParams.scaledViewportWidth;
        final int sliceStart = streamParams.sliceStart;
        final int sliceEnd = streamParams.sliceEnd;
        final float centerY = streamParams.centerPosition;
        final int samplesToSkip = streamParams.samplesToSkip;

        final int groupSize = data.length / sampleSize;
        final short RESET_MAX = Short.MIN_VALUE;
        final short RESET_MIN = Short.MAX_VALUE;
        short min1, max1;

        Path[] resultPaths;

        if(streamParams.streamMode == CompressedStreamParams.STREAM_MODE_MIN_MAX) {
            resultPaths = new Path[2];
            resultPaths[0] = new Path();
            resultPaths[1] = new Path();
            resultPaths[0].moveTo(0, centerY);
            resultPaths[1].moveTo(0, centerY);
        } else {
            resultPaths = new Path[1];
            resultPaths[0] = new Path();
            resultPaths[0].moveTo(0, centerY);
        }

        float scaledIncrementer = 0;

        for(int i = sliceStart; i < sliceEnd; i += samplesToSkip) {
            final int groupStart = i * groupSize;
            final int calculatedNext = (i + 1) * groupSize;
            final int groupEnd = calculatedNext <= data.length ? calculatedNext : data.length;

            // We have calculated a start position for a nonexistant sample area; we must stop
            // todo: it'd be great to snap to a valid scale, but it's easier for now to just leave our result full of zeroes.
            if(groupStart >= groupEnd) {
                break;
            }

            min1 = RESET_MIN;
            max1 = RESET_MAX;

            for (int j = groupStart; j < groupEnd; j++) {
                final short sample1 = data[j];
                min1 = min1 <= sample1 ? min1 : sample1;
                max1 = max1 >= sample1 ? max1 : sample1;
            }

            float yPosMax1 = centerY - (((float)max1 / RESET_MIN) * centerY);
            float yPosMin1 = centerY - (((float)min1 / RESET_MIN) * centerY);


            switch (streamParams.streamMode) {
                case CompressedStreamParams.STREAM_MODE_MIN_MAX:
                    resultPaths[0].lineTo(scaledIncrementer, yPosMin1);
                    resultPaths[1].lineTo(scaledIncrementer, yPosMax1);
                    break;
                case CompressedStreamParams.STREAM_MODE_MAX_ONLY:
                    resultPaths[0].lineTo(scaledIncrementer, yPosMax1);
                    break;
                case CompressedStreamParams.STREAM_MODE_MIN_ONLY:
                    resultPaths[0].lineTo(scaledIncrementer, yPosMin1);
                    break;
                case CompressedStreamParams.STREAM_MODE_AVERAGE:
                    resultPaths[0].moveTo(scaledIncrementer, yPosMax1);
                    resultPaths[0].lineTo(scaledIncrementer, yPosMin1);
                    break;
            }

            scaledIncrementer += streamParams.scaleIncrement;
        }

        return resultPaths;
    }

}
