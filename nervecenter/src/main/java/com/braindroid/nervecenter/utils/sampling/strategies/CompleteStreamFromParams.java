package com.braindroid.nervecenter.utils.sampling.strategies;

import com.braindroid.nervecenter.utils.sampling.StreamParams;
import com.braindroid.nervecenter.utils.sampling.StreamResults;

public class CompleteStreamFromParams {

    public static StreamResults stream(StreamParams streamParams) {

        final short[] audioData = streamParams.sampleSet;
        final int widthOfSingleViewSlice = streamParams.scaledViewportWidth;
        final int sliceStart = 0;
        final int sliceEnd = widthOfSingleViewSlice;
        final float centerY = streamParams.centerPosition;

        final int groupSize = audioData.length / widthOfSingleViewSlice;
        final short RESET_MAX = Short.MIN_VALUE;
        final short RESET_MIN = Short.MAX_VALUE;
        short min1, min2, max1, max2;


        final int pointCount = sliceEnd - sliceStart;
        final int interspersedPoints = pointCount - 1; // we have 1 less interspersed point than we do points
        final int coordinateCount = pointCount * 4 + interspersedPoints * 4;

        final float[] finalPointsMin = new float[coordinateCount];
        final float[] finalPointsMax = new float[coordinateCount];

        int zeroBasedArrayPos = 0;

        int lastStart = -1;
        float lastMin = -1;
        float lastMax = -1;

        for(int i = sliceStart; i < sliceEnd; i++) {
            final int groupStart = i * groupSize;
            final int calculatedNext = (i + 1) * groupSize;
            final int groupEnd = calculatedNext <= audioData.length ? calculatedNext : audioData.length;

            // We have calculated a start position for a nonexistant sample area; we must stop
            // todo: it'd be great to snap to a valid scale, but it's easier for now to just leave our result full of zeroes.
            if(groupStart >= groupEnd) {
                break;
            }

            min1 = min2 = RESET_MIN;
            max1 = max2 = RESET_MAX;

            for (int j = groupStart; j < groupEnd - 1; j++) {
                final short sample1 = audioData[j];
                final short sample2 = audioData[j + 1];
                min1 = min1 <= sample1 ? min1 : sample1;
                max1 = max1 >= sample1 ? max1 : sample1;
                min2 = min2 <= sample2 ? min2 : sample2;
                max2 = max2 >= sample2 ? max2 : sample2;
            }


            float yPosMin1 = centerY - (((float)min1 / RESET_MIN) * centerY);
            float yPosMax1 = centerY - (((float)max1 / RESET_MIN) * centerY);
            float yPosMin2 = centerY - (((float)min2 / RESET_MIN) * centerY);
            float yPosMax2 = centerY - (((float)max2 / RESET_MIN) * centerY);


            int pointStart = zeroBasedArrayPos;
            zeroBasedArrayPos += 4;

            // TODO: note, we can't do the below; we don't have the next computed value. instead, store the LAST value, and insert it
            // x x x x x x x x x x x x x x x x x x x x
            // for every point we draw, we need to add the x1/y1 to the list of coordinates a second time
            // to 'connect' the x1/y1 to the x2/y2.
            // x x x x x x x x x x x x x x x x x x x x

            // for every pair of coordinates draw, store the LAST pair (and its position)
            // if we have a LAST pair, then write to the array that LAST pair, and this new pair's FIRST coordinate.
            // THEN, we need to offset where we WERE going to write the new coordinates by 4, and start there
            // -- repeat

            // l1       == (x0, y0), (x1, y1)
            //  li12    == (x1, y1), (x2, y2)
            // l2       == (x2, y2), (x3, y3)
            //  li23    == (x3, y3), (x4, y4)
            // l3       == (x4, y4), (x5, y5)
            //  li34    == (x5, y5), (x6, y6)
            // l4       == (x7, y7), (x8, y8)

            if(lastStart != -1) {
                finalPointsMin[pointStart   ] = lastStart;
                finalPointsMin[pointStart + 1] = lastMin;
                finalPointsMin[pointStart + 2] = lastStart + 1;
                finalPointsMin[pointStart + 3] = yPosMin1;

                finalPointsMax[pointStart   ] = lastStart;
                finalPointsMax[pointStart + 1] = lastMax;
                finalPointsMax[pointStart + 2] = lastStart + 1;
                finalPointsMax[pointStart + 3] = yPosMax1;

                pointStart += 4;
            }

            lastStart = i + 1;
            lastMin = yPosMin2;
            lastMax = yPosMax2;

            finalPointsMin[pointStart   ] = i;
            finalPointsMin[pointStart + 1] = yPosMin1;
            finalPointsMin[pointStart + 2] = i + 1;
            finalPointsMin[pointStart + 3] = yPosMin2;

            finalPointsMax[pointStart   ] = i;
            finalPointsMax[pointStart + 1] = yPosMax1;
            finalPointsMax[pointStart + 2] = i + 1;
            finalPointsMax[pointStart + 3] = yPosMax2;
        }

        return new StreamResults(finalPointsMin, finalPointsMax);
    }

}
