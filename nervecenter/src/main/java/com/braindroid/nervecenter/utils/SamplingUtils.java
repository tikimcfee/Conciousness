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
