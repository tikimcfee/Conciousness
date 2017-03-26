/*
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

package com.braindroid.nervecenter.utils;

import android.graphics.Path;

import com.braindroid.nervecenter.visualization.StreamParams;
import com.braindroid.nervecenter.visualization.StreamResults;

import java.util.Arrays;
import java.util.Locale;

import timber.log.Timber;

public final class SamplingUtils {

    public interface StreamReceiver {
        void onExtreme(int position, short min, short max);
    }

    public interface StreamPairReceiver {
        void onExtremePair(int position1, short min1, short max1, int position2, short min2, short max2);
    }

//    public static void streamExtremePairsFromSlice(short[] data, int sliceStart, int sliceEnd, StreamPairReceiver streamReceiver) {
////        int groupSize = sliceEnd - sliceStart;
//        short min1, min2, max1, max2;
//        min1 = min2 = Short.MAX_VALUE;
//        max1 = max2 = Short.MIN_VALUE;
//        for(int i = sliceStart; i < sliceEnd; i++) {
//            short sample1 = data[i];
//            short sample2 = data[i + 1];
//            min1 = (short) Math.min(min1, sample1);
//            max1 = (short) Math.max(max1, sample1);
//
//            min2 = (short) Math.min(min2, sample2);
//            max2 = (short) Math.max(max2, sample2);
//        }
//        streamReceiver.onExtremePair(sliceStart, min1, max1, sliceEnd, min2, max2);
//    }

    public static StreamResults streamFromParams_FLOATS(StreamParams streamParams) {

        final short[] data = streamParams.sampleSet;
        final int sampleSize = streamParams.scaledViewportWidth;
        final int sliceStart = streamParams.sliceStart;
        final int sliceEnd = streamParams.sliceEnd;
        final float centerY = streamParams.centerPosition;

        final int groupSize = data.length / sampleSize;
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
            final int groupEnd = calculatedNext <= data.length ? calculatedNext : data.length;

            // We have calculated a start position for a nonexistant sample area; we must stop
            // todo: it'd be great to snap to a valid scale, but it's easier for now to just leave our result full of zeroes.
            if(groupStart >= groupEnd) {
                break;
            }

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

    public static void streamFromParams(StreamParams streamParams) {

        final short[] data = streamParams.sampleSet;
        final int sampleSize = streamParams.scaledViewportWidth;
        final int sliceStart = streamParams.sliceStart;
        final int sliceEnd = streamParams.sliceEnd;
        final Path minPath = streamParams.minPath;
        final Path maxPath = streamParams.maxPath;
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
    }

    public static void streamExtremesFromSliceIntoPaths(short[] data, int sampleSize,
                                                        int sliceStart, int sliceEnd,
                                                        Path minPath, Path maxPath) {
        int groupSize = data.length / sampleSize;

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

            minPath.lineTo(i, min1);
            minPath.lineTo(i + 1, min2);
            maxPath.lineTo(i, max1);
            maxPath.lineTo(i + 1, max2);
        }
    }

    public static void streamExtremePairsFromSlice_HL(short[] data, int sampleSize,
                                                      int sliceStart, int sliceEnd,
                                                      StreamPairReceiver streamReceiver) {
        int groupSize = data.length / sampleSize;

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

            streamReceiver.onExtremePair(i, min1, max1, i + 1, min2, max2);
        }
    }

    public static void streamExtremePairsFromSlice(short[] data, int sampleSize, int sliceStart, int sliceEnd, StreamPairReceiver streamReceiver) {
        int groupSize = data.length / sampleSize;
        for(int i = sliceStart; i < sliceEnd; i++) {
            int groupStart = i * groupSize;
            int groupEnd = Math.min((i + 1) * groupSize, data.length);

            short min1, min2, max1, max2;
            min1 = min2 = Short.MAX_VALUE;
            max1 = max2 = Short.MIN_VALUE;
            for (int j = groupStart; j < groupEnd - 1; j++) {
                short sample1 = data[j];
                short sample2 = data[j + 1];
                min1 = (short) Math.min(min1, sample1);
                max1 = (short) Math.max(max1, sample1);

                min2 = (short) Math.min(min2, sample2);
                max2 = (short) Math.max(max2, sample2);
            }

            streamReceiver.onExtremePair(i, min1, max1, i + 1, min2, max2);
        }
    }

    public static void streamExtremePairs(short[] data, int sampleSize, StreamPairReceiver streamReceiver) {
        int groupSize = data.length / sampleSize;
        for(int i = 0; i < sampleSize; i++) {
            int groupStart = i * groupSize;
            int groupEnd = Math.min((i + 1) * groupSize, data.length);

            short min1, min2, max1, max2;
            min1 = min2 = Short.MAX_VALUE;
            max1 = max2 = Short.MIN_VALUE;
            for (int j = groupStart; j < groupEnd - 1; j++) {
                short sample1 = data[j];
                short sample2 = data[j + 1];
                min1 = (short) Math.min(min1, sample1);
                max1 = (short) Math.max(max1, sample1);

                min2 = (short) Math.min(min2, sample2);
                max2 = (short) Math.max(max2, sample2);
            }

            streamReceiver.onExtremePair(i, min1, max1, i + 1, min2, max2);
        }
    }

    public static void streamExtremes(short[] data, int sampleSize, StreamReceiver streamReceiver) {
        int groupSize = data.length / sampleSize;
        for(int i = 0; i < sampleSize; i++) {
            int groupStart = i * groupSize;
            int groupEnd = Math.min((i + 1) * groupSize, data.length);

            short min = Short.MAX_VALUE, max = Short.MIN_VALUE;
            for (int j = groupStart; j < groupEnd; j++) {
                short sample = data[j];
                min = (short) Math.min(min, sample);
                max = (short) Math.max(max, sample);
            }

            streamReceiver.onExtreme(i, min, max);
        }
    }

    public static short[][] getExtremes(short[] data, int sampleSize) {
        short[][] newData = new short[sampleSize][];
        int groupSize = data.length / sampleSize;

        for (int i = 0; i < sampleSize; i++) {
            short[] group = Arrays.copyOfRange(data, i * groupSize,
                    Math.min((i + 1) * groupSize, data.length));

            // Find min & max values
            short min = Short.MAX_VALUE, max = Short.MIN_VALUE;
            for (short a : group) {
                min = (short) Math.min(min, a);
                max = (short) Math.max(max, a);
            }
            newData[i] = new short[] { max, min };
        }

        return newData;
    }

    public static void printSample(short[] sample, int columns) {
        StringBuilder builder = new StringBuilder("");
        for(int i = 0; i < sample.length; i++) {
            builder.append(
                    String.format(Locale.ENGLISH, "%7d", sample[i])
            ).append(" ");
            if(i > 0 && i % columns == 0) {
                builder.append("\n");
            }
        }
        Timber.v("---- Short Data [%s] ----\n%s", sample, builder.toString());
    }
}
