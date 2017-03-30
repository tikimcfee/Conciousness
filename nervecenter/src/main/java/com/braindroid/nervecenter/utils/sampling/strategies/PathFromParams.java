package com.braindroid.nervecenter.utils.sampling.strategies;

import android.graphics.Path;

import com.braindroid.nervecenter.utils.sampling.StreamParams;

public class PathFromParams {

    public static PathHolder buildPaths(StreamParams streamParams) {

        final short[] data = streamParams.sampleSet;
        final int sampleSize = streamParams.scaledViewportWidth;
        final int sliceStart = streamParams.sliceStart;
        final int sliceEnd = streamParams.sliceEnd;

        final Path minPath = new Path();
        minPath.moveTo(0, streamParams.centerPosition);
        final Path maxPath = new Path();
        maxPath.moveTo(0, streamParams.centerPosition);

        final float centerY = streamParams.centerPosition;

        final int groupSize = data.length / sampleSize;
        final short RESET_MAX = Short.MIN_VALUE;
        final short RESET_MIN = Short.MAX_VALUE;
        short min1, min2, max1, max2;

        for(int i = sliceStart; i < sliceEnd; i++) {
            final int groupStart = i * groupSize;
            final int calculatedNext = (i + 1) * groupSize;
            final int groupEnd = calculatedNext <= data.length ? calculatedNext : data.length;

            min1 = min2 = RESET_MIN;
            max1 = max2 = RESET_MAX;

            for (int j = groupStart; j < groupEnd - 1; j++) {
                final short sample1 = data[j];
                final short sample2 = data[j + 1];
                min1 = min1 <= sample1 ? min1 : sample1;
                max1 = max1 >= sample1 ? max1 : sample1;
                min2 = min2 <= sample2 ? min2 : sample2;
                max2 = max2 >= sample2 ? max2 : sample2;
            }

            float yPosMin1 = centerY - (((float)min1 / RESET_MIN) * centerY);
            float yPosMax1 = centerY - (((float)max1 / RESET_MIN) * centerY);
            float yPosMin2 = centerY - (((float)min2 / RESET_MIN) * centerY);
            float yPosMax2 = centerY - (((float)max2 / RESET_MIN) * centerY);

            minPath.lineTo(i, yPosMin1);
            minPath.lineTo(i + 1, yPosMin2);
            maxPath.lineTo(i, yPosMax1);
            maxPath.lineTo(i + 1, yPosMax2);
        }

        return new PathHolder(minPath, maxPath);
    }
}
